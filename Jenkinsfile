pipeline {
    agent any
    
    options {
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    environment {
        GITHUB_REPO = 'https://github.com/01JOSE1/ipas.git'
        IMAGE_NAME = 'ipas-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('01 - Checkout') {
            steps {
                echo '========== DESCARGANDO CÓDIGO =========='
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: '${GITHUB_REPO}',
                        credentialsId: 'github-credentials-ipas'
                    ]]
                ])
            }
        }
        
        stage('02 - Compilar') {
            steps {
                echo '========== COMPILANDO =========='
                sh 'mvn clean compile'
            }
        }
        
        stage('03 - Testear') {
            steps {
                echo '========== EJECUTANDO TESTS =========='
                sh 'mvn test'
            }
        }
        
        stage('04 - Empaquetar') {
            steps {
                echo '========== EMPAQUETANDO JAR =========='
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('05 - Crear Imagen Docker') {
            steps {
                echo '========== CREANDO IMAGEN DOCKER =========='
                sh '''
                    docker build -t ${IMAGE_NAME}:${DOCKER_TAG} .
                    docker tag ${IMAGE_NAME}:${DOCKER_TAG} ${IMAGE_NAME}:latest
                '''
            }
        }
    }
    
    post {
        success {
            echo '✓ Pipeline exitoso'
        }
        failure {
            echo '✗ Pipeline falló'
        }
    }
}
