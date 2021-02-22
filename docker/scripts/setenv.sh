#!/bin/bash

CATALINA_OPTS="";

if [[ -n "${KEYCLOAK_BASE_URL+x}" && -n "${KEYCLOAK_REALM+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_ADMIN_URL=${KEYCLOAK_BASE_URL}/admin/realms/${KEYCLOAK_REALM} -DKEYCLOAK_ISSUER_URL=${KEYCLOAK_BASE_URL}/realms/${KEYCLOAK_REALM}"
fi

if [[ -n "${KEYCLOAK_CLIENT_SECRET+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_CLIENT_SECRET=${KEYCLOAK_CLIENT_SECRET}"
fi

if [[ -n "${KC_FILTER_CLIENT_ID+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_CLIENT_ID=${KC_FILTER_CLIENT_ID}"
fi

if [[ -n "${KC_PLUGIN_USERNAME_AS_ID+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_USERNAME_AS_ID=${KC_PLUGIN_USERNAME_AS_ID}"
fi

if [[ -n "${KEYCLOAK_ADMINISTRATOR_GROUP_NAME+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_ADMINISTRATOR_GROUP_NAME=${KEYCLOAK_ADMINISTRATOR_GROUP_NAME}"
fi

if [[ -n "${KEYCLOAK_DISABLE_SSL_CERTIFICATE_VALIDATION+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_DISABLE_SSL_CERTIFICATE_VALIDATION=${KEYCLOAK_DISABLE_SSL_CERTIFICATE_VALIDATION}"
fi

if [[ -n "${KEYCLOAK_AUTHORIZATION_CHECK_ENABLED+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_AUTHORIZATION_CHECK_ENABLED=${KEYCLOAK_AUTHORIZATION_CHECK_ENABLED}"
fi

if [[ -n "${KC_PLUGIN_USERNAME_AS_ID+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_USERNAME_AS_ID=${KC_PLUGIN_USERNAME_AS_ID}"
fi

if [[ -n "${KEYCLOAK_GROUP_PATH_AS_ID+x}" ]]; then
  CATALINA_OPTS="$CATALINA_OPTS -DKEYCLOAK_GROUP_PATH_AS_ID=${KEYCLOAK_GROUP_PATH_AS_ID}"
fi

echo $CATALINA_OPTS

if [[ -n "${KEYCLOAK_BASE_URL+x}" &&  -n "${KEYCLOAK_REALM+x}" &&  -n "${KEYCLOAK_CLIENT_ID+x}" &&  -n "${KEYCLOAK_CLIENT_SECRET+x}" ]]; then

cat <<EOF > /camunda/webapps/camunda/WEB-INF/keycloak.json
{
  "realm": "${KEYCLOAK_REALM}",
  "auth-server-url": "${KEYCLOAK_BASE_URL}",
  "ssl-required": "external",
  "resource": "${KC_FILTER_CLIENT_ID}",
  "verify-token-audience": true,
  "credentials": {
    "secret": "${KEYCLOAK_CLIENT_SECRET}"
  },
  "use-resource-role-mappings": true,
  "confidential-port": 0
}
EOF

cat <<EOF > /camunda/webapps/engine-rest/WEB-INF/keycloak.json
{
  "realm": "${KEYCLOAK_REALM}",
  "auth-server-url": "${KEYCLOAK_BASE_URL}",
  "ssl-required": "external",
  "resource": "${KC_FILTER_CLIENT_ID}",
  "verify-token-audience": true,
  "credentials": {
    "secret": "${KEYCLOAK_CLIENT_SECRET}"
  },
  "use-resource-role-mappings": true,
  "confidential-port": 0
}
EOF

fi

