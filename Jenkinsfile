pipeline {
    agent any

    tools {
        // Jenkins'te tanımlı JDK ve Maven sürümlerini kullanın
        maven 'mavenJenkins'
    }

    environment {
        // `gauge` komutunun bulunduğu dizini PATH'e ekleyin
        PATH = "/opt/homebrew/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Kaynak kodunu Git deposundan çek
                git url: 'https://github.com/emrekrdenz/jenkinsDeneme.git', branch: 'master'
            }
        }

        stage('Build') {
            steps {
                // Maven ile projeyi derle
                sh 'mvn clean install'
            }
        }

        stage('Verify Gauge Installation') {
            steps {
                // PATH değişkenini ve gauge'ın bulunduğu yolu doğrula
                sh '''
                    echo "Current PATH: $PATH"
                    which gauge
                    gauge -v
                '''
            }
        }

        stage('Test') {
            steps {
                // Gauge testlerini çalıştır
                sh 'gauge run specs --junit-report'
            }
            post {
                always {
                    // JUnit formatında test raporlarını topla
                    junit 'reports/**/*.xml'
                }
            }
        }
    }

    post {
        always {
            // Derlenen jar dosyalarını arşivle
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}
