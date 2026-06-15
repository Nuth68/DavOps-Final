pipeline {
    agent any

    // ─── Poll Git every 5 minutes for changes ───────────────────────────────
    triggers {
        pollSCM('H/5 * * * *')
    }

    // ─── Environment variables ──────────────────────────────────────────────
    environment {
        PROJECT_NAME     = 'Football Terrain Rental'
        CC_EMAIL         = 'pravevinuth888@gmail.com'
        ANSIBLE_DIR      = 'ansible'
    }

    stages {

        // ─── Stage 1: Checkout ──────────────────────────────────────────────
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    sh 'chmod +x ./mvnw'
                }
            }
        }

        // ─── Stage 2: Build ─────────────────────────────────────────────────
        stage('Build') {
            steps {
                echo 'Building application with Maven...'
                sh './mvnw clean compile -B -e'
            }
        }

        // ─── Stage 3: Test ──────────────────────────────────────────────────
        stage('Test') {
            steps {
                echo 'Running tests with H2 in-memory test database...'
                sh './mvnw -Dspring.profiles.active=test -B test'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        // ─── Stage 4: Deploy via Ansible ────────────────────────────────────
        stage('Deploy') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo 'Deploying to Web Server via Ansible...'

                script {
                    // Detect Docker and set the right Ansible host
                    def isDocker = sh(
                        script: '[ -f /.dockerenv ] && echo yes || echo no',
                        returnStdout: true
                    ).trim()
                    def ansibleHost = (isDocker == 'yes') ? 'host.docker.internal' : '127.0.0.1'
                    echo "Docker: ${isDocker} → Ansible host: ${ansibleHost}"

                    // Install Ansible if missing, patch inventory, run playbook
                    sh """
                        set +e
                        if ! command -v ansible-playbook &>/dev/null; then
                            apt-get update -qq && apt-get install -y -qq ansible 2>/dev/null || pip3 install ansible 2>/dev/null
                        fi

                        cd ${ANSIBLE_DIR}
                        sed -i "s/ansible_host=[^ ]*/ansible_host=${ansibleHost}/" inventory.ini
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook -i inventory.ini playbook.yml -v
                        ANSIBLE_EXIT=\$?

                        if [ \$ANSIBLE_EXIT -eq 4 ]; then
                            echo 'WARNING: Web server unreachable (containers not running)'
                            echo 'Start with: docker-compose up -d'
                            exit 0
                        elif [ \$ANSIBLE_EXIT -ne 0 ]; then
                            exit \$ANSIBLE_EXIT
                        fi
                    """
                }
            }
        }
    }

    // ─── Post-pipeline notifications ────────────────────────────────────────
    post {
        success {
            echo 'Pipeline completed successfully!'
            emailext(
                subject: "[Jenkins] ✅ SUCCESS: ${PROJECT_NAME} #${BUILD_NUMBER}",
                body: """<p>Build and tests passed. Deploy completed.</p>
<ul>
  <li><b>Project:</b> ${PROJECT_NAME}</li>
  <li><b>Build:</b> #${BUILD_NUMBER}</li>
  <li><b>Branch:</b> ${GIT_BRANCH}</li>
  <li><b>Commit:</b> ${GIT_COMMIT}</li>
</ul>
<p><a href="${BUILD_URL}">View Build</a></p>""",
                mimeType: 'text/html',
                to: env.CC_EMAIL,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        failure {
            echo 'Pipeline FAILED — sending error email...'
            emailext(
                subject: "[Jenkins] ❌ FAILED: ${PROJECT_NAME} #${BUILD_NUMBER}",
                body: """<p>The build has <b>FAILED</b>.</p>
<ul>
  <li><b>Project:</b> ${PROJECT_NAME}</li>
  <li><b>Build:</b> #${BUILD_NUMBER}</li>
  <li><b>Branch:</b> ${GIT_BRANCH}</li>
  <li><b>Commit:</b> ${GIT_COMMIT}</li>
</ul>
<p><a href="${BUILD_URL}console">View Console Output</a></p>""",
                mimeType: 'text/html',
                to: env.CC_EMAIL,
                recipientProviders: [
                    [$class: 'DevelopersRecipientProvider'],
                    [$class: 'RequesterRecipientProvider']
                ]
            )
        }
        unstable {
            echo 'Pipeline UNSTABLE (test failures).'
            emailext(
                subject: "[Jenkins] ⚠️ UNSTABLE: ${PROJECT_NAME} #${BUILD_NUMBER}",
                body: """<p>Build is <b>UNSTABLE</b> — tests failed.</p>
<ul>
  <li><b>Project:</b> ${PROJECT_NAME}</li>
  <li><b>Build:</b> #${BUILD_NUMBER}</li>
  <li><b>Branch:</b> ${GIT_BRANCH}</li>
  <li><b>Commit:</b> ${GIT_COMMIT}</li>
</ul>
<p><a href="${BUILD_URL}testReport">View Test Report</a></p>""",
                mimeType: 'text/html',
                to: env.CC_EMAIL,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}
