pipeline {
    agent any
    
    options {
        timeout(time: 1, unit: 'HOURS')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '========== DESCARGANDO CÓDIGO =========='
                git branch: 'main', url: 'https://github.com/TU_USUARIO/IPAS.git'
            }
        }
        
        stage('Compilar') {
            steps {
                echo '========== COMPILANDO CON MAVEN =========='
                sh 'mvn clean compile'
            }
        }
        
        stage('Testear') {
            steps {
                echo '========== EJECUTANDO TESTS =========='
                sh 'mvn test'
            }
        }
        
        stage('Empaquetar') {
            steps {
                echo '========== EMPAQUETANDO JAR =========='
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Crear Imagen Docker') {
            steps {
                echo '========== CREANDO IMAGEN DOCKER =========='
                sh '''
                    # Construir imagen
                    docker build -t ipas-app:${BUILD_NUMBER} .
                    
                    # Etiquetar como latest (versión más nueva)
                    docker tag ipas-app:${BUILD_NUMBER} ipas-app:latest
                '''
            }
        }
        
        stage('Success') {
            steps {
                echo '========== PIPELINE COMPLETADO =========='
                echo 'Imagen lista: ipas-app:${BUILD_NUMBER}'
            }
        }
    }
    
    post {
        failure {
            echo 'Pipeline falló. Revisar logs.'
        }
        success {
            echo 'Pipeline exitoso.'
        }
    }
}
