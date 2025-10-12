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
                    def registry = "docker.io/01jose1"
                    sh """
                        docker build -t ${registry}/${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${registry}/${DOCKER_IMAGE}:${DOCKER_TAG} ${registry}/${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                echo '📤 Subiendo imagen al registry Docker Hub...'
                script {
                    def registry = "docker.io/01jose1"
                    // 🔐 Login seguro usando credenciales almacenadas en Jenkins
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ${registry}/${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker push ${registry}/${DOCKER_IMAGE}:latest
                        """
                    }
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
        
        
        stage('Deploy to Kubernetes (Production)') {
            steps {
                echo '☸️ Desplegando en Kubernetes (producción)...'
                script {
                    sh """
                        kubectl set image deployment/ipas-app \
                        ipas=docker.io/01jose1/${DOCKER_IMAGE}:latest \
                        -n ${K8S_NAMESPACE}
                    """
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
                    sh "kubectl get pods -l app=ipas -n ${K8S_NAMESPACE}"
                    sh "kubectl get deployment ipas-app -n ${K8S_NAMESPACE}"
                    sh "kubectl get hpa ipas-hpa -n ${K8S_NAMESPACE}"
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ ¡Pipeline ejecutado exitosamente!'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo "📦 Build #${BUILD_NUMBER} desplegado con éxito"
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

