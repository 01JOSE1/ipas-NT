pipeline {
    // agent any -> le dice a Jenkins que puede ejecutar este pipeline en cualquier agente disponible.
    agent any
    
    environment {
        // ---------- Variables de entorno (disponibles en todo el pipeline) ----------
        // Credenciales/host puerto para la DB de pruebas (se usan en la etapa de tests)
        DB_TEST_HOST = 'ipas-mysql-test'          // Host (nombre del servicio Docker) para DB de pruebas
        DB_TEST_PORT = '3306'                    // Puerto del servicio MySQL (interno del contenedor)
        DB_TEST_NAME = 'ipas_test'               // Nombre de la BD de pruebas
        DB_TEST_USER = 'ipas_test_user'          // Usuario para la BD de pruebas
        DB_TEST_PASSWORD = 'test_password'       // Contraseña para la BD de pruebas
        
        // Credenciales/host puerto para la BD de producción (se usan al desplegar)
        DB_PROD_HOST = 'ipas-mysql-prod'         // Host para DB de producción
        DB_PROD_PORT = '3306'                    // Puerto de la BD de producción
        DB_PROD_NAME = 'ipas_prod'               // Nombre de la BD de producción
        DB_PROD_USER = 'ipas_prod_user'          // Usuario BD producción
        DB_PROD_PASSWORD = 'prod_password'       // Contraseña BD producción
        
        // Docker: nombre de imagen y tag (tag toma el número de build de Jenkins)
        DOCKER_IMAGE = 'ipas-app'                // Nombre base de la imagen Docker que construiremos
        DOCKER_TAG = "${env.BUILD_NUMBER}"       // Tag dinámico: número de la build actual (ej. 42)
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Muestra en la consola un mensaje informativo
                echo '🔄 Clonando código desde GitHub...'
                // checkout scm -> clona el repositorio configurado en el job (scm) a workspace
                checkout scm
            }
        }
        
        stage('Build con Maven') {
            steps {
                echo '🔨 Compilando proyecto con Maven...'
                // Ejecuta mvn clean compile en la máquina donde se corre el agente (compila el código)
                sh 'mvn clean compile'
            }
        }
        
        stage('Test con JUnit') {
            steps {
                echo '🧪 Ejecutando tests unitarios...'
                // Ejecuta mvn test pasando propiedades para que la app use la BD de pruebas.
                // Aquí se inyectan las variables de entorno definidas arriba.
                sh """
                    mvn test \
                    -Dspring.datasource.url=jdbc:mysql://${DB_TEST_HOST}:${DB_TEST_PORT}/${DB_TEST_NAME} \
                    -Dspring.datasource.username=${DB_TEST_USER} \
                    -Dspring.datasource.password=${DB_TEST_PASSWORD}
                """
            }
            post {
                always {
                    // Publica los resultados de los tests JUnit en Jenkins (informes de surefire)
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Empaquetando aplicación...'
                // Crea el jar empaquetado; -DskipTests evita ejecutar pruebas otra vez aquí
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Construyendo imagen Docker...'
                script {
                    // Construye la imagen Docker usando el Dockerfile del repo
                    // -t ${DOCKER_IMAGE}:${DOCKER_TAG} -> etiqueta la imagen con nombre:tag (ej. ipas-app:42)
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    
                    // Crea (adicionalmente) la etiqueta "latest" apuntando a la misma imagen recién creada
                    // Esto facilita referirse a ipas-app:latest en despliegues posteriores
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }
        
        stage('Deploy to Docker') {
            steps {
                echo '🚀 Desplegando aplicación...'
                script {
                    // Detener contenedor anterior si existe (evita errores si ya había uno corriendo)
                    sh '''
                        docker stop ipas-app || true   # intenta detener; si falla (no existe) continúa
                        docker rm ipas-app || true     # intenta borrar el contenedor anterior
                    '''
                    
                    // Ejecutar nuevo contenedor con la imagen creada en la etapa anterior.
                    // -d -> detach; --network ipas-network -> conecta a la red Docker que usa las BDs
                    // -p 8081:8080 -> expone el puerto 8080 del contenedor en 8081 del host
                    // -e SPRING_DATASOURCE_* -> variables de entorno que la app consumirá para conectarse a BD prod
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
            // Mensaje que se ejecuta si el pipeline termina correctamente
            echo '✅ Pipeline ejecutado exitosamente!'
        }
        failure {
            // Mensaje que se muestra si algo falla en cualquier etapa
            echo '❌ Pipeline falló. Revisa los logs.'
        }
        always {
            // Esto se ejecuta siempre al final: limpia el workspace de Jenkins
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
    }
}
