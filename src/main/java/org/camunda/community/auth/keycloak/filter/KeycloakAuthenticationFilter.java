package org.camunda.community.auth.keycloak.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
 * OAuth2 Authentication Filter for usage with Keycloak. Protects REST-ENGINE.
 */
public class KeycloakAuthenticationFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(KeycloakAuthenticationFilter.class);

    private String claimGroups = null;
    private boolean groupsFromClaim = false;
    private String camundaResourceServer = "";

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Init KeycloakAuthenticationFilter");
        //Set group claim from env if available
        if (System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS")!=null &&
       		 !System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS").isEmpty()) {
        	this.claimGroups = System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS");
        	this.groupsFromClaim=true;
        	log.debug("Getting camunda-groups from claim {}",this.claimGroups);
       } else if (System.getenv("KEYCLOAK_CLIENT_ID")!=null &&
          		 !System.getenv("KEYCLOAK_CLIENT_ID").isEmpty()) {
    	   this.camundaResourceServer = System.getenv("KEYCLOAK_CLIENT_ID");
    	   log.debug("Getting camunda-groups from resource-server {} roles",this.camundaResourceServer);
       } else {
    	   log.warn("Neither KEYCLOAK_FILTER_CLAIM_GROUPS nor KEYCLOAK_CLIENT_ID are configured - we won't be able to get groups from JWTs");
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
        log.debug("Got principal ",principal.toString());

        
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
     * Get user groups from Access-Token claims or from resource-access
     * 
     * It is not always possible to get the groups from the keycloak-identity-plugin
     * because in case of a keycloak-service-account  that performs the the api-call, the user-id
     * is not a real user
     * 
     * @param accessToken
     * @return Array-List of groups
     */
    @SuppressWarnings("unchecked")
	private List<String> getUserGroups(AccessToken accessToken){

        List<String> groupIds = new ArrayList<String>();

        //Get groups from claim
        if(this.groupsFromClaim) {
	        Map<String, Object> otherClaims = accessToken.getOtherClaims();
	        if (otherClaims.containsKey(claimGroups)) {
			    groupIds = (ArrayList<String>) otherClaims.get(claimGroups);
	            log.debug("Found groups in token " + groupIds.toString());
	        }
        } else {
	        //extract groups from resource_access roles        
	        if(accessToken.getResourceAccess().containsKey(this.camundaResourceServer)) {
	        	Set<String> roles = accessToken.getResourceAccess(this.camundaResourceServer).getRoles();
	        	groupIds = (ArrayList<String>) roles.stream().collect(Collectors.toList());
	        	log.debug("Found groups in resource-access " + groupIds.toString());
	        }
        }
        if (groupIds.isEmpty()) {
        	log.warn("Found no groups in JWT");
        }
        return groupIds;
    }


}
