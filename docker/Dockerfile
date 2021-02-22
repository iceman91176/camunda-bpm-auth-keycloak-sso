FROM camunda/camunda-bpm-platform:tomcat-7.14.0

ARG CAMUNDA_RELEASE

###
# Cleanup unneeded stuff 
###

RUN rm -rf webapps/camunda-invoice webapps/camunda-welcome webapps/manager webapps/host-manager webapps/examples webapps/ROOT 

ENV KC_PLUGIN_USERNAME_AS_ID=true
ENV KEYCLOAK_DISABLE_SSL_CERTIFICATE_VALIDATION=true
ENV KEYCLOAK_AUTHORIZATION_CHECK_ENABLED=true
ENV KC_PLUGIN_USERNAME_AS_ID=true
ENV KEYCLOAK_GROUP_PATH_AS_ID=true
ENV KEYCLOAK_ADMINISTRATOR_GROUP_NAME=camunda-admin

#####
# Add 3rd party libs
#####
COPY third-party-libs/${CAMUNDA_RELEASE}/target/dependency/ lib/

#####
# Add Plugins to webapps
#####
COPY webapp-libs/${CAMUNDA_RELEASE}/target/dependency/ webapps/camunda/WEB-INF/lib/

#####
# Add Plugins to rest-engine
#####
COPY engine-rest-libs/${CAMUNDA_RELEASE}/target/dependency/ webapps/engine-rest/WEB-INF/lib/

#####
# Copy scripts
#####
COPY scripts/ bin/

#####
# Copy config
#####
COPY conf/bpm-platform.xml conf/bpm-platform.xml
COPY conf/webapp-sso.xml webapps/camunda/WEB-INF/web.xml
COPY conf/engine-rest.xml webapps/engine-rest/WEB-INF/web.xml

