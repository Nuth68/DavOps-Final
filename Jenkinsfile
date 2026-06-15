pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        PROJECT_NAME   = 'Football Terrain Rental'
        ALERT_EMAIL    = 'pravevinuth888@gmail.com'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm

                script {
                    sh 'chmod -R +x . || true'

                    // Get git info safely
                    def commitEmail = sh(script: "git log -1 --pretty=format:'%ae' || true", returnStdout: true).trim()
                    def commitName  = sh(script: "git log -1 --pretty=format:'%an' || true", returnStdout: true).trim()
                    def commitMsg   = sh(script: "git log -1 --pretty=format:'%s' || true", returnStdout: true).trim()

                    if (commitEmail == "" || commitEmail == "null") {
                        commitEmail = env.ALERT_EMAIL
                    }

                    env.GIT_COMMITTER_EMAIL = commitEmail
                    env.GIT_COMMITTER_NAME  = commitName
                    env.GIT_COMMIT_MSG      = commitMsg

                    env.EMAIL_RECIPIENTS = "${commitEmail},${env.ALERT_EMAIL}"

                    echo "Committer: ${commitName} <${commitEmail}>"
                    echo "Message: ${commitMsg}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh '''
                        echo "=== BUILD STAGE ==="
                        ls -la

                        if [ -f mvnw ]; then
                            echo "Using Maven Wrapper"
                            chmod +x mvnw
                            ./mvnw clean compile -e
                        else
                            echo "mvnw not found — using system Maven"
                            mvn -v || (echo "ERROR: Maven not installed" && exit 4)
                            mvn clean compile -e
                        fi
                    '''
                }
            }

            post {
                failure {
                    emailext(
                        to: env.EMAIL_RECIPIENTS,
                        subject: "[FAILED BUILD] ${env.PROJECT_NAME}",
                        body: """BUILD FAILED

Project: ${env.PROJECT_NAME}
Commit: ${env.GIT_COMMIT_MSG}
Author: ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>
Branch: ${env.GIT_BRANCH ?: 'unknown'}
Build URL: ${env.BUILD_URL}

Check logs for details.
""",
                        mimeType: 'text/plain',
                        attachLog: true
                    )
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh '''
                        echo "=== TEST STAGE ==="
                        if [ -f mvnw ]; then
                            ./mvnw test
                        else
                            mvn test
                        fi
                    '''
                }
            }

            post {
                failure {
                    emailext(
                        to: env.EMAIL_RECIPIENTS,
                        subject: "[FAILED TEST] ${env.PROJECT_NAME}",
                        body: """TEST FAILED

Project: ${env.PROJECT_NAME}
Commit: ${env.GIT_COMMIT_MSG}
Author: ${env.GIT_COMMITTER_NAME}
Build URL: ${env.BUILD_URL}

Tests failed.
""",
                        attachLog: true
                    )
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                script {
                    sh '''
                        echo "=== DEPLOY STAGE ==="

                        if ! command -v ansible-playbook &>/dev/null; then
                            echo "Installing Ansible..."

                            if command -v apt-get &>/dev/null; then
                                apt-get update -qq
                                apt-get install -y -qq ansible
                            elif command -v pip3 &>/dev/null; then
                                pip3 install ansible
                            else
                                echo "ERROR: Cannot install Ansible"
                                exit 1
                            fi
                        fi

                        ansible-playbook --version

                        if [ -d ansible ]; then
                            cd ansible
                            ansible-playbook -i inventory.ini playbook.yml -v
                        else
                            echo "ERROR: ansible folder not found"
                            exit 1
                        fi
                    '''
                }
            }

            post {
                success {
                    emailext(
                        to: env.EMAIL_RECIPIENTS,
                        subject: "[DEPLOY SUCCESS] ${env.PROJECT_NAME}",
                        body: """DEPLOYMENT SUCCESS

Project: ${env.PROJECT_NAME}
Commit: ${env.GIT_COMMIT_MSG}
Author: ${env.GIT_COMMITTER_NAME}
Build URL: ${env.BUILD_URL}

Deployment completed successfully.
""",
                        attachLog: true
                    )
                }

                failure {
                    emailext(
                        to: env.EMAIL_RECIPIENTS,
                        subject: "[DEPLOY FAILED] ${env.PROJECT_NAME}",
                        body: """DEPLOYMENT FAILED

Project: ${env.PROJECT_NAME}
Commit: ${env.GIT_COMMIT_MSG}
Build URL: ${env.BUILD_URL}

Check Ansible logs.
""",
                        attachLog: true
                    )
                }
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS — ${env.PROJECT_NAME}"
        }

        failure {
            echo "PIPELINE FAILED — emails sent to ${env.EMAIL_RECIPIENTS}"
        }
    }
}