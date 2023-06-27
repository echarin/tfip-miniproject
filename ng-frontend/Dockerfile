# Use Node.js for build stage
FROM node:18.16.1-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . ./
RUN npm run build -- --output-path=./dist/out

# Use Nginx for run stage
FROM nginx:stable-alpine
COPY --from=build /app/dist/out/ /usr/share/nginx/html
# COPY nginx.conf /etc/nginx/nginx.conf

# Expose the application port
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]