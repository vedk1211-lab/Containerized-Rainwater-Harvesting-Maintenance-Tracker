pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging'], description: 'Target environment to deploy to')
    }

    tools {
        maven 'Maven-3.9.16'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test -DexcludedGroups=selenium -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Selenium UI Tests') {
            steps {
                bat '''
                    if exist target\\screenshots rd /s /q target\\screenshots
                    mvn test -Dgroups=selenium -Dspring.profiles.active=test
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/screenshots/**'
                }
            }
        }
    }

    post {
        success {
            echo "CI pipeline passed for ${params.DEPLOY_ENV} environment."
            script {
                build job: 'rwh-tracker-cd', wait: false
            }
        }
        failure {
            echo "CI pipeline failed for ${params.DEPLOY_ENV} environment."
        }
    }
}