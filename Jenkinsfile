pipeline {
    agent any
    environment {
        dockerImage=''
        registry = "rajabisamah/order-service"
        registryCredential = credentials('dockerhub-id')
    }
    tools {
        maven "maven"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                extensions: [cleanBeforeCheckout()],
                userRemoteConfigs: [[credentialsId: 'github-ssh', url: 'git@github.com:Samah-00/order.git']])
            }
        }
        stage('Build and Test') {
            steps {
                script {
                    bat "mvn clean install"
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    // Push Docker image to DockerHub
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-id') {
                        dockerImage.push('latest')
                    }
                }
            }
        }
        stage('Clean up') {
            steps {
                bat 'del /s /q order' // Delete order directory recursively (/s) and quietly (/q)
                bat "docker rmi $registry:$BUILD_NUMBER"
            }
        }
    }
}
