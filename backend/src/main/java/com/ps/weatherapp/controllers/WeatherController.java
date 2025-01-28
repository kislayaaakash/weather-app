pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'kislaya-weather-app'
        BACKEND_IMAGE = "${DOCKER_REGISTRY}/backend:latest"
        FRONTEND_IMAGE = "${DOCKER_REGISTRY}/frontend:latest"
    }

    stages {
        stage('Check for Changes') {
            steps {
                script {
                    // Check the changes in the last commit using PowerShell
                    def changes = powershell(script: '''
                        $changes = git diff --name-only HEAD~1
                        return $changes
                    ''', returnStdout: true).trim()

                    // Set environment variables based on changes detected
                    env.BUILD_BACKEND = changes.contains('backend/') ? 'true' : 'false'
                    env.BUILD_FRONTEND = changes.contains('frontend/') ? 'true' : 'false'
                    env.UPDATE_ENV = changes.contains('.env') ? 'true' : 'false'
                }
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    // Build the backend image if necessary (either the image doesn't exist or changes detected)
                    def backendImageExists = powershell(script: '''
                        $image = docker images -q $Env:BACKEND_IMAGE
                        if ($image) {
                            return $true
                        } else {
                            return $false
                        }
                    ''', returnStdout: true).trim()

                    if (backendImageExists == 'false' || $env.BUILD_BACKEND == 'true') {
                        echo "Building backend image..."
                        dir('backend') {
                            // Run the backend build steps
                            powershell '''
                            ./mvnw clean package
                            docker build -t $Env:BACKEND_IMAGE .
                            '''
                        }
                    } else {
                        echo "Skipping backend build, no changes detected."
                    }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                script {
                    // Build the frontend image if necessary (either the image doesn't exist or changes detected)
                    def frontendImageExists = powershell(script: '''
                        $image = docker images -q $Env:FRONTEND_IMAGE
                        if ($image) {
                            return $true
                        } else {
                            return $false
                        }
                    ''', returnStdout: true).trim()

                    if (frontendImageExists == 'false' || $env.BUILD_FRONTEND == 'true') {
                        echo "Building frontend image..."
                        dir('frontend') {
                            // Run the frontend build steps
                            powershell '''
                            npm install
                            npm run build
                            docker build -t $Env:FRONTEND_IMAGE .
                            '''
                        }
                    } else {
                        echo "Skipping frontend build, no changes detected."
                    }
                }
            }
        }

        stage('Stop and Remove Running Containers') {
            steps {
                script {
                    // Stop and remove the existing backend container if it's running
                    if (docker ps -q -f "name=spring-boot-app-01") {
                        echo "Stopping and removing the existing backend container..."
                        powershell '''
                        docker stop spring-boot-app-01
                        docker rm spring-boot-app-01
                        '''
                    }
                    
                    // Stop and remove the existing frontend container if it's running
                    if (docker ps -q -f "name=react-app-01") {
                        echo "Stopping and removing the existing frontend container..."
                        powershell '''
                        docker stop react-app-01
                        docker rm react-app-01
                        '''
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                withCredentials([string(credentialsId: 'WEATHER_API_KEY', variable: 'WEATHER_API_KEY')]) {
                    powershell '''
                    # Load environment variables from the .env file inside the devops folder
                    $env:EnvFile = ".env"
                    if (Test-Path $env:EnvFile) {
                        Get-Content $env:EnvFile | ForEach-Object {
                            $key, $value = $_ -split '='
                            [System.Environment]::SetEnvironmentVariable($key, $value, [System.EnvironmentVariableTarget]::Process)
                        }
                    }

                    # Shutdown any running Docker containers using the docker-compose.yml inside the devops folder
                    docker-compose down

                    # Run Docker Compose in detached mode with the environment variables set
                    # Use --no-build to avoid rebuilding images if no changes
                    docker-compose up -d --no-build
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
