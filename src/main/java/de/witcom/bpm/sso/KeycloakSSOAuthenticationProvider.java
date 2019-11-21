package de.witcom.bpm.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.keycloak.KeycloakPrincipal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and KeycloakIdentityProviderPlugin.
 */
public class KeycloakSSOAuthenticationProvider extends ContainerBasedAuthenticationProvider {
    private static Log log = LogFactory.getLog(KeycloakSSOAuthenticationProvider.class);

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {

        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) request.getUserPrincipal();

        if (principal == null) {
            return AuthenticationResult.unsuccessful();
        }

        String name = principal.getKeycloakSecurityContext().getToken().getPreferredUsername();

        if (name == null || name.isEmpty()) {
            return AuthenticationResult.unsuccessful();
        }
        log.debug("Got username "+name+" from token");

        // Authentication successful
        AuthenticationResult authenticationResult = new AuthenticationResult(name, true);
        authenticationResult.setGroups(getUserGroups(name, engine));

        return authenticationResult;



    }

    private List<String> getUserGroups(String userId, ProcessEngine engine){
        List<String> groupIds = new ArrayList<>();
        // query groups using KeycloakIdentityProvider plugin
        engine.getIdentityService().createGroupQuery().groupMember(userId).list()
        	.forEach( g -> groupIds.add(g.getId()));
        return groupIds;
    }


}
