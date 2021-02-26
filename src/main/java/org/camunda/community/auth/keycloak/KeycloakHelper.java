package org.camunda.community.auth.keycloak;

import org.camunda.community.auth.keycloak.sso.KeycloakSSOAuthenticationProvider;
import org.keycloak.KeycloakPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakHelper {
	
	private static Logger log = LoggerFactory.getLogger(KeycloakSSOAuthenticationProvider.class);
	
	public static String getUsernameFromPrincipal(KeycloakPrincipal<?> principal) {
		
		String name = null;

		boolean useUsernameAsCamundaUserId=Boolean.parseBoolean(System.getenv("KEYCLOAK_USERNAME_AS_ID"));
        boolean useEmailAsCamundaUserId=false;
        if (!useUsernameAsCamundaUserId) {
        	useEmailAsCamundaUserId=Boolean.parseBoolean(System.getenv("KEYCLOAK_EMAIL_AS_ID"));
        }
        if ((!useUsernameAsCamundaUserId) && (!useEmailAsCamundaUserId)) {
        	log.debug("Retrieve username from principal-name (keycloak-id)");
        	name = principal.getName();
        } else if (useUsernameAsCamundaUserId) {
        	log.debug("Retrieve username from preferred-username");
        	name = principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        } else if (useEmailAsCamundaUserId) {
        	log.debug("Retrieve username from eMail-Adress");
        	name = principal.getKeycloakSecurityContext().getToken().getEmail();
        }
       
		return name;
		
	}

}
