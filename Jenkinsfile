pipeline {
    agent any

    tools {
        nodejs 'nodejs 23'
        maven 'mavenJenkins'
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

       stage('Test with Gauge (Maven)') {
           steps {
               // Gauge Maven plugin'i ile testleri çalıştır
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
