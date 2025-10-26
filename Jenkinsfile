pipeline {
    agent any

    tools {
        maven 'Maven-3.9.11'
    }

    environment {
        DOCKER_REGISTRY = "alexlms123"
        // Incluye todos los core y microservicios
        SERVICES = "service-discovery cloud-config api-gateway proxy-client product-service order-service payment-service shipping-service favourite-service user-service"
        K8S_NAMESPACE_DEV = "ecommerce-dev"
        K8S_NAMESPACE_STAGE = "ecommerce-stage"
        K8S_NAMESPACE_MASTER = "ecommerce-master"
        DOCKER_COMPOSE_FILE = "docker-compose-test.yml"
        // Usar timestamp como tag de versión
        BUILD_TIMESTAMP = sh(script: "date +%Y%m%d_%H%M%S", returnStdout: true).trim()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Clonando todo el repositorio...'
                git url: 'https://github.com/Alexlms123/ecommerce-microservice-backend-app.git', branch: 'dev'
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                script {
                    def BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()

                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        env.SERVICES.split().each { service ->
                            echo "Building Docker image for ${service}..."
                            sh """
                                docker build \
                                    -t ${env.DOCKER_REGISTRY}/${service}:${BUILD_TIMESTAMP} \
                                    -f ${service}/Dockerfile \
                                    .
                            """

                            echo "Pushing image for ${service}..."
                            sh """
                                echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                                docker push ${env.DOCKER_REGISTRY}/${service}:${BUILD_TIMESTAMP}
                            """
                        }
                    }
                }
            }
        }


        // PUNTO 3: DEV
        stage('Unit & Integration Tests') {
            when { branch 'dev' }
            steps {
                echo "=== Ejecutando pruebas unitarias y de integración ==="
                sh '''
                   for s in ${SERVICES}; do
                      if [ -d "$s" ]; then
                         cd $s
                         mvn clean test
                         cd ..
                      fi
                   done
                '''
            }
        }

        stage('Docker Compose E2E Tests') {
            when { branch 'dev' }
            steps {
                echo "=== E2E Tests con Docker Compose ==="
                sh '''
                    docker-compose -f ${DOCKER_COMPOSE_FILE} up -d
                    sleep 25
                    mvn test -Dgroups=e2e
                    docker-compose -f ${DOCKER_COMPOSE_FILE} down
                '''
            }
        }

        // PUNTO 4: STAGE
        stage('Deploy Stage + Performance Tests') {
            when { branch 'stage' }
            steps {
                echo "=== Desplegando en Kubernetes Stage ==="
                withCredentials([file(credentialsId: 'kubeconfig-stage', variable: 'KUBECONFIG')]) {
                    sh '''
                        export KUBECONFIG=$KUBECONFIG
                        kubectl create namespace ${K8S_NAMESPACE_STAGE} --dry-run=client -o yaml | kubectl apply -f -
                        for s in ${SERVICES}; do
                            kubectl apply -f k8s/$s/ -n ${K8S_NAMESPACE_STAGE} || true
                            kubectl rollout status deployment/$s -n ${K8S_NAMESPACE_STAGE} --timeout=5m
                        done
                        pip install locust
                        locust -f locustfile.py --headless -u 100 --spawn-rate 10 -t 1m --csv=reports/loadtest_${BUILD_TIMESTAMP}
                    '''
                }
            }
        }

        // PUNTO 5: MASTER
        stage('Deploy Master + Release Notes') {
            when { branch 'master' }
            steps {
                echo "=== Desplegando en Kubernetes Master ==="
                withCredentials([file(credentialsId: 'kubeconfig-master', variable: 'KUBECONFIG')]) {
                    sh '''
                        export KUBECONFIG=$KUBECONFIG
                        kubectl create namespace ${K8S_NAMESPACE_MASTER} --dry-run=client -o yaml | kubectl apply -f -
                        for s in ${SERVICES}; do
                            kubectl apply -f k8s/$s/ -n ${K8S_NAMESPACE_MASTER} || true
                            kubectl rollout status deployment/$s -n ${K8S_NAMESPACE_MASTER} --timeout=5m
                        done
                        echo "# Release ${BUILD_TIMESTAMP}" > RELEASE_NOTES.md
                        echo "Fecha: $(date)" >> RELEASE_NOTES.md
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "🧹 Limpieza final"
            sh 'docker-compose -f ${DOCKER_COMPOSE_FILE} down || true'
        }
    }
}
