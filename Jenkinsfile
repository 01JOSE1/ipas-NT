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
    }
}
