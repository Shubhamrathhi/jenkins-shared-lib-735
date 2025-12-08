// Global function: credScan
// Scans repo for secrets using Gitleaks
def call(Map config = [:]) {

    def reportFile = config.reportFile ?: "gitleaks-report.json"
    def exitCodeFile = "exit_code.txt"

    pipeline {
        agent any

        stages {
            stage('Install Gitleaks') {
                steps {
                    echo "Installing Gitleaks..."
                    sh '''
                        wget https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_arm64.tar.gz -O gitleaks.tar.gz
                        tar -xzf gitleaks.tar.gz
                        sudo mv gitleaks /usr/local/bin/gitleaks || true
                        sudo chmod +x /usr/local/bin/gitleaks
                        gitleaks version
                    '''
                }
            }

            stage('Run Scan') {
                steps {
                    echo "Running Gitleaks Scan..."
                    sh """
                        gitleaks detect --source . --report-path ${reportFile} || echo \$? > ${exitCodeFile}
                        test -f ${exitCodeFile} || echo 0 > ${exitCodeFile}
                    """
                }
            }

            stage('Archive Report') {
                steps {
                    archiveArtifacts artifacts: reportFile, fingerprint: true
                    archiveArtifacts artifacts: exitCodeFile, fingerprint: true
                }
            }

            stage('Validate Result') {
                steps {
                    script {
                        def exitCode = readFile(exitCodeFile).trim()
                        if (exitCode != "0") {
                            currentBuild.result = "FAILURE"
                            error("❌ Secrets detected! Exit code: ${exitCode}")
                        } else {
                            echo "✅ No secrets found!"
                        }
                    }
                }
            }
        }

        post {
            always { sh "rm -f gitleaks.tar.gz || true" }
        }
    }
}
