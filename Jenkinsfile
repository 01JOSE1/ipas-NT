pipeline {
  agent any
  stages {
    stage('Test SSH') {
      steps {
        script {
          sshagent(['deploy-server']) {
            sh "ssh -o StrictHostKeyChecking=no jose@192.168.1.8 'echo Conexión OK desde Jenkins'"
          }
        }
      }
    }
  }
}
