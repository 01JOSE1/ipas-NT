pipeline {
    agent any
    
    options {
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    environment {
        // Variables disponibles en todo el pipeline
        GITHUB_REPO = 'https://github.com/01JOSE1/ipas.git'
        GITHUB_USER = '01JOSE1'
        IMAGE_NAME = 'ipas-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('01 - Checkout (Descargar Código)') {
            steps {
                echo '========== DESCARGANDO CÓDIGO DE GITHUB =========='
                echo "Descargando de: ${GITHUB_REPO}"
                
                checkout(
                    [
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [
                            [
                                url: '${GITHUB_REPO}',
                                credentialsId: 'github-credentials-ipas'
                            ]
                        ]
                    ]
                )
                
                echo "✓ Código descargado exitosamente"
                sh 'ls -la'
            }
        }
        
        stage('02 - Compilar') {
            steps {
                echo '========== COMPILANDO CON MAVEN =========='
                sh '''
                    echo "Compilando proyecto IPAS..."
                    mvn clean compile
                    echo "✓ Compilación exitosa"
                '''
            }
        }
        
        stage('03 - Testear') {
            steps {
                echo '========== EJECUTANDO TESTS UNITARIOS =========='
                sh '''
                    echo "Ejecutando tests..."
                    mvn test
                    echo "✓ Todos los tests pasaron"
                '''
            }
        }
        
        stage('04 - Empaquetar') {
            steps {
                echo '========== GENERANDO JAR CON MAVEN =========='
                sh '''
                    echo "Empaquetando aplicación..."
                    mvn clean package -DskipTests
                    echo "✓ JAR generado exitosamente"
                    ls -lh target/*.jar
                '''
            }
        }
        
        stage('05 - Crear Imagen Docker') {
            steps {
                echo '========== CONSTRUYENDO IMAGEN DOCKER =========='
                sh '''
                    echo "Construyendo imagen Docker..."
                    echo "Nombre: ${IMAGE_NAME}:${DOCKER_TAG}"
                    
                    docker build -t ${IMAGE_NAME}:${DOCKER_TAG} .
                    docker tag ${IMAGE_NAME}:${DOCKER_TAG} ${IMAGE_NAME}:latest
                    
                    echo "✓ Imagen Docker creada exitosamente"
                    docker images | grep ipas-app
                '''
            }
        }
        
        stage('06 - Verificación Final') {
            steps {
                echo '========== VERIFICACIÓN FINAL =========='
                sh '''
                    echo "Verificando imagen Docker..."
                    docker inspect ${IMAGE_NAME}:latest
                    echo "✓ Imagen verificada correctamente"
                '''
            }
        }
    }
    
    post {
        always {
            echo '========== LIMPIEZA DE WORKSPACE =========='
            cleanWs()
        }
        
        success {
            echo '''
            ╔════════════════════════════════════════╗
            ║   ✓ PIPELINE COMPLETADO EXITOSAMENTE   ║
            ╚════════════════════════════════════════╝
            
            Lo que se hizo:
            ✓ Código descargado de GitHub
            ✓ Compilación exitosa
            ✓ Todos los tests pasaron
            ✓ JAR generado
            ✓ Imagen Docker creada
            
            Información de la build:
            - Build Number: ${BUILD_NUMBER}
            - Nombre de imagen: ${IMAGE_NAME}:${DOCKER_TAG}
            - Tag latest: ${IMAGE_NAME}:latest
            
            Próximos pasos:
            1. Ver imagen: docker images | grep ipas-app
            2. Ejecutar: docker-compose up --build
            3. Acceder: http://localhost:8080
            '''
        }
        
        failure {
            echo '''
            ╔═════════════════════════════════════╗
            ║   ✗ PIPELINE FALLÓ                   ║
            ╚═════════════════════════════════════╝
            
            Revisar logs en Jenkins para más detalles.
            Build Number: ${BUILD_NUMBER}
            '''
        }
    }
}
