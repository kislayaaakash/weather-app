# Step 1: Use the official Node.js image to build the React application
FROM node:18 as build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code to the container
COPY . .

# Build the React application for production
RUN npm run build

# Step 2: Use a lightweight web server (e.g., Nginx) to serve the built files
FROM nginx:stable-alpine

# Copy the build output from the previous stage to the Nginx web root
COPY --from=build /app/build /usr/share/nginx/html

# Expose the port the app runs on
EXPOSE 8080

# Start Nginx when the container starts
CMD ["nginx", "-g", "daemon off;"]
