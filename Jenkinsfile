pipeline {
    agent any

    environment {
        // PATH'e Maven ve Homebrew bin dizinlerini ekliyoruz
        PATH = "/Users/testinium/.jenkins/tools/hudson.tasks.Maven_MavenInstallation/mavenJenkins/bin:/opt/homebrew/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/emrekrdenz/jenkinsDeneme.git'
            }
        }

        stage('Build') {
            steps {
                // Doğru şekilde sh komutunu kullanıyoruz
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'scp target/myapp.jar user@server:/path/to/deploy'
            }
        }
    }

    post {
        success {
            echo 'Pipeline başarıyla tamamlandı!'
        }
        failure {
            echo 'Pipeline başarısız oldu.'
        }
    }
}
