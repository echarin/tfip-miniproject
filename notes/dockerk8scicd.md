# Docker, Kubernetes and CI/CD with GitHub Actions

- Docker is a platform that helps in automating the deployment, scaling and management of applications by containerising them
- Package your app and all its dependencies together in the form of a Docker container to ensure it works seamlessly in any environment, be it development, staging or production
- Kubernetes is a container orchestration system for Docker containers which is more extensive than Docker Swarm
  - It is meant to coordinate clusters of nodes at scale in production in an efficient manner
  - Helps in managing, scaling and ensuring the availability of containerised applications
- GitHub Actions automates your software workflows, including building, testing and deploying applications
  - When something happens to or in the repository, you can start an automated workflow in response
  - For example, in this case:
    - Build Docker images for your frontend and backend applications
    - Push the Docker images to a Docker registry
    - Apply the Kubernetes deployment and service configurations

## Docker Overview

### Motivation

- Applications used to run on physical servers
- Because resource boundaries for applications in a physical servers did not exist, there would be resource allocation issues where one application might hog up resources and cause other apps to underperform

### Container

- A container is a sandboxed process on your machine which is isolated from all other processes on the host machine
- It is a runnable instance of an image - you can create, start, stop, move, or delete a container using the DockerAPI or CLI
- Containers can be run on local machines, virtual machines or deployed to the cloud
- They are portable and can be run on any OS
- They are isolated from other containers, and run their own software, binaries and configurations

### Container image

- When running a container, it uses an isolated filesystem
- The custom filesystem is provided by a container image
- Since the image contains the container's filesystem, it must contain everything needed to run an app: all dependencies, configurations, scripts, binaries, etc.
- The image also contains other configurations for the container, such as environment variables, a default command to run, and other metadata

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

### Multicontainer apps

- Each container should do one thing and do it well
  - You may have to scale APIs and frontends differently than databases
  - Separate containers let you version and update versions in isolation
  - While you may use a container for the database locally, you may want to use a managed service for the database in production; you don't want to ship your database engine with your app then
  - Running multiple processes will require a process manager (the container only starts one process), which adds complexity to container startup/shutdown
- If you place multiple containers on the same network, they can talk to each other
  - Two ways:
    - Assign the network when starting the container
    - Connect an already running container to a network
- First, we create the network: `docker network create todo-app`
- Assigning the network when starting the container

```powershell
docker run -d `
     --network todo-app --network-alias mysql `
     -v todo-mysql-data:/var/lib/mysql `
     -e MYSQL_ROOT_PASSWORD=secret `
     -e MYSQL_DATABASE=todos `
     mysql:8.0
```

- The command
  - You have specified the network in `--network`, where its alias is `mysql`
  - There is a volume named `todo-mysql-data` mounted at `/var/lib/mysql` which is where MySQL stores its data
    - Despite not running a `docker volume create` command, Docker recognises that you want to use a named volume and creates one automatically
  - The `-e` flags are for setting environment variables
- Connecting to the database
  - `docker exec -it <mysql-container-id> mysql -u root -p`
  - A password prompt will come up; use the password you've set as `MYSQL_ROOT_PASSWORD` through the `-e` flag
  - You will enter the MySQL shell; do `SHOW DATABASES` to verify that you see the `todos` database which was the name you specified in `MYSQL_DATABASE`
  - Type `exit` in the shell to return to the shell on your machine
- Connecting a container to the database
  - You can run another container on the same network, and then specify the database's IP address
  - Run `docker run -it --network todo-app nicolaka/netshoot`; it has tools that are useful for troubleshooting or debugging networking issues
    - In the container, use `dig mysql`, which will help you look up the IP address for the hostname `mysql`, which was the name you gave through the `--network-alias` flag
    - In the answer section, you will see an `A` record for `mysql` that resolves to the IP address you will use
  - You may set environment variables to specify MySQL connecting settings
    - This is generally accepted for development but highly discouraged when running applications in production
      - A more secure mechanism is to use the secret support provided by your container orchestration framework e.g. Kubernetes, which allows you to use sensitive information without including it in your application code
  - Start the container while in the `getting-started/app` directory:

```powershell
docker run -dp 127.0.0.1:3000:3000 `
   -w /app -v "$(pwd):/app" `
   --network todo-app `
   -e MYSQL_HOST=mysql `
   -e MYSQL_USER=root `
   -e MYSQL_PASSWORD=secret `
   -e MYSQL_DB=todos `
   node:18-alpine `
   sh -c "yarn install && yarn run dev"
```

- Checking through the logs using `docker logs -f <container-id>`, you will see `Connected to mysql db at host mysql`
- Connect to the mysql databnase using `docker exec -it <mysql-container-id> mysql -p todos`
  - You will see the items when you do `select * from todo_items` in the MySQL shell

### Docker Compose

- A tool that helps to define and share multicontainer applications
- You can create a YAML file to define the services and with a single command, spin everything up or tear it all down
- With one single file in the root of your project repo, you can define your application stack
  - Someone would only need to clone your repo and start the compose app
- Create a `docker-compose.yml` file in the `getting-started/app` directory

```yml
# Define the list of services (or containers) that you want to run as part of your application
services:
  # docker run -dp 127.0.0.1:3000:3000 \
  #   -w /app -v "$(pwd):/app" \
  #   --network todo-app \
  #   -e MYSQL_HOST=mysql \
  #   -e MYSQL_USER=root \
  #   -e MYSQL_PASSWORD=secret \
  #   -e MYSQL_DB=todos \
  #   node:18-alpine \
  #   sh -c "yarn install && yarn run dev"
  # You can pick any name for this service, which will automatically become a network alias
  # The network alias will be useful when defining the MySQL service
  app:
    image: node:18-alpine
    command: sh -c "yarn install && yarn run dev"
    # This maps to the -p flag by defining the ports for the service
    ports:
      - 127.0.0.1:3000:3000
    # -w flag
    working_dir: /app
    # -v flag
    # Notice relative paths from the current directory
    volumes:
      - ./:/app
    environment:
      MYSQL_HOST: mysql
      MYSQL_USER: root
      MYSQL_PASSWORD: secret
      MYSQL_DB: todos
  # docker run -d \
  #   --network todo-app --network-alias mysql \
  #   -v todo-mysql-data:/var/lib/mysql \
  #   -e MYSQL_ROOT_PASSWORD=secret \
  #   -e MYSQL_DATABASE=todos \
  #   mysql:8.0
  mysql:
    image: mysql:8.0
    volumes:
      - todo-mysql-data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: todos

# When doing docker run, the named volume for the MySQL container was created automatically
# However, this does not ghappen when running with Compose
# We need to define the volume in the top-level volumes section and then specify the mountpoint in the service config
# If you simply provide only the volume name here, then the default options are used
# Other options can be found in https://docs.docker.com/compose/compose-file/07-volumes/
volumes:
  todo-mysql-data:
```

- Run the application stack using `docker compose up -d`, where the `-d` flag will run everything in the background
  - You can check the logs using `docker compose logs -f`, where the `f` flag "follows" the log, giving you live output as the log is being generated
  - When you see `Listening on port 3000`, you can then open the app
- On Docker Dashboard, you will see a group named `app` - the project name is the name of the directory where your `docker-compose.yml` was located in
  - By clicking the disclose arrow, you can see both containers defined in the compose file
  - The names of the containers follow the pattern `<service-name>-<replica-number>`
- To tear down everything, run `docker compose down` or hit the trash can on the Docker Dashboard for the entire app
  - However, this does not remove named volumes in your compose file; if you want to remove them, then add the `--volumes` flag
  - The Docker Dashboard will also not remove volumes when you delete the app stack

### Image-building best practices

#### Image layering

- Once a layer changes, all downstream layers have to be recreated as well

```dockerfile
# syntax=docker/dockerfile:1
FROM node:18-alpine
WORKDIR /app
COPY . .
RUN yarn install --production
CMD ["node", "src/index.js"]
```

- When we made a change to the image, the yarn dependencies had to be reinstalled, since it comes after `COPY . .`
  - But we should not have to, since these are the same dependencies every time
- Therefore, to cache the dependencies, we can copy `package.json` in first, then install the dependencies, then copy in everything else
  - As such, we will only recreate the yarn dependencies if there was a change to `package.json`
  - Since we usually don't change the dependencies, future builds will be much faster
- We can update our Dockerfile as such:

```dockerfile
# syntax=docker/dockerfile:1
 FROM node:18-alpine
 WORKDIR /app
 COPY package.json yarn.lock ./
 RUN yarn install --production
 COPY . .
 CMD ["node", "src/index.js"]
```

- Create a `.dockerignore` file in the same directory as the Dockerfile as such:
  - More details here [https://docs.docker.com/engine/reference/builder/#dockerignore-file]
  - The `node_modules` folder should be omitted in the second `COPY` step because otherwise, it would possibly overwrite files which were created by the command in the `RUN` step before that

```.dockerignore
node_modules
```

- We can build a new image with `docker build -t getting-started .`, make a change, build it again, and see that the second build is much faster

#### Multistage builds

- Using multiple stages to create an image
  - Separate build-time dependencies from runtime dependencies
  - Reduce overall image size by shipping only what your app needs to run
- Maven/Tomcat example
  - When building Java-based applications, a JDK is needed to compile the source code to Java bytecode
    - But this JDK is not needed in production
  - You might also be using Maven or Gradle to build the app, which are not needed in the final image

```dockerfile
# syntax=docker/dockerfile:1
FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM tomcat
COPY --from=build /app/target/file.war /usr/local/tomcat/webapps
```

- Explanation
  - We use one stage (called `build`) to perform the actual Java build using Maven
  - In the second stage (second `FROM`), we copy in files from the build stage
  - The final image is only the last stage being created

## Building a Java image

Taken from [https://docs.docker.com/language/java/]

## For tfip-miniproject

### Notes

- We were using the "pre-package, then copy into image" approach
- Two `application.yml` files for different profiles: one for local development, one for Docker
- Package with `mvn clean install -Dspring.profiles.active=local`, therefore using the local profile so that tests will pass
- `docker-compose.yml` has `SPRING_PROFILES_ACTIVE=docker` which will use the `docker` profile, while also passing in 

### Optimised Dockerfile

- This Dockerfile will be usable for Railway (set your own environmental variables)

```dockerfile
# Use Maven for build stage
FROM maven:3.9.2-eclipse-temurin-20-alpine AS build

# Set up working directory in the container
WORKDIR /app

# First, copy the pom.xml file where you list your dependencies
# From root directory to /app
# The source path is relative to the build context
# The destination path is relative to the working directory inside the container
COPY pom.xml ./

# Download all required dependencies; this layer gets cached
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application. Since dependencies are downloaded,
# this will basically just compile the code and package it
RUN mvn clean package

# Use OpenJDK for run stage
FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

# Copy the JAR file from the build stage
# Notice /app/target: This JAR file comes from the build container, not the host machine
COPY --from=build /app/target/tfip-miniproject-0.0.1-SNAPSHOT.jar ./tfip-miniproject-0.0.1-SNAPSHOT.jar

# Specify environment variables required at build time
ARG SPRING_PROFILES_ACTIVE
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG JWT_SIGNINGKEY

# Make these variables available at runtime
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
ENV JWT_SIGNINGKEY=$JWT_SIGNINGKEY

# Specify the command to run your application
ENTRYPOINT [ "java", "-jar", "./tfip-miniproject-0.0.1-SNAPSHOT.jar" ]

# Expose the application port
EXPOSE 8080
```