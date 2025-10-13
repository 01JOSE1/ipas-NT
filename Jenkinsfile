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
        
        // Docker - ⚠ REEMPLAZA TU_USERNAME con tu usuario de Docker Hub (ejemplo: 01jose1)
        DOCKER_REGISTRY = 'docker.io/01jose1'
        DOCKER_IMAGE = 'ipas-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        IMAGE_FULL = "${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonando código desde GitHub.'
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
                    sh """
                        docker build -t ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                echo '📤 Subiendo imagen a Docker Hub...'
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                            docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest
                            docker logout
                        """
                    }
                }
            }
        }
        
        stage('Smoke Test on Docker') {
            steps {
                echo '🔍 Verificando que la aplicación en Docker funciona...'
                script {
                    // Limpiar contenedor previo si existe
                    sh 'docker rm -f ipas-app-test || true'
                    
                    // Levantar contenedor de prueba
                    sh """
                        docker run -d \
                        --name ipas-app-test \
                        --restart always \
                        -p 8081:8080 \
                        --network ipas-network \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://${DB_PROD_HOST}:${DB_PROD_PORT}/${DB_PROD_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=${DB_PROD_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PROD_PASSWORD} \
                        ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                    
                    echo '⏳ Esperando 90 segundos para que la aplicación inicie...'
                    sleep 90
                    
                    sh '''
                        echo "🔍 Iniciando health checks..."
                        for i in {1..30}; do
                            echo "📡 Intento $i/30 - Verificando health endpoint..."
                            
                            if ! docker ps | grep -q ipas-app-test; then
                                echo "❌ Error: El contenedor ipas-app-test no está corriendo"
                                docker ps -a | grep ipas-app-test || echo "Contenedor no existe"
                                exit 1
                            fi
                            
                            HTTP_CODE=$(curl -f -s -o /dev/null -w "%{http_code}" http://ipas-app-test:8080/actuator/health || echo "000")
                            
                            if [ "$HTTP_CODE" = "200" ]; then
                                echo "✅ Health check exitoso - Aplicación respondiendo correctamente"
                                curl -s http://ipas-app-test:8080/actuator/health | head -5
                                exit 0
                            fi
                            
                            echo "⚠ Código HTTP: $HTTP_CODE (esperando 200)"
                            echo "⏳ Esperando 10 segundos antes del siguiente intento..."
                            sleep 10
                        done
                        
                        echo "❌ Health check falló después de 30 intentos (5 minutos)"
                        echo "📋 Últimos logs de la aplicación:"
                        docker logs --tail 50 ipas-app-test
                        docker rm -f ipas-app-test
                        exit 1
                    '''
                }
            }
        }

        stage('Publish build info') {
            steps {
              echo "Imagen publicada: ${IMAGE_FULL}"
              // archivar un pequeño archivo con el nombre de la imagen
              writeFile file: 'image-info.txt', text: "${IMAGE_FULL}\n${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest"
              archiveArtifacts artifacts: 'image-info.txt', fingerprint: true
            }
        }

        stage('Approve deploy to production') {
            steps {
              script {
                // Este input pausa el pipeline hasta que un humano lo apruebe.
                timeout(time: 1, unit: 'HOURS') {
                  input message: "Aprobar despliegue de ${IMAGE_FULL} a producción?", submitter: 'ops,admin'
                }
              }
            }
        }

        stage('Deploy to Kubernetes (remote)') {
                steps {
                    script {
                        def IMAGE = "${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest"
                        echo "🚀 Desplegando ${IMAGE} en Kubernetes (remotamente)..."
            
                        sshagent(credentials: ['deploy-server']) {
                            sh """
                                ssh -tt -o StrictHostKeyChecking=no jose@192.168.1.8 \\
                                "/home/jose/deploy-ipas.sh ${IMAGE} || exit 1"
                            """
                        }
                    }
                }
            }
        }
        
    }


    
    post {
        success {
            echo '✅ ¡Pipeline ejecutado exitosamente!'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo "📦 Build #${BUILD_NUMBER} completado"
            echo "🐳 Imagen: ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo "🐳 Imagen: ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest"
            echo '☸ Para desplegar en Kubernetes, ejecuta:'
            echo '   kubectl set image deployment/ipas-app ipas=${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest'
            echo '   kubectl rollout restart deployment/ipas-app'
            echo '📊 Grafana: http://IP_VM:3000 (user: admin, pass: admin123)'
            echo '📈 Prometheus: http://IP_VM:9090'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
        }
        failure {
            echo '❌ Pipeline falló. Revisa los logs.'
            echo "Ejecuta: docker logs ipas-jenkins"
            echo "Ver logs del stage fallido arriba"
        }
        always {
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
    }
}
