package org.camunda.community.auth.keycloak.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.camunda.community.auth.keycloak.KeycloakHelper;
import org.keycloak.KeycloakPrincipal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and KeycloakIdentityProviderPlugin.
 */
public class KeycloakSSOAuthenticationProvider extends ContainerBasedAuthenticationProvider {
    private static Logger log = LoggerFactory.getLogger(KeycloakSSOAuthenticationProvider.class);

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {

        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) request.getUserPrincipal();

        if (principal == null) {
        	log.warn("No principal found in request - auth not possible");
            return AuthenticationResult.unsuccessful();
        }
        
        String name = KeycloakHelper.getUsernameFromPrincipal(principal);
        if (name == null || name.isEmpty()) {
        	log.warn("No username found in token - auth not possible");
            return AuthenticationResult.unsuccessful();
        }
        log.debug("Got username {} from token",name);

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
