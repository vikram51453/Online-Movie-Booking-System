
pipeline {
    agent any

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
                    docker ps --filter "name=movie-booking-app"
                    docker inspect --format="{{.State.Status}}" movie-booking-app
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Movie Booking System built, tested, and deployed using Docker!'
            echo 'Website: http://localhost:8081'
        }

        failure {
            echo 'FAILURE: Pipeline failed. Check Console Output for the error.'
        }
    }
}
