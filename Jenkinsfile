
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Automated Tests') {
            steps {
                bat 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t movie-booking:1.0 .'
            }
        }

        stage('Docker Deploy') {
            steps {
                bat '''
                    docker stop movie-booking-app 2>NUL
                    docker rm movie-booking-app 2>NUL
                    docker run -d --name movie-booking-app -p 8081:8081 -e DB_HOST=host.docker.internal -e DB_PORT=3306 -e DB_NAME=movie_booking movie-booking:1.0
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                bat '''
                    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ok = $false; for ($i = 0; $i -lt 12; $i++) { try { $r = Invoke-WebRequest -Uri 'http://localhost:8081/' -UseBasicParsing -TimeoutSec 5; if ($r.StatusCode -eq 200) { $ok = $true; break } } catch { Start-Sleep -Seconds 5 } }; if (-not $ok) { Write-Error 'Website health check failed'; exit 1 }; Write-Host 'Website health check passed: HTTP 200'"
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build, tests, Docker deployment, and website health check completed!'
            echo 'Website: http://localhost:8081'
        }

        failure {
            echo 'FAILURE: Pipeline failed. Check Console Output for the error.'
        }
    }
}
