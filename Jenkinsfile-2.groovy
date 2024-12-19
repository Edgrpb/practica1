pipeline {
    agent none 

    stages {
        stage('Clonar repositorio') {
            agent { label 'linux-agent' }  // Cambié el agente a 'linux-agent'
            steps {
                echo "Clonando el repositorio..."
                git branch: 'main', url: 'https://github.com/Edgrpb/unir-practicas.git'
                stash name: 'repositorio' 
            }
        }

        stage('Build') {
            agent { label 'linux-agent' }  // Cambié el agente a 'linux-agent'
            steps {
                unstash 'repositorio' 
                echo "Ejecutando el Build (no hay nada que compilar)"
                sh "ls"  // Usé 'ls' en lugar de 'dir' porque ahora es en Linux
            }
        }

        stage('Test') {
            parallel {
                stage('Test Unit') {
                    agent { label 'windows-agent' }  // Cambié el agente a 'windows-agent'
                    steps {
                        unstash 'repositorio' 
                        echo "Ejecutando pruebas Unit en el agente Windows"
                        bat '''
                                set PATH=C:\\Users\\Edgar\\AppData\\Local\\Programs\\Python\\Python313\\Scripts;%PATH%
                                set PYTHONPATH=%WORKSPACE%
                                pytest --junitxml=resultado-Unit.xml test\\unit
                            '''
                    }
                }

                stage('Test Rest') {
                    agent { label 'windows-agent' }  // Cambié el agente a 'windows-agent'
                    steps {
                        unstash 'repositorio' 
                        echo "Ejecutando pruebas Rest en el agente Windows"
                        bat '''
                                set PATH=C:\\Users\\Edgar\\AppData\\Local\\Programs\\Python\\Python313\\Scripts;%PATH%
                                start cmd /c flask --app app\\api.py run
                                start cmd /c java -jar "C:\\Users\\Edgar\\OneDrive - Anadat Technology\\Escritorio\\Devops\\DevOps & Cloud (PADEVCLO) - PER 11647\\Actividades\\caso practico 1\\wiremock-standalone-3.10.0.jar" --port 9090 --root-dir "C:\\Users\\Edgar\\OneDrive - Anadat Technology\\Escritorio\\Devops\\DevOps & Cloud (PADEVCLO) - PER 11647\\Actividades\\caso practico 1\\helloworld\\test\\wiremock"
                                pytest --junitxml=resultado-Rest.xml test\\rest
                                
                            '''
                }
            }
        }

        stage('Resultado') {
            agent { label 'linux-agent' }  // Cambié el agente a 'linux-agent'
            steps {
                echo "Recuperando resultados de las pruebas..."
                junit '**/resultado*.xml' 
            }
        }
    }
}
