
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
    }

    post {
        success {
            echo 'Movie Booking System pipeline and Docker build completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
