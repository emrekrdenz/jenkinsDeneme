pipeline {
    agent any

    tools {
        nodejs 'nodejs 23'
        maven 'mavenJenkins'
    }

     environment {
            PATH = "/opt/homebrew/bin:${env.PATH}"
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

         stage('Build and Test Compile') {
             steps {
                 sh 'mvn clean test-compile'
             }
         }

         stage('Test with Gauge (Maven)') {
             steps {
                 sh 'mvn gauge:execute -DspecsDir=specs -DadditionalFlags="-r html-report"'
             }
         }



        stage('Publish Report') {
            steps {
                // HTML rapor varsayılan olarak reports/html-report altına oluşturulur
                publishHTML(target: [
                    reportDir: 'reports/html-report',
                    reportFiles: 'index.html',
                    reportName: 'Gauge Test Raporu'
                ])
            }
        }
    }

    post {
        always {

            archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
        }
    }
}
