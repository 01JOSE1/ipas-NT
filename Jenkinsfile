pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        echo 'Compilando el proyecto...'
        sh 'mvn clean package -DskipTests' // o mvn clean install
      }
    }

    stage('Test') {
        steps {
            script {
                withCredentials([usernamePassword(credentialsId: 'db-creds', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')]) {
                sh '''
                  export DB_HOST=127.0.0.1
                  export DB_PORT=3306
                  export DB_NAME=ipas_db
                  export DB_USER=root
                  export DB_PASS=admin123
                  mvn test
                '''
                }
            }
        }
    }


    stage('Archive') {
      steps {
        echo 'Archivando artefacto...'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }

    stage('Deploy') {
      steps {
        script {
          // Inyecta las credenciales DB desde Jenkins (db-creds)
          withCredentials([usernamePassword(credentialsId: 'db-creds', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')]) {

            // crear la red si no existe
            sh '''
              docker network inspect mi_red >/dev/null 2>&1 || docker network create mi_red
            '''

            // construir imagen
            sh 'docker build -t miapp:latest .'

            // esperar a que MySQL responda (hasta ~60s)
            sh '''
              echo "Esperando a que MySQL esté listo..."
              for i in $(seq 1 30); do
                if docker exec mysql_container mysqladmin ping -u${DB_USER} -p${DB_PASS} --silent; then
                  echo "MySQL listo"
                  break
                fi
                echo "MySQL no listo, intento ${i}/30. Esperando 2s..."
                sleep 2
              done
            '''

            // parar y remover contenedor anterior y lanzar la app montada en la misma red
            sh '''
              docker stop miapp_container || true
              docker rm miapp_container || true
              docker run -d \
                --network mi_red \
                --restart unless-stopped \
                -e DB_HOST=mysql_container \
                -e DB_PORT=3306 \
                -e DB_NAME=mi_base \
                -e DB_USER=${DB_USER} \
                -e DB_PASS=${DB_PASS} \
                -p 8080:8080 \
                --name miapp_container \
                miapp:latest
            '''
          } // withCredentials
        } // script
      } // steps
    } // stage Deploy
  } // stages
}
