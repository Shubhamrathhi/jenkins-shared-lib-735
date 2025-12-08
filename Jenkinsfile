@Library('jenkins-shared-lib') _  // Name of your shared library in Jenkins

pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-org/secret-cred-scanning.git'
            }
        }

        stage('Setup Environment') {
            steps {
                setupEnv(workspaceDir: "my-workspace")
            }
        }

        stage('Credential Scan') {
            steps {
                credScan(reportFile: "gitleaks-report.json")
            }
        }
    }
}
