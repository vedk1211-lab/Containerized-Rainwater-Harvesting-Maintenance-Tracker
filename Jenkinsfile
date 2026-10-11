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

    environment {
        PATH          = "C:\\Users\\Vedant\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
        DOCKER_CONFIG = "C:\\Users\\Vedant\\.docker"
        IMAGE_NAME    = 'vedantkulkarni12/rwh-tracker'
        IMAGE_TAG     = "1.0.${env.BUILD_NUMBER}"
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
                bat 'mvn test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Check') {
            steps {
                bat 'where docker'
                bat 'docker version'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%IMAGE_TAG% -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                    bat 'docker push %IMAGE_NAME%:%IMAGE_TAG%'
                    bat 'docker push %IMAGE_NAME%:latest'
                    bat 'docker logout'
                }
            }
        }

        stage('Deploy Container') {
            steps {
                withCredentials([string(credentialsId: 'rwh-db-password', variable: 'DB_PASS')]) {
                    bat 'docker rm -f rwh-tracker 2>nul || ver >nul'
                    bat 'docker run -d --name rwh-tracker -p 8081:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/rwh_tracker -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=%DB_PASS% %IMAGE_NAME%:%IMAGE_TAG%'
                }
            }
        }
    }

    post {
        success {
            echo "Successfully deployed ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "Build/deploy failed for ${params.DEPLOY_ENV} environment."
        }
    }
}