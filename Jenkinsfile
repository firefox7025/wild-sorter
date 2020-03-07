podTemplate(yaml: """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: builder
spec:
  containers:
  - name: gradle
    image: adoptopenjdk/openjdk11
    command:
    - cat
    tty: true
"""
  ) {

  node(POD_LABEL) {
    stage('Build and test') {
    checkout scm
      container('gradle') {
        sh 'chmod 777 gradlew'
        sh './gradlew clean build'
      }
    }
  }
}