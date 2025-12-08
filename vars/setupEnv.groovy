// vars/setupEnv.groovy
def call(Map config = [:]) {
    def workspaceDir = config.workspaceDir ?: "workspace"

    echo "Creating workspace: ${workspaceDir}"
    sh "mkdir -p ${workspaceDir} || true"
    script { env.WORKSPACE_DIR = workspaceDir }
}
