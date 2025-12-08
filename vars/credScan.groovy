def call(Map config = [:]) {
    def reportFile = config.reportFile ?: "gitleaks-report.json"
    def exitCodeFile = "exit_code.txt"

    // 1️⃣ Install Gitleaks
    echo "Installing Gitleaks..."
    sh '''
        wget https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_arm64.tar.gz -O gitleaks.tar.gz
        tar -xzf gitleaks.tar.gz
        sudo mv gitleaks /usr/local/bin/gitleaks || true
        sudo chmod +x /usr/local/bin/gitleaks
        gitleaks version
    '''

    // 2️⃣ Run Gitleaks scan in the Jenkins workspace (repo root)
    echo "Running Gitleaks Scan..."
    sh """
        cd ${env.WORKSPACE}
        gitleaks detect --source . --report-path ${reportFile} || echo \$? > ${exitCodeFile}
        test -f ${exitCodeFile} || echo 0 > ${exitCodeFile}
    """

    // 3️⃣ Archive the report
    archiveArtifacts artifacts: "${env.WORKSPACE}/${reportFile}", fingerprint: true
    archiveArtifacts artifacts: "${env.WORKSPACE}/${exitCodeFile}", fingerprint: true

    // 4️⃣ Validate result
    script {
        def exitCode = readFile("${env.WORKSPACE}/${exitCodeFile}").trim()
        if (exitCode != "0") {
            currentBuild.result = "FAILURE"
            error("❌ Secrets detected! Exit code: ${exitCode}")
        } else {
            echo "✅ No secrets found!"
        }
    }

    // Clean up
    sh "rm -f gitleaks.tar.gz || true"
}
