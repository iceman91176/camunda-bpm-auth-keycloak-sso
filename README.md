# camunda-sso-tomcat

## Tomcat Preparation

### Add Camunda camunda-bpm-identity-keycloak Library
Get camunda-bpm-identity-keycloak-1.3.0-SNAPSHOT.jar and copy it to CATALINA_HOME/lib/

Add dependencies to CATALINA_HOME/lib/

```
wget https://repo1.maven.org/maven2/org/springframework/spring-web/5.1.8.RELEASE/spring-web-5.1.8.RELEASE.jar
wget https://repo1.maven.org/maven2/org/springframework/spring-beans/5.1.8.RELEASE/spring-beans-5.1.8.RELEASE.jar
wget https://repo1.maven.org/maven2/org/springframework/spring-jcl/5.1.8.RELEASE/spring-jcl-5.1.8.RELEASE.jar
wget https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.9/httpclient-4.5.9.jar
wget https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.11/httpcore-4.4.11.jar
wget https://repo1.maven.org/maven2/commons-codec/commons-codec/1.11/commons-codec-1.11.jar
wget https://repo1.maven.org/maven2/org/springframework/spring-core/5.1.8.RELEASE/spring-core-5.1.8.RELEASE.jar
```

### Add Keycloak-Adapter Libraries

Get [keycloak-tomcat-adapter-dist-8.0.0.tar.gz](https://downloads.jboss.org/keycloak/8.0.0/adapters/keycloak-oidc/keycloak-tomcat-adapter-dist-8.0.0.tar.gz) and extract somewhere. Remove the following files, since they are alreaydy provided

* httpclient-*.jar
* httpcore-*.jar
* commons-codec-*.jar

Copy the remaining jars to CATALINA_HOME/lib/

### Configure camunda-bpm-identity-keycloak Library

Configure the plugin in CATALINA_HOME/conf

```
<plugin>
        <class>org.camunda.bpm.extension.keycloak.plugin.KeycloakIdentityProviderPlugin</class>
        <properties>

            <property name="keycloakIssuerUrl">https://URL/auth/realms/REALM</property>
            <property name="keycloakAdminUrl">https://URL/auth/admin/realms/REALM</property>
            <property name="clientId">camunda-demo-engine</property>
            <property name="clientSecret">aaaaa-fdc0-4a36-yyyy-b21623f74a52</property>
            <property name="useUsernameAsCamundaUserId">true</property>
            <property name="useGroupPathAsCamundaGroupId">true</property>
            <property name="administratorGroupName">camunda-admin</property>
            <property name="disableSSLCertificateValidation">true</property>

        </properties>
    </plugin>
```

### Configure SSO for Camunda-WebApps

* Copy camunda-sso-tomat JAR to CATALINA_HOME/webapps/camunda/WEB-INF/lib
* Copy keycloak.json (get it from keycloak web-ui) to CATALINA_HOME/webapps/camunda/WEB-INF
* Create CATALINA_HOME/webapps/camunda/META-INF/context.xml (see src/assembly)
* Modify CATALINA_HOME/webapps/camunda/WEB-INF/web.xml (see src/assembly/camunda-web.xml)

