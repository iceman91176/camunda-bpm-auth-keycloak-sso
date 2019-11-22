package org.camunda.community.auth.keycloak.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.util.EngineUtil;
import org.camunda.community.auth.keycloak.KeycloakHelper;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and KeycloakIdentityProviderPlugin.
 */
public class KeycloakAuthenticationFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(KeycloakAuthenticationFilter.class);

    private String claimGroups = "groupIds";

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Init KeycloakAuthenticationFilter");
        //Set group claim from env if available
        if (System.getenv("KC_FILTER_CLAIM_GROUPS")!=null &&
       		 !System.getenv("KC_FILTER_CLAIM_GROUPS").isEmpty()) {
        	this.claimGroups = System.getenv("KC_FILTER_CLAIM_GROUPS");
        	log.debug("Getting camunda-groups form claim {}",this.claimGroups);
       }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ProcessEngine engine = EngineUtil.lookupProcessEngine("default");
        final HttpServletRequest req = (HttpServletRequest) request;
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) req.getUserPrincipal();

        if (principal == null) {
            log.warn("Principal is null - auth not possible");
            clearAuthentication(engine);
            return;
        }
        
        String name = KeycloakHelper.getUsernameFromPrincipal(principal);
        if (name == null || name.isEmpty()) {
            log.warn("Username is null - auth not possible");
            clearAuthentication(engine);
            return;

        }
        log.debug("Got username "+name+" from token");
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        
        try {
            engine.getIdentityService().setAuthentication(name, getUserGroups(accessToken));
            chain.doFilter(request, response);
        } finally {
            clearAuthentication(engine);
        }


    }

    @Override
    public void destroy() {

    }

    private void clearAuthentication(ProcessEngine engine) {
        engine.getIdentityService().clearAuthentication();
    }

    /**
     * Get user groups from Access-Token claims
     * It is not possible to get the groups from the keycloak-identity-plugin
     * because in case of a keycloak-client that performs the the api-call, the user-id
     * is not a real keycloak-user
     * 
     * @param accessToken
     * @return Array-List of groups
     */
    @SuppressWarnings("unchecked")
	private List<String> getUserGroups(AccessToken accessToken){

        List<String> groupIds = new ArrayList<String>();
        Map<String, Object> otherClaims = accessToken.getOtherClaims();
        if (otherClaims.containsKey(claimGroups)) {
		    groupIds = (ArrayList<String>) otherClaims.get(claimGroups);
            log.debug("Found groups in token " + groupIds.toString());
        }
        return groupIds;
    }


}
