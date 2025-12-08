// Global function: setupEnv
// Setup workspace and environment variables
def call(Map config = [:]) {
    def workspaceDir = config.workspaceDir ?: "workspace"

    pipeline {
        agent any

        stages {
            stage('Setup Environment') {
                steps {
                    echo "Creating workspace: ${workspaceDir}"
                    sh "mkdir -p ${workspaceDir} || true"
                    script { env.WORKSPACE_DIR = workspaceDir }
                }
            }
        }
    }
}
