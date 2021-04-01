ARG BASE=camunda/camunda-bpm-platform:tomcat-7.14.0
FROM $BASE

ARG CAMUNDA_RELEASE

###
# Fix permissions
###
USER root
RUN chown -R 1000:0 /camunda && chmod -Rf 770 /camunda && chmod -Rf 775 /camunda/*.sh && chmod -Rf 775 /camunda/bin/*.sh
USER camunda

ENV KEYCLOAK_USERNAME_AS_ID=true \
    KEYCLOAK_DISABLE_SSL_CERTIFICATE_VALIDATION=true \
    KEYCLOAK_AUTHORIZATION_CHECK_ENABLED=true \
    KEYCLOAK_GROUP_PATH_AS_ID=true \
    KEYCLOAK_ADMINISTRATOR_GROUP_NAME=camunda-admin

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
# "Copy" scripts
#####
COPY scripts/ temp/
RUN cat temp/setenv_add.sh >> bin/setenv.sh && rm temp/setenv_add.sh

#####
# Copy configs
#####
COPY conf/bpm-platform.xml conf/bpm-platform.xml
COPY conf/webapp-sso.xml webapps/camunda/WEB-INF/web.xml
COPY conf/engine-rest.xml webapps/engine-rest/WEB-INF/web.xml

