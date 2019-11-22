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

Add Keycloak Servlet Filter Adapter to CATALINA_HOME/lib

```
wget https://repo1.maven.org/maven2/org/keycloak/keycloak-servlet-filter-adapter/8.0.0/keycloak-servlet-filter-adapter-8.0.0.jar
wget https://repo1.maven.org/maven2/org/keycloak/keycloak-servlet-adapter-spi/8.0.0/keycloak-servlet-adapter-spi-8.0.0.jar
```

### Configure camunda-bpm-identity-keycloak Library

Configure the plugin in CATALINA_HOME/conf - to make it configurable (e.g. in containerized environments) it is recommended to reference system properties.

```
<plugin>
        <class>org.camunda.bpm.extension.keycloak.plugin.KeycloakIdentityProviderPlugin</class>
        <properties>

            <property name="keycloakIssuerUrl">${KEYCLOAK_ISSUER_URL}</property>
            <property name="keycloakAdminUrl">${KEYCLOAK_ADMIN_URL}</property>
            <property name="clientId">${KEYCLOAK_CLIENT_ID}</property>
            <property name="clientSecret">${KEYCLOAK_CLIENT_ID}</property>
            <property name="useUsernameAsCamundaUserId">${KC_PLUGIN_USERNAME_AS_ID}true</property>
            <property name="useGroupPathAsCamundaGroupId">${KC_PLUGIN_GROUPPATH_AS_ID}</property>
            <property name="administratorGroupName">${KC_PLUGIN_ADMIN_GROUP}</property>
            <property name="disableSSLCertificateValidation">${KC_PLUGIN_DISABLE_SSL_VALIDATION}</property>
            <property name="authorizationCheckEnabled">${KC_PLUGIN_ENABLE_AUTH_CHECK}</property>

        </properties>
    </plugin>
```

### Create keycloak-adpater configuration file
Get the client keycloak.json file and place it in a central folder, e.g. $CAMUNDA_HOME/conf

### Configure SSO for Camunda-WebApps

* Copy camunda-sso-tomat JAR to CATALINA_HOME/webapps/camunda/WEB-INF/lib
* Modify CATALINA_HOME/webapps/camunda/WEB-INF/web.xml (see src/assembly/camunda-web.xml)

### Configure SSO for Camunda-REST-Engine

* Copy camunda-sso-tomat JAR to CATALINA_HOME/webapps/engine-rest/WEB-INF/lib
* Modify CATALINA_HOME/webapps/engine-rest/WEB-INF/web.xml (see src/assembly/engine-rest-web.xml)

