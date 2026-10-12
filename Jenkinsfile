
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
    }

    post {
        success {
            echo 'Movie Booking System pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
