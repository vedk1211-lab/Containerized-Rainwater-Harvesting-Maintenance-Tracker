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
                bat 'mvn test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        success {
            echo "CI pipeline passed for ${params.DEPLOY_ENV} environment."
        }
        failure {
            echo "CI pipeline failed for ${params.DEPLOY_ENV} environment."
        }
    }
}