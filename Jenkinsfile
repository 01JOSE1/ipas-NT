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
        
        // Kubernetes
        K8S_NAMESPACE = 'default'
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
        
        stage('Deploy to Docker (Staging)') {
            steps {
                echo '🚀 Desplegando en Docker (ambiente de staging)...'
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
        
        stage('Smoke Test on Docker') {
            steps {
                echo '🔍 Verificando que la aplicación en Docker funciona...'
                script {
                    // Esperar a que la app inicie
                    sleep 60
                    
                    // Health check
                    sh '''
                        for i in {1..20}; do
                          if curl -f http://ipas-app:8080/actuator/health; then
                            echo "✅ Health check exitoso"
                            exit 0
                          fi
                          echo "⏳ Intento $i/20 - Esperando..."
                          sleep 5
                        done
                        echo "❌ Health check falló"
                        exit 1
                    '''
                }
            }
        }
        
         stage('Load Image to Kubernetes') {
            steps {
                echo '📤 Cargando imagen en Minikube...'
                script {
                    // AUTOMATIZACIÓN: Cargar imagen en Minikube
                    sh "minikube image load ${DOCKER_IMAGE}:latest"
                    
                    // Verificar que la imagen se cargó
                    sh "minikube image ls | grep ${DOCKER_IMAGE}"
                }
            }
        }
        
        stage('Deploy to Kubernetes (Production)') {
            steps {
                echo '☸️ Desplegando en Kubernetes (producción)...'
                script {
                    // Actualizar el deployment con la nueva imagen
                    // Esto hace un rolling update automático
                    sh """
                        kubectl set image deployment/ipas-app \
                        ipas=${DOCKER_IMAGE}:latest \
                        -n ${K8S_NAMESPACE}
                    """
                    
                    // Esperar a que el rollout se complete
                    sh """
                        kubectl rollout status deployment/ipas-app \
                        -n ${K8S_NAMESPACE} \
                        --timeout=5m
                    """
                }
            }
        }

        
        stage('Verify Kubernetes Deployment') {
            steps {
                echo '✅ Verificando despliegue en Kubernetes...'
                script {
                    // Ver el estado de los pods
                    sh "kubectl get pods -l app=ipas -n ${K8S_NAMESPACE}"
                    
                    // Ver el estado del deployment
                    sh "kubectl get deployment ipas-app -n ${K8S_NAMESPACE}"
                    
                    // Ver el estado del HPA (auto-scaling)
                    sh "kubectl get hpa ipas-hpa -n ${K8S_NAMESPACE}"
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ ¡Pipeline ejecutado exitosamente!'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo '📦 Build #${BUILD_NUMBER} desplegado con éxito'
            echo '🐳 Docker (Staging): http://localhost:8081'
            echo '☸️  Kubernetes (Production): ejecuta "minikube service ipas-service --url"'
            echo '📊 Grafana: http://localhost:3000'
            echo '📈 Prometheus: http://localhost:9090'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
        }
        failure {
            echo '❌ Pipeline falló. Revisa los logs.'
            echo 'Ejecuta: docker logs ipas-jenkins'
        }
        always {
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
    }
}
