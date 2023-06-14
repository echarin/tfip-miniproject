# Docker, Kubernetes and CI/CD with GitHub Actions

- Docker is a platform that helps in automating the deployment, scaling and management of applications by containerising them
- Package your app and all its dependencies together in the form of a Docker container to ensure it works seamlessly in any environment, be it development, staging or production
- Kubernetes is a container orchestration system for Docker containers which is more extensive than Docker Swarm
  - It is meant to coordinate clusters of nodes at scale in production in an efficient manner
  - Helps in managing, scaling and ensuring the availability of containerised applications
- GitHub Actions automates your software workflows, including building, testing and deploying applications
  - It can be used to automate building the Docker images and deploying them to the chosen PaaS platform

## Kubernetes

### Motivation

- Applications used to run on physical servers
- Because resource boundaries for applications in a physical servers did not exist, there would be resource allocation issues where one application might hog up resources and cause other apps to underperform


## Possible starting workflow

- **Application development**
  - Develop the Spring Boot backend and Angular frontend applications
- **Containerise your applications**
  - Write Dockerfiles for both the frontend and backend applications, which describe the build process for Docker to create Docker images
  - These Docker images will contain everything your applications nedd to run, including the application code and dependencies
- **CI/CD setup with GitHub Actions**
  - Set up a GitHub Actions workflow. When you push code to the repository, this workflow should:
    - Build Docker images for the frontend and backend using your Dockerfiles
    - Push these Docker images to a Docker registry. Docker Hub is a popular choice, but there are others like Google Container Registry or AWS ECR
- **Kubernetes deployment**
  - Create Kubernetes deployment files (manifests) for both frontend and backend applications
  - These YAML files describe the desired state for your applications in Kubernetes, including the Docker image to use, the number of replicas, and other configurations
- **Set up Kubernetes services**
  - Services allow your applications to communicate with each other and also expose your applications within the Kubernetes cluster
- **