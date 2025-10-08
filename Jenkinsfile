pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Compilando el proyecto...'
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                echo 'Ejecutando pruebas...'
                sh 'mvn test'
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
