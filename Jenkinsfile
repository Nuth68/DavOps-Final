pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        PROJECT_NAME      = 'Football Terrain Rental'
        ALERT_CC_EMAIL    = 'pravevinuth888@gmail.com'
        // Jenkins on macOS doesn't include Homebrew paths — add them explicitly
        PATH              = "/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:${env.PATH}"
        JAVA_HOME         = '/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                script {
                    sh 'chmod +x ./mvnw'
                    sh 'echo "PATH=$PATH"; echo "JAVA_HOME=$JAVA_HOME"; which java; java -version 2>&1; which ansible-playbook'

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
                    env.EMAIL_RECIPIENTS = "${env.GIT_COMMITTER_EMAIL}, ${env.ALERT_CC_EMAIL}"
                    echo "Last commit by: ${env.GIT_COMMITTER_NAME} <${env.GIT_COMMITTER_EMAIL}>"
                    echo "Message: ${env.GIT_COMMIT_MSG}"
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Building project with Maven (compile only)...'
                sh './mvnw clean compile -q'
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.EMAIL_RECIPIENTS,
                            subject: "[BUILD FAILED] ${PROJECT_NAME}",
                            body: """BUILD FAILURE — ${PROJECT_NAME}

Commit:  ${GIT_COMMIT_MSG}
Author:  ${GIT_COMMITTER_NAME} <${GIT_COMMITTER_EMAIL}>
Branch:  ${GIT_BRANCH}
Jenkins: ${BUILD_URL}

Check console output for details.""",
                            mimeType: 'text/plain',
                            attachLog: true
                        )
                    }
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests with H2 in-memory test database...'
                sh './mvnw test'
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.EMAIL_RECIPIENTS,
                            subject: "[TEST FAILED] ${PROJECT_NAME}",
                            body: """TEST FAILURE — ${PROJECT_NAME}

Commit:  ${GIT_COMMIT_MSG}
Author:  ${GIT_COMMITTER_NAME} <${GIT_COMMITTER_EMAIL}>
Branch:  ${GIT_BRANCH}
Jenkins: ${BUILD_URL}

Tests failed. Check console output.""",
                            mimeType: 'text/plain',
                            attachLog: true
                        )
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo 'Running Ansible playbook to deploy to web server...'
                sh '''
                    cd ansible
                    ansible-playbook -i inventory.ini playbook.yml -v
                '''
            }
            post {
                failure {
                    script {
                        emailext(
                            to: env.EMAIL_RECIPIENTS,
                            subject: "[DEPLOY FAILED] ${PROJECT_NAME}",
                            body: """DEPLOYMENT FAILURE — ${PROJECT_NAME}

Commit:  ${GIT_COMMIT_MSG}
Author:  ${GIT_COMMITTER_NAME} <${GIT_COMMITTER_EMAIL}>
Branch:  ${GIT_BRANCH}
Jenkins: ${BUILD_URL}

Ansible playbook failed. Check console output.""",
                            mimeType: 'text/plain',
                            attachLog: true
                        )
                    }
                }
                success {
                    script {
                        emailext(
                            to: env.EMAIL_RECIPIENTS,
                            subject: "[DEPLOY SUCCESS] ${PROJECT_NAME} deployed",
                            body: """DEPLOYMENT SUCCESS — ${PROJECT_NAME}

Commit:  ${GIT_COMMIT_MSG}
Author:  ${GIT_COMMITTER_NAME} <${GIT_COMMITTER_EMAIL}>
Branch:  ${GIT_BRANCH}
Jenkins: ${BUILD_URL}

Deployed — Test DB: H2 | Prod DB: MySQL (PRAVE_Vinuth-db) | Backup saved.""",
                            mimeType: 'text/plain',
                            attachLog: true
                        )
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "Pipeline FAILED — email sent to ${env.EMAIL_RECIPIENTS}"
        }
        success {
            echo "Pipeline SUCCESS — ${PROJECT_NAME} built, tested, and deployed"
        }
    }
}
