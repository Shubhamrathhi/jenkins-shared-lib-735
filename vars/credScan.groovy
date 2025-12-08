// vars/credScan.groovy
def call(Map config = [:]) {
    def reportFile = config.reportFile ?: "gitleaks-report.json"
    def exitCodeFile = "exit_code.txt"

    echo "Installing Gitleaks..."
    sh '''
        wget https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_arm64.tar.gz -O gitleaks.tar.gz
        tar -xzf gitleaks.tar.gz
        sudo mv gitleaks /usr/local/bin/gitleaks || true
        sudo chmod +x /usr/local/bin/gitleaks
        gitleaks version
    '''

    echo "Running Gitleaks Scan..."
    sh """
        gitleaks detect --source . --no-git --report-path ${reportFile} || echo \$? > ${exitCodeFile}
        test -f ${exitCodeFile} || echo 0 > ${exitCodeFile}
    """

    archiveArtifacts artifacts: reportFile, fingerprint: true
    archiveArtifacts artifacts: exitCodeFile, fingerprint: true

    script {
        def exitCode = readFile(exitCodeFile).trim()
        if (exitCode != "0") {
            currentBuild.result = "FAILURE"
            error("❌ Secrets detected! Exit code: ${exitCode}")
        } else {
            echo "✅ No secrets found!"
        }
    }

    sh "rm -f gitleaks.tar.gz || true"
}
