pipeline {
    agent any

    tools {
        nodejs 'nodejs 23' // Global Tool Configuration’da belirlediğiniz isim
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/emrekrdenz/jenkinsDeneme.git'
            }
        }

        stage('Setup') {
            steps {
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
