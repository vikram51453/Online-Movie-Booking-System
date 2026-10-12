
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
                    docker rm -f movie-booking-app 2>NUL
                    docker run -d --name movie-booking-app -p 8081:8081 -e DB_HOST=host.docker.internal -e DB_PORT=3306 -e DB_NAME=movie_booking movie-booking:1.0
                '''
            }
        }
    }

    post {
        success {
            echo 'Movie Booking System pipeline and Docker deployment completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
