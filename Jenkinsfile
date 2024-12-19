pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/emrekrdenz/jenkinsDeneme.git'
            }
        }

        stage('Setup') {
            steps {
                // Node.js kurulumunu sağlamak için
                tool name: 'nodejs 23', type: 'NodeJS'
                sh 'npm install'
            }
        }

        stage('Test') {
            steps {
                sh 'gauge run specs --html-report'
            }
        }

        stage('Publish Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'reports',
                    reportFiles: 'index.html',
                    reportName: 'Gauge Test Raporu'
                ])
            }
        }
    }

    post {
        always {
            junit 'reports/*.xml'
            archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
        }
    }
}
