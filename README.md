# brms-java-client-api
A sample Java client accessing BRMS Execution Engine thru REST. Includes a JUnit TestCase.

## BRMS JVM tweaks and BRMS environment setup - <jboss-eap-7>/bin/standalone.conf
```
JAVA_OPTS="$JAVA_OPTS -Dorg.kie.server.location=http://localhost:8080/kie-server/services/rest/server -Dorg.kie.server.id=default-kie-server -Dorg.kie.server.controller=http://localhost:8080/business-central/rest/controller -Dorg.kie.server.controller.user=admin -Dorg.kie.server.controller.pwd=admin123 -Dorg.kie.server.user=admin -Dorg.kie.server.pwd=admin123"
````
