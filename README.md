# THIS REPOSITORY IS UNMAINTAINED !! 

We switched to Camunda Zeebe and therefore have no need for this integration anymore. Habe a look at https://github.com/camunda-community-hub/camunda-platform-7-keycloak, which supports more recent versions aof camunda.

# Camunda SSO for WebApps / REST-API
Enables SSO to Web-Apps / REST-API. Uses https://github.com/camunda/camunda-bpm-identity-keycloak as Identity-Provider, so there is no need to configure groups in Camunda

Runs as a fully configurable docker-image based on the Tomcat-Distribution of camunda.

## Issues with Camunda Groups and the Keycloak identity-plugin
Unfortunately it is not possible to user Keycloak-Roles only with the keycloak-identity-plugin. Basically it boils down to the fact that keycloak can only retrieve directly assigned users of a role.

See details https://github.com/camunda/camunda-bpm-identity-keycloak/issues/3

Groups alone are not working for Keycloak-Service-Accounts, so we need roles anyway. Our approach is to have both groups and roles with the same name. Roles are added to the corresponding groups (This is a little bit cumbersome)
For those that don't use the REST-API - you can skip the roles alltogether.

## Keycloak Configuration
For Details see: https://github.com/camunda/camunda-bpm-identity-keycloak

AND/OR

* Create confidential client with service-accounts enabled
* Add valid Redirect-URL (Base-URL of your camunda webapp, eg https://my.domain.org/camunda/*
* Give the client the permission to query users&groups and to view users
* Be sure that roles are added to your JWT (this is the default behaviour of Keycloak)
* Create camunda-admin role in your client
* Create Camunda-Admin group, assign the camunda-admin role to it
* Add user(s) to the admin-group

### Service-Account REST-API Client
If you have a "machine" client that has to connect to the REST-API, do the following

* Create confidential client with service-accounts enabled only (no standard flow)
* enable service-accounts
* Add the required roles to this service-account
* Be sure that roles are added to your JWT (this is the default behaviour of Keycloak)

The roles have to exists as groups in Keycloak. If you don't have the identity-plugin configured to use goup-names (useGroupPathAsCamundaGroupId), you have send the internal-id of the group in the claim manually.

## Build Docker-Image
Allthough it is possible to run it non-dockerized, this README will focus on how to build the docker-image.

Requirement: Build this PLUGIN and either push it to a maven-repository that is available to you, or build and install it on your local box.

```
 mvn install
 cd ./docker
 make
``` 

This builds a docker-image based on camunda/camunda-bpm-platform:tomcat-7.14.0.

It builds the image with the following name camunda-7.14.x-identity-keycloak:<RELEASE>

### All options to Make

| *Option* | *Description* | *Default* |
| --- | --- |--- |
| `DOCKER_CMD` | Which docker cmd to use| docker|
| `BASE` | Base-Image | camunda/camunda-bpm-platform:tomcat-7.14.0 |
| `CAMUNDA_RELEASE` | Used to get thrid-party-libs depending on camunda-version. Do not touch| 7.14.x|
| `DOCKER_REPO` | Used in target docker_tag to prefix the built image  | some-repo.org|
| `DOCKER_ORG` | Used in target docker_tag to prefix the built image  | witcom|
| `DOCKER_RELEASE` | Tag for the built image  | 1.3 |
| `DOCKER_BUILD_ARGS` | Additional build-args | - |
| `IDENTITY_KEYCLOAK_RELEASE` | Version of identity-plugin to use  | 2.0.0 |
| `AUTH_KEYCLOAK_RELEASE` | Version of this plugin to use  | 1.3 |


## Run
Just pass the needed ENV-Vars. See Configuration 

```
docker run --rm -p 8080:8080 \
 -e KEYCLOAK_BASE_URL=https://xxx/auth \
 -e KEYCLOAK_REALM=witcom \
 -e KEYCLOAK_CLIENT_ID=camunda-resource-server \
 -e KEYCLOAK_CLIENT_SECRET=SECRET \
 camunda-7.14.x-identity-keycloak:1.3.0
```

### Configuration
The SSO-Plugin can be configured by setting env-variables (except the keycloak-client-configuration, which has to done with the keycloak.json file) In the docker-image this is done automagically ;-)

It re-uses some of the variables introduced by camunda-bpm-identity-keycloak

| *Variable* | *Description* | *Used in camunda-bpm-identity-keycloak ?* |
| --- | --- |--- |
| `KEYCLOAK_EMAIL_AS_ID` | Whether to use the Keycloak email attribute as Camunda's user ID. Default is `false`.<br/>Both keycloak-identity-service AND SSO-Plugin have to be configured identical.| YES|
| `KEYCLOAK_USERNAME_AS_ID` | Whether to use the Keycloak preferred-username attribute as Camunda's user ID. Default is `false`. In the default case the plugin will use the internal Keycloak ID as Camunda's user ID.<br/>Both keycloak-identity-service AND SSO-Plugin have to be configured identical.<br/>*Note*:takes precedence over KEYCLOAK_EMAIL_AS_ID | YES|
| `KEYCLOAK_CLIENT_ID` | Camunda Keycloak-Resource-server| YES|
| `KEYCLOAK_FILTER_CLAIM_GROUPS` | Defines in which claim of the Access-Token the client's groups are listed. You only need that if you don't use keycloak default roles  | NO|

