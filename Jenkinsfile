pipeline {
    agent any

    tools {
        maven 'mavenJenkins'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/kullanici/proje-repo.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'gauge run specs'
            }
            post {
                always {
                    junit 'reports/**/*.xml'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}
