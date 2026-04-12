pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        APP_NAME = 'quality-gates-api'
        DOCKER_IMAGE = "quality-gates-api:${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Descargando código fuente...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Compilando el proyecto...'
                sh 'mvn clean compile -B'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Ejecutando pruebas unitarias...'
                sh 'mvn test -B'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml',
                         allowEmptyResults: true
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🔗 Ejecutando pruebas de integración...'
                sh 'mvn verify -B -DskipUnitTests=true'
            }
            post {
                always {
                    junit testResults: '**/target/failsafe-reports/*.xml',
                         allowEmptyResults: true
                }
            }
        }

        stage('Code Coverage - JaCoCo') {
            steps {
                echo '📊 Generando reporte de cobertura de código...'
                sh 'mvn jacoco:report -B'
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage('Static Analysis - Checkstyle') {
            steps {
                echo '🔍 Ejecutando análisis estático de código...'
                sh 'mvn checkstyle:checkstyle -B'
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site',
                        reportFiles: 'checkstyle.html',
                        reportName: 'Checkstyle Report'
                    ])
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '🚦 Evaluando Quality Gate...'
                script {
                    echo '✅ Quality Gate: Verificando cobertura mínima del 80%'
                    echo '✅ Quality Gate: Verificando análisis estático (Checkstyle)'
                    echo '✅ Quality Gate: Verificando que todos los tests pasen'

                    // Verificar que JaCoCo check pase (mínimo 80%)
                    sh 'mvn jacoco:check -B'

                    // Verificar Checkstyle
                    sh 'mvn checkstyle:check -B'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Construyendo imagen Docker...'
                script {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                    sh "docker tag ${DOCKER_IMAGE} ${APP_NAME}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Desplegando aplicación...'
                script {
                    // Detener contenedor anterior si existe
                    sh '''
                        docker stop qg_api_deployed 2>/dev/null || echo "No container to stop"
                        docker rm qg_api_deployed 2>/dev/null || echo "No container to remove"
                    '''

                    // Desplegar nueva versión
                    sh """
                        docker run -d --name qg_api_deployed \\
                            -p 8082:8081 \\
                            -e MYSQL_HOST=host.docker.internal \\
                            -e MYSQL_PORT=3307 \\
                            -e MYSQL_DB=quality_gates_db \\
                            -e MYSQL_USER=root \\
                            -e MYSQL_PASSWORD=root123 \\
                            ${APP_NAME}:latest
                    """
                }
            }
        }
    }

    post {
        success {
            echo '''
            ╔══════════════════════════════════════════╗
            ║  ✅ PIPELINE EXITOSO                     ║
            ║  ✅ Todos los Quality Gates aprobados     ║
            ║  ✅ Aplicación desplegada correctamente   ║
            ╚══════════════════════════════════════════╝
            '''
        }
        failure {
            echo '''
            ╔══════════════════════════════════════════╗
            ║  ❌ PIPELINE FALLIDO                     ║
            ║  ❌ Quality Gate NO aprobado              ║
            ║  ❌ Revisar los reportes de errores       ║
            ╚══════════════════════════════════════════╝
            '''
        }
        always {
            echo "Pipeline finalizado: ${currentBuild.result ?: 'SUCCESS'}"
        }
    }
}
