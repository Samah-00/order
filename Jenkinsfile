pipeline {
    agent any
    tools {
        maven "maven"
    }
    environment {
        dockerImage=''
        registry = "rajabisamah/order-service"
    }
    stages {
        stage('Initial Checks') {
            steps {
                script {
                    if (current_status == "closed" && merged == "true" && branch == "main") {
                        env.executeStages = true
                    } else {
                        env.executeStages = false
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                extensions: [cleanBeforeCheckout()],
                userRemoteConfigs: [[credentialsId: 'github-ssh', url: 'git@github.com:Samah-00/order.git']])
            }
        }
        stage('Build and Test') {
            when {
                expression { return env.executeStages }
            }
            steps {
                script {
                    bat "mvn clean install"
                }
            }
        }
        stage('Build Docker Image') {
            when {
                expression { return env.executeStages }
            }
            steps {
                script {
                    dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }
        stage('Push to Docker Hub') {
            when {
                expression { return env.executeStages }
            }
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub_id') {
                        dockerImage.push()
                        dockerImage.push('latest')
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                bat "docker rmi -f $registry:$BUILD_NUMBER"            }
        }
    }
}
