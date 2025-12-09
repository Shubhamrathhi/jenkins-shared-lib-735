def call(Map params = [:]) {
    def to = params.get('to', 'shubhamrathee2210@gmail.com')
    def status = params.get('status', 'SUCCESS') // Pass "SUCCESS" or "FAILURE"
    def subject = params.get('subject', "[Jenkins] Credential Scan Notification")

    // Construct body based on scan status
    def body = ""
    if (status == "SUCCESS") {
        body = "✅ Credential scan completed successfully. No secrets were detected in the repository."
    } else if (status == "FAILURE") {
        body = "❌ Credential scan detected secrets or failed. Please check the gitleaks report in Jenkins."
    } else {
        body = "Credential scan completed with status: ${status}"
    }

    try {
        mail(
            to: to,
            subject: subject,
            body: body
        )
        echo "Email sent to ${to} with subject '${subject}' and status '${status}'"
    } catch (Exception e) {
        echo "Failed to send email: ${e.getMessage()}"
    }
}
