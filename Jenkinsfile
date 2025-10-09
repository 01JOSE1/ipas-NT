pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Compilando el proyecto...'
                bat 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo 'Ejecutando pruebas...'
                bat 'mvn test'
            }
        }

        stage('Archive') {
            steps {
                echo 'Archivando artefacto...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Construyendo imagen Docker...'
                sh 'docker build -t miapp:latest .'
                echo 'Ejecutando contenedor...'
                sh '''
                    docker stop miapp_container || true
                    docker rm miapp_container || true
                    docker run -d -p 8080:8080 --name miapp_container miapp:latest
                '''
            }
        }
    }
}