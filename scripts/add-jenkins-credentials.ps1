<#
.SYNOPSIS
    Adds Docker Hub and PostgreSQL credentials to Jenkins via REST API.
.DESCRIPTION
    Prompts interactively for Jenkins user/token and credentials,
    keeps secrets in secure strings, converts them in memory only during POST,
    and never logs or persists secrets to disk.
#>

[CmdletBinding()]
param(
    [string]$JenkinsUrl = "http://localhost:8080"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Helper for friendly error messages without echoing secrets
function Handle-JenkinsException ($ex, [string]$context) {
    if ($ex.Exception -and $ex.Exception.Response) {
        $status = [int]$ex.Exception.Response.StatusCode
        if ($status -eq 401) {
            Write-Error "[$context] HTTP 401 Unauthorized: Invalid Jenkins username or API token."
            exit 1
        }
        if ($status -eq 403) {
            Write-Error "[$context] HTTP 403 Forbidden: Insufficient permissions or CSRF protection check failed."
            exit 1
        }
        Write-Error "[$context] HTTP $status error returned by Jenkins."
        exit 1
    }
    if ($ex.Exception.InnerException -is [System.Net.Sockets.SocketException] -or $ex.Exception.Message -match "actively refused|unable to connect|connect") {
        Write-Error "[$context] Connection refused: Unable to connect to Jenkins at $JenkinsUrl. Please verify Jenkins is running."
        exit 1
    }
    Write-Error "[$context] Request failed: $($ex.Exception.Message)"
    exit 1
}

# 1. Interactive prompts
Write-Host "=== Jenkins Credentials Setup ===" -ForegroundColor Cyan
$jenkinsUser = Read-Host -Prompt "Enter Jenkins username"
if ([string]::IsNullOrWhiteSpace($jenkinsUser)) {
    Write-Error "Jenkins username cannot be empty."
    exit 1
}

$jenkinsTokenSec = Read-Host -Prompt "Enter Jenkins API token" -AsSecureString

# 2. Interactive prompt for secrets
$dhTokenSec = Read-Host -Prompt "Enter Docker Hub access token" -AsSecureString
$dbPassSec  = Read-Host -Prompt "Enter PostgreSQL database password" -AsSecureString

# Prepare Basic Auth header
$jenkinsTokenPlain = [System.Net.NetworkCredential]::new('', $jenkinsTokenSec).Password
$authBytes = [System.Text.Encoding]::UTF8.GetBytes("${jenkinsUser}:${jenkinsTokenPlain}")
$authBase64 = [Convert]::ToBase64String($authBytes)
$jenkinsTokenPlain = $null

$headers = @{
    "Authorization" = "Basic $authBase64"
}

# Check CSRF Crumb if enabled
try {
    $crumbResponse = Invoke-RestMethod -Uri "$JenkinsUrl/crumbIssuer/api/json" -Headers $headers -Method Get -ErrorAction SilentlyContinue
    if ($crumbResponse -and $crumbResponse.crumbRequestField -and $crumbResponse.crumb) {
        $headers[$crumbResponse.crumbRequestField] = $crumbResponse.crumb
    }
} catch {
    # If crumb issuer is disabled or not found, proceed without it
}

# Fetch existing credentials
$credsApiUrl = "$JenkinsUrl/credentials/store/system/domain/_/api/json?tree=credentials[id]"
try {
    $existingResponse = Invoke-RestMethod -Uri $credsApiUrl -Headers $headers -Method Get
} catch {
    Handle-JenkinsException $_ "Checking existing credentials"
}

$existingIds = @()
if ($existingResponse -and $existingResponse.credentials) {
    $existingIds = @($existingResponse.credentials | ForEach-Object { $_.id })
}

$createUrl = "$JenkinsUrl/credentials/store/system/domain/_/createCredentials"

# 3a. Docker Hub credential (dockerhub-creds)
$dhId = "dockerhub-creds"
if ($existingIds -contains $dhId) {
    Write-Host "Credential '$dhId' already exists. Skipping creation." -ForegroundColor Yellow
} else {
    $dhPassPlain = [System.Net.NetworkCredential]::new('', $dhTokenSec).Password
    try {
        $dhPayload = [ordered]@{
            "" = "0"
            credentials = [ordered]@{
                scope       = "GLOBAL"
                id          = $dhId
                username    = "vedantkulkarni12"
                password    = $dhPassPlain
                description = "Docker Hub"
                '$class'    = "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl"
            }
        }
        $dhJson = $dhPayload | ConvertTo-Json -Depth 5 -Compress
        $dhBody = "json=" + [System.Uri]::EscapeDataString($dhJson)

        Invoke-RestMethod -Uri $createUrl -Headers $headers -Method Post -ContentType "application/x-www-form-urlencoded" -Body $dhBody | Out-Null
        Write-Host "Created credential: $dhId" -ForegroundColor Green
    } catch {
        Handle-JenkinsException $_ "Creating $dhId"
    } finally {
        $dhPassPlain = $null
        [System.GC]::Collect()
    }
}

# 3b. PostgreSQL password credential (rwh-db-password)
$dbId = "rwh-db-password"
if ($existingIds -contains $dbId) {
    Write-Host "Credential '$dbId' already exists. Skipping creation." -ForegroundColor Yellow
} else {
    $dbPassPlain = [System.Net.NetworkCredential]::new('', $dbPassSec).Password
    try {
        $dbPayload = [ordered]@{
            "" = "0"
            credentials = [ordered]@{
                scope       = "GLOBAL"
                id          = $dbId
                secret      = $dbPassPlain
                description = "RWH database password"
                '$class'    = "org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl"
            }
        }
        $dbJson = $dbPayload | ConvertTo-Json -Depth 5 -Compress
        $dbBody = "json=" + [System.Uri]::EscapeDataString($dbJson)

        Invoke-RestMethod -Uri $createUrl -Headers $headers -Method Post -ContentType "application/x-www-form-urlencoded" -Body $dbBody | Out-Null
        Write-Host "Created credential: $dbId" -ForegroundColor Green
    } catch {
        Handle-JenkinsException $_ "Creating $dbId"
    } finally {
        $dbPassPlain = $null
        [System.GC]::Collect()
    }
}

# 5. Retrieve and print ONLY the credential IDs
try {
    $finalCreds = Invoke-RestMethod -Uri $credsApiUrl -Headers $headers -Method Get
    Write-Host "`nCredential IDs currently configured in Jenkins:" -ForegroundColor Cyan
    if ($finalCreds -and $finalCreds.credentials) {
        foreach ($c in $finalCreds.credentials) {
            Write-Output $c.id
        }
    }
} catch {
    Handle-JenkinsException $_ "Listing credentials"
}
