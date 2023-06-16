# Docker, Kubernetes and CI/CD with GitHub Actions

- Docker is a platform that helps in automating the deployment, scaling and management of applications by containerising them
- Package your app and all its dependencies together in the form of a Docker container to ensure it works seamlessly in any environment, be it development, staging or production
- Kubernetes is a container orchestration system for Docker containers which is more extensive than Docker Swarm
  - It is meant to coordinate clusters of nodes at scale in production in an efficient manner
  - Helps in managing, scaling and ensuring the availability of containerised applications
- GitHub Actions automates your software workflows, including building, testing and deploying applications
  - It can be used to automate building the Docker images and deploying them to the chosen PaaS platform

## Motivation

- Applications used to run on physical servers
- Because resource boundaries for applications in a physical servers did not exist, there would be resource allocation issues where one application might hog up resources and cause other apps to underperform

## Possible starting workflow

- **Application development**
  - Develop the Spring Boot backend and Angular frontend applications
- **Containerise your applications**
  - Write Dockerfiles for both the frontend and backend applications, which describe the build process for Docker to create Docker images
  - These Docker images will contain everything your applications need to run, including the application code and dependencies
- **CI/CD setup with GitHub Actions**
  - Set up a GitHub Actions workflow. When you push code to the repository, this workflow should:
    - Build Docker images for the frontend and backend using your Dockerfiles
    - Push these Docker images to a Docker registry. Docker Hub is a popular choice, but there are others like Google Container Registry or AWS ECR
- **Kubernetes deployment**
  - Create Kubernetes deployment files (manifests) for both frontend and backend applications
  - These YAML files describe the desired state for your applications in Kubernetes, including the Docker image to use, the number of replicas, and other configurations
- **Set up Kubernetes services**
  - Services allow your applications to communicate with each other and also expose your applications within the Kubernetes cluster
- **Database deployment**
  - Deploy your MySQL/MongoDB databases to a cloud-based managed database service such as Google Cloud SQL or Amazon RDS for MySQL, and MongoDB Atlas for MongoDB
  - The connection strings for these databases will be used in the Spring Boot application
- **Ingress controller**
  - Configure an Ingress controller to manage external access to your services in the cluster
  - This acts as a gateway for your applications and routes incoming requests to the appropriate services
  - Might need a LoadBalancer type service for the Ingress controller to expose it to the internet
- **Automate Kubernetes deployment with GitHub Actions**
  - Extend your GitHub Actions workflow to update your Kubernetes deployments with the new Docker images after every build
  - This can be done using `kubectl` commands

## Docker Walkthrough

Taken from [https://docs.docker.com/get-started/]

### Containerise an application

- In the app directory, create a `Dockerfile` with the following contents:

```dockerfile
# syntax=docker/dockerfile:1

# Set the baseImage to use for subsequent instructions. FROM must be the first instruction in a Dockerfile.
FROM node:18-alpine

# Set the working directory for any subsequent ADD, COPY, CMD, ENTRYPOINT, or RUN instructions that follow it in the Dockerfile.
WORKDIR /app

# Copy files or folders from source to the dest path in the image's filesystem.
COPY . .

# Execute any commands on top of the current image as a new layer and commit the results.
RUN yarn install --production

# Provide defaults for an executing container. If an executable is not specified, then ENTRYPOINT must be specified as well. There can only be one CMD instruction in a Dockerfile.
CMD ["node", "src/index.js"]

# Define the network ports that this container will listen on at runtime.
EXPOSE 3000
```

- Build the container image with the CLI: `docker build -t getting-started .`
  - `docker build` uses the Dockerfile to build a new container image
    - If you don't have the `node:18-alpine` image, then Docker will download it
  - Subsequent instructions: the application was copied in, and then `yarn` was used to install the application's dependencies
    - `CMD` then specifies the default command to run when starting a container from this image
  - `-t` tags your image; we named the image `getting-started`
    - We can now use `getting-started` to refer to that image when running a container
  - `.` tells Docker that it should look for the `Dockerfile` in the current directory
- Start the container using `docker run -dp 127.0.0.1:3000:3000 getting-started`
  - `-d` is short for `--detached`, which runs the container in the background
  - `-p` is short for `--publish`, which creates a port mapping between the host and the container
    - `-p` takes a string value in the form of `HOST:CONTAINER`, where `HOST` is the address on the host, and `CONTAINER` is the port on the container
    - Here, we see `HOST` as `127.0.0.1:3000` and `CONTAINER` as `3000`
    - Without this port mapping, we would not be able to access the application from the host
- The application is now accessible on [http://localhost:3000]

### Update the application

- Make changes to your code
- Build the image again using `docker build`
- Since the old container is still running, you have to stop it and remove it
  - Find the ID of the container using `docker ps`
  - Run `docker stop <the-container-id>`
  - Run `docker rm <the-container-id>`
  - In one command, you can stop and remove a container: `docker rm -f <the-container-id>`
- However, there is no persistence in this process and there are too many steps

### Share the application

- You can share Docker images to a Docker registry (the default is Docker Hub)
- Create a public repository on Docker Hub and push the image to the repo
  - First, login with `docker login -u <username>`
  - Tag your existing image you've built to give it another name: `docker tag getting-started <username>/getting-started`
    - Format: `docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]`
  - Then we push: `docker push <username>/getting-started`
- We can run the image as such: `docker run -dp 0.0.0.0:3000:3000 <username>/getting-started`
  - Where we used to have `127.0.0.1:3000` as the host, we now use `0.0.0.0`
  - Binding to `127.0.0.1` exposes a container's ports to the loopback interface
  - But binding to `0.0.0.0` exposes the container's port on all interfaces of the host, making it available to the outside world

### Persist the DB

- When a container runs, it uses the various layers from an image for its filesystem
  - Each container also gets its own "scratch space" to CRUD files
  - Any changes won't be seen in another container, even if they're using the same image
- Volumes are the preferred mechanism for persisting data generated by, and used by Docker containers
  - They provide the ability to connect specific filesystem paths of the container back to the host machine
  - If you mount a directory in the container, then changes in that directory are also seen on the host machine
  - When mounting that same directory across container restarts, you'll see the same files
  - For example, with the database being a single file (as on SQLite), you can persist that file on the host and make it available to the next container, which will pick up where the last one left off
  - By creating a volume and attaching it to the directory where the data is stored, the data can be persisted
- Create a volume and start the container
  - Volume creation: `docker volume create todo-db`
  - When starting the todo app container, add the `--mount` option to specify a volume mount: `docker run -dp 127.0.0.1:3000:3000 --mount type=volume,src=todo-db,target=/etc/todos getting-started`
  - Afterwards, despite removing the container and starting a new container, the todo data is persisted
- You can inspect the volume using `docker volume inspect <volume-name>`

### Using bind mounts

- Bind mounts are another type of mount which let you share a directory from the host's filesystem into the container
  - You can use a bind mount to mount source code into the container
  - The container will see changes you make to the code immediately, as soon as you save a file
  - Therefore you can run processes in the container which watch for filesystem changes and respond to them
  - Using bind mounts and `nodemon`, you can watch for file changes and restart the application automatically
- Trying out bind mounts
  - In the `getting-started\app` directory, run in PowerShell: `docker run -it --mount "type=bind,src=$pwd,target=/src" ubuntu bash`
    - `--mount` tells Docker to create a bind mount, where `src=$pwd` points to the current working directory on the host machine, and `target` is where that directory should appear inside the container (in `/src)
    - `bash` tells Docker to start an interactive `bash` session in the root directory of the container's filesystem
  - When changes are made to a file in host `getting-started\app` or container `\src`, the changes will appear in both
- Running an app in a development container
  - The goal is to run a development container with a bind mount which mounts the source code into the container, installs all dependencies, and starts `nodemon` to watch for filesystem changes

```powershell
docker run -dp 127.0.0.1:3000:3000 `
    -w /app --mount "type=bind,src=$pwd,target=/app" `
    node:18-alpine `
    sh -c "yarn install && yarn run dev"
```

- Breakdown of the command
  - `-dp 127.0.0.1:3000:3000`: run in detached (background) mode, create a port mapping
  - `-w /app` sets the "working direcory" or the current directory that the command will run from
  - `--mount "type=bind,src=$pwd,target=/app"` will bind mount the current directory from the host into the `/app` directory in the container
  - `node:18-alpine` is the image to use; note this is the base image for the `getting-started` Dockerfile
  - `sh -c "yarn install && yarn run dev"` is the command to run: start a shell using `sh` (since alpine does not have `bash`), and run `yarn install` to install packages, then run `yarn run dev` to start the development server
    - Inspecting `package.json`, we see that `dev` script starts `nodemon`
- The rest
  - We can watch the logs using `docker logs <container-id>`
  - You can go when you see `Listening on port 3000`
  - When making a change to the files in `/src`, the changes are reflected almost immediately