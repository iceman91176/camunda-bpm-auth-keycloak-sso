package org.camunda.community.auth.keycloak;

import org.keycloak.KeycloakPrincipal;

public class KeycloakHelper {
	
	public static String getUsernameFromPrincipal(KeycloakPrincipal<?> principal) {
		String name = null;
		boolean useUsernameAsCamundaUserId=Boolean.parseBoolean(System.getenv("KC_PLUGIN_USERNAME_AS_ID"));
        boolean useEmailAsCamundaUserId=false;
        if (!useUsernameAsCamundaUserId) {
        	useEmailAsCamundaUserId=Boolean.parseBoolean(System.getenv("KC_PLUGIN_EMAIL_AS_ID"));
        }
        
        if ((!useUsernameAsCamundaUserId) && (useEmailAsCamundaUserId)) {
        	name = principal.getName();
        } else if (useUsernameAsCamundaUserId) {
        	name = principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        } else if (useEmailAsCamundaUserId) {
        	name = principal.getKeycloakSecurityContext().getToken().getEmail();
        }
        
		return name;
		
	}

}
