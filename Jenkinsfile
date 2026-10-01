pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging'], description: 'Target environment to deploy to')
    }

    tools {
    maven 'Maven-3.9.16'
    jdk 'JDK-21'
    }

    environment {
        TOMCAT_WEBAPPS = 'C:\\DevTools\\apache-tomcat-10.1\\webapps'
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
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def artifactName = "rwh-tracker-${params.DEPLOY_ENV}.war"
                    bat "copy target\\*.war \"${env.TOMCAT_WEBAPPS}\\${artifactName}\""
                }
            }
        }
    }

    post {
        success {
            echo "Deployed to ${params.DEPLOY_ENV} environment successfully."
        }
    }
}