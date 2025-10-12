pipeline {
    agent any
    
    environment {
        // Credenciales de las bases de datos
        DB_TEST_HOST = 'ipas-mysql-test'
        DB_TEST_PORT = '3306'
        DB_TEST_NAME = 'ipas_test'
        DB_TEST_USER = 'ipas_test_user'
        DB_TEST_PASSWORD = 'test_password'
        
        DB_PROD_HOST = 'ipas-mysql-prod'
        DB_PROD_PORT = '3306'
        DB_PROD_NAME = 'ipas_prod'
        DB_PROD_USER = 'ipas_prod_user'
        DB_PROD_PASSWORD = 'prod_password'
        
        // Docker
        DOCKER_IMAGE = 'ipas-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonando código desde GitHub...'
                checkout scm
            }
        }
        
        stage('Build con Maven') {
            steps {
                echo '🔨 Compilando proyecto con Maven...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test con JUnit') {
            steps {
                echo '🧪 Ejecutando tests unitarios...'
                sh """
                    mvn test \
                    -Dspring.datasource.url=jdbc:mysql://${DB_TEST_HOST}:${DB_TEST_PORT}/${DB_TEST_NAME} \
                    -Dspring.datasource.username=${DB_TEST_USER} \
                    -Dspring.datasource.password=${DB_TEST_PASSWORD}
                """
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Empaquetando aplicación...'
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Construyendo imagen Docker...'
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }
        
        stage('Deploy to Docker') {
            steps {
                echo '🚀 Desplegando aplicación...'
                script {
                    // Detener contenedor anterior si existe
                    sh '''
                        docker stop ipas-app || true
                        docker rm ipas-app || true
                    '''
                    
                    // Ejecutar nuevo contenedor
                    sh """
                        docker run -d \
                        --name ipas-app \
                        --network ipas-network \
                        -p 8081:8080 \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://${DB_PROD_HOST}:${DB_PROD_PORT}/${DB_PROD_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=${DB_PROD_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PROD_PASSWORD} \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline ejecutado exitosamente!'
        }
        failure {
            echo '❌ Pipeline falló. Revisa los logs.'
        }
        always {
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
    }
}
