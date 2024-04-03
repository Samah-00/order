# Jenkins CI Pipeline Documentation

## Overview
This document provides an explanation of the Jenkins CI pipeline script used to automate the build and deployment process for the Order Service application. The pipeline integrates with GitHub, Maven, and Docker Hub to facilitate continuous integration and deployment.

## Pipeline Script Explanation

```groovy
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
```

## Plugins Used
- **GitHub Integration Plugin:** Integrates Jenkins with GitHub to trigger builds upon code changes.
- **Docker Pipeline Plugin:** Provides Docker integration within Jenkins pipelines.
- **Maven Integration Plugin:** Integrates Maven build tool with Jenkins for compiling Java projects.

## Credentials
- **Docker Hub Credentials (`dockerhub-id`):** Provides authentication for pushing Docker images to Docker Hub.
- **GitHub SSH Credentials (`samah-00 github-ssh`):** Provides authentication for accessing the GitHub repository via SSH.

## Jenkins Configuration
- **Agent Configuration:** Configured to run the pipeline on any available agent.
- **Environment Variables:** Defined environment variables for Docker image, Docker registry, and Docker Hub credentials.
- **Tool Configuration:** Configured Maven as a tool to be used in the pipeline.

## Pipeline Stages
1. **Checkout:** Retrieves the source code from the GitHub repository.
2. **Build and Test:** Compiles the codebase and executes unit tests using Maven.
3. **Build Docker Image:** Creates a Docker image of the application.
4. **Push to Docker Hub:** Pushes the Docker image to Docker Hub.
5. **Clean up:** Removes temporary files and Docker images.

## Pipeline Script Environment Compatibility

### Windows Environment
The provided Jenkins pipeline script is compatible with Windows environments by default. It utilizes Windows batch script (`bat`) syntax for executing commands.

### Unix Environment
For Unix-based environments such as Linux or macOS, adjustments need to be made to the pipeline script syntax. Specifically, the `bat` command needs to be replaced with `sh` to execute shell commands.

### Docker Daemon
In order to build Docker images, a Docker daemon needs to be running on the machine where Jenkins is installed. Make sure that a Docker daemon is running, such as Docker Desktop, before executing the pipeline script. Without a running Docker daemon, the build process for Docker images will fail.

### GitHub Webhook Trigger
The Jenkins pipeline is triggered automatically upon code merges into the main branch on GitHub. This is achieved by configuring a webhook on the GitHub repository to trigger Jenkins builds.
Since Jenkins is running on localhost, a tool like ngrok can be used to create a tunnel and expose the local server to the internet. The Payload URL for the webhook should be set to the ngrok URL. For example:

```
Payload URL: https://8a3d-2a06-c701-9637-c700-9960-1667-c3e-46cf.ngrok-free.app/github-webhook/
```

Ensure that the ngrok tunnel is running and correctly forwarding requests to your local Jenkins instance.
