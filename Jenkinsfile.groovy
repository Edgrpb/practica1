pipeline {
    agent any
    
   

    stages {
        stage('Clonar repositorio') {
            steps {
                git branch: 'main', url: 'https://github.com/Edgrpb/unir-practicas.git'
            }
        }
        
        stage('Build'){
            steps {
                    echo 'no hay nada que copilar'
                    bat "dir"
                
              
            }
        }
        
        stage('Test'){
            parallel {
                    stage('Unit'){
                        steps {
                          catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            bat '''
                                set PATH=C:\\Users\\Edgar\\AppData\\Local\\Programs\\Python\\Python313\\Scripts;%PATH%
                                set PYTHONPATH=%WORKSPACE%
                                pytest --junitxml=resultado-Unit.xml test\\unit
                            '''
                          }
                        }
                    }
                    
                    
                    stage('Rest'){
                        steps {
                          catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            bat '''
                                set PATH=C:\\Users\\Edgar\\AppData\\Local\\Programs\\Python\\Python313\\Scripts;%PATH%
                                start cmd /c flask --app app\\api.py run
                                start cmd /c java -jar "C:\\Users\\Edgar\\OneDrive - Anadat Technology\\Escritorio\\Devops\\DevOps & Cloud (PADEVCLO) - PER 11647\\Actividades\\caso practico 1\\wiremock-standalone-3.10.0.jar" --port 9090 --root-dir "C:\\Users\\Edgar\\OneDrive - Anadat Technology\\Escritorio\\Devops\\DevOps & Cloud (PADEVCLO) - PER 11647\\Actividades\\caso practico 1\\helloworld\\test\\wiremock"
                                pytest --junitxml=resultado-Rest.xml test\\rest
                                
                            '''
                          }
                          
                        }
                    }
            }
        }
        stage('resultado') {
            steps {
                junit 'resultado*.xml'
            }
        }
    }
}
