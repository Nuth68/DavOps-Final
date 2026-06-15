pipeline {
    agent any

    triggers {
        // Poll SCM every 5 minutes
        pollSCM('H/5 * * * *')
    }

    environment {
        APP_DIR           = '/app'
        ANSIBLE_DIR       = 'ansible'
        PROJECT_NAME      = 'Football Terrain Rental'
        ALERT_CC_EMAIL    = 'pravevinuth888@gmail.com'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                script {
                    // Capture the last commit author email for failure notifications
                    env.GIT_COMMITTER_EMAIL = sh(
                        script: "git --no-pager log -1 --format='%ae' HEAD",
                        returnStdout: true
                    ).trim()
                    env.GIT_COMMITTER_NAME = sh(
                        script: "git --no-pager log -1 --format='%an' HEAD",
                        returnStdout: true
                    ).trim()
                    env.GIT_COMMIT_MSG = sh(
                        script: "git --no-pager log -1 --format='%s' HEAD",
                        returnStdout: true
                    ).trim()
                    echo "Last commit by: ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>"
                    echo "Message: ${env.GIT_COMMIT_MSG}"
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Building project with Maven (skip tests for faster build)...'
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo ${JAVA_HOME})
                    ./mvnw clean compile -q 2>&1
                '''
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.GIT_COMMITTER_EMAIL,
                            cc: env.ALERT_CC_EMAIL,
                            subject: "[BUILD FAILED] ${env.PROJECT_NAME} - Build stage",
                            body: """BUILD FAILURE — ${env.PROJECT_NAME}

Commit:  ${env.GIT_COMMIT_MSG}
Author:  ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>
Branch:  ${env.GIT_BRANCH}
Jenkins: ${env.BUILD_URL}

Please check the console output for details.""",
                            mimeType: 'text/plain'
                        )
                    }
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests with H2 in-memory database...'
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo ${JAVA_HOME})
                    ./mvnw test 2>&1
                '''
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.GIT_COMMITTER_EMAIL,
                            cc: env.ALERT_CC_EMAIL,
                            subject: "[TEST FAILED] ${env.PROJECT_NAME} - Test stage",
                            body: """TEST FAILURE — ${env.PROJECT_NAME}

Commit:  ${env.GIT_COMMIT_MSG}
Author:  ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>
Branch:  ${env.GIT_BRANCH}
Jenkins: ${env.BUILD_URL}

Tests failed. Please check the console output for details.""",
                            mimeType: 'text/plain'
                        )
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            when {
                expression {
                    // Only deploy if build and test stages passed
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo 'Running Ansible playbook to deploy to web server...'
                sh '''
                    cd "${ANSIBLE_DIR}"

                    # Ensure SSH key or use ansible password from inventory
                    ansible-playbook -i inventory.ini playbook.yml -v 2>&1
                '''
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.GIT_COMMITTER_EMAIL,
                            cc: env.ALERT_CC_EMAIL,
                            subject: "[DEPLOY FAILED] ${env.PROJECT_NAME} - Ansible deployment",
                            body: """DEPLOYMENT FAILURE — ${env.PROJECT_NAME}

Commit:  ${env.GIT_COMMIT_MSG}
Author:  ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>
Branch:  ${env.GIT_BRANCH}
Jenkins: ${env.BUILD_URL}

Ansible playbook failed. Please check the console output.""",
                            mimeType: 'text/plain'
                        )
                    }
                }
                success {
                    script {
                        emailext(
                            to: env.GIT_COMMITTER_EMAIL,
                            cc: env.ALERT_CC_EMAIL,
                            subject: "[DEPLOY SUCCESS] ${env.PROJECT_NAME} deployed",
                            body: """DEPLOYMENT SUCCESS — ${env.PROJECT_NAME}

Commit:  ${env.GIT_COMMIT_MSG}
Author:  ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>
Branch:  ${env.GIT_BRANCH}
Jenkins: ${env.BUILD_URL}

Application deployed successfully with Ansible.
- Test DB: H2 in-memory
- Prod DB: MySQL (PRAVE_Vinuth-db)
- MySQL backup saved.""",
                            mimeType: 'text/plain'
                        )
                    }
                }
            }
        }
    }

    post {
        // Global failure handler — catches failures from any stage
        failure {
            echo "Pipeline FAILED — notifications sent to committer (${env.GIT_COMMITTER_EMAIL}) and CC (${env.ALERT_CC_EMAIL})"
        }
        success {
            echo "Pipeline SUCCESS — ${env.PROJECT_NAME} built, tested, and deployed"
        }
    }
}
