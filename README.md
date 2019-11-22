# Camunda SSO for WebApps / REST-API
Enables SSO to Web-Apps / REST-API. Uses https://github.com/camunda/camunda-bpm-identity-keycloak as Identity-Provider, so there is no need to configure groups in Camunda

## Keycloak Configuration

For Details see: https://github.com/camunda/camunda-bpm-identity-keycloak
OR

* Create confidential client with service-accounts enabled
* Add valid Redirect-URL (Base-URL of your camunda webapp, eg https://my.domain.org/camunda/*
* Give the client the permission to query users&groups and to view users
* Create Camunda-Admin group, and add user(s) to it

### REST-API Clients

If you have REST-API clients only, you have to set the groupmemberships in a claim in the access-token. 

Create confidential client with service-accounts enabled only (no stadard flow)

Create script mapper 

Claim-Name : groupIds

```
var ArrayList = Java.type('java.util.ArrayList');
var list = new ArrayList();
list.add("camunda-special-group");
list.add("camunda-task-reader");
token.setOtherClaims("groupIds",list);
```

Mapper has to be **multivalued**, and has to be added to the access-token

The groups have to exist in Keycloak, otherwise the identity-plugin can't find them.
If you don't have the identity-plugin configured to use goup-names (useGroupPathAsCamundaGroupId),
you have send the internal-id of the group in the claim

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
            <property name="clientSecret">${KEYCLOAK_CLIENT_SECRET}</property>
            <property name="useUsernameAsCamundaUserId">${KC_PLUGIN_USERNAME_AS_ID}true</property>
            <property name="useGroupPathAsCamundaGroupId">${KC_PLUGIN_GROUPPATH_AS_ID}</property>
            <property name="administratorGroupName">${KC_PLUGIN_ADMIN_GROUP}</property>
            <property name="disableSSLCertificateValidation">${KC_PLUGIN_DISABLE_SSL_VALIDATION}</property>
            <property name="authorizationCheckEnabled">${KC_PLUGIN_ENABLE_AUTH_CHECK}</property>

        </properties>
    </plugin>
```

The values KC_PLUGIN_USERNAME_AS_ID and KC_PLUGIN_EMAIL_AS_ID are used in the SSO-Plugin also,
so it makes sense to provide them as ENV-Variables

You can either modify CATALINA_HOME/bin/setenv.sh to set add the system-properties like that

```
CATALINA_OPTS="other stuff -DKC_PLUGIN_USERNAME_AS_ID=$KC_PLUGIN_USERNAME_AS_ID -DKEYCLOAK_CLIENT_SECRET=$KEYCLOAK_CLIENT_SECRET ...."
```

or just modify/set JAVA_OPTS before starting tomcat (which might be the easiest solution for containerized deyployments)

## SSO-Plugin

### Create keycloak-adapter configuration file
Get the client keycloak.json file and place it in a central folder, e.g. $CAMUNDA_HOME/conf

### Configuration
The SSO-Plugin can be configured by setting env-variables (except the keycloak-client-configuration, which has to done with the keycloak.json file)

| *Variable* | *Description* |
| --- | --- |
| `KC_PLUGIN_EMAIL_AS_ID` | Whether to use the Keycloak email attribute as Camunda's user ID. Default is `false`.<br/>Both keycloak-identity-service AND SSO-Plugin have to be configured identical.|
| `KC_PLUGIN_USERNAME_AS_ID` | Whether to use the Keycloak preferred-username attribute as Camunda's user ID. Default is `false`. In the default case the plugin will use the internal Keycloak ID as Camunda's user ID.<br/>Both keycloak-identity-service AND SSO-Plugin have to be configured identical.<br/>*Note*:takes precedence over KC_PLUGIN_EMAIL_AS_ID |
| `KC_FILTER_CLAIM_GROUPS` | For rest-engine filter only. Defines in which claim of the Access-Token the client's groups are listed.. Default is `groupIds`. Useful for keycloak "Service-Accounts" which are no real users, and have no group membership.  |


### Configure SSO for Camunda-WebApps

* Copy camunda-bpm-auth-keycloak-sso JAR to CATALINA_HOME/webapps/camunda/WEB-INF/lib
* Modify CATALINA_HOME/webapps/camunda/WEB-INF/web.xml (see src/assembly/camunda-web.xml)

### Configure SSO for Camunda-REST-Engine

* Copy camunda-bpm-auth-keycloak-sso JAR to CATALINA_HOME/webapps/engine-rest/WEB-INF/lib
* Modify CATALINA_HOME/webapps/engine-rest/WEB-INF/web.xml (see src/assembly/engine-rest-web.xml)

