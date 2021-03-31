package org.camunda.community.auth.keycloak.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.camunda.community.auth.keycloak.KeycloakHelper;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and
 * KeycloakIdentityProviderPlugin. Used for SSO to Camunda-Webapps 
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
		log.debug("Got username {} from token", name);

		// Authentication successful
		AuthenticationResult authenticationResult = new AuthenticationResult(name, true);
		AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();

		authenticationResult.setGroups(getUserGroups(name, accessToken, engine));

		return authenticationResult;

	}

	/**
	 * Get user groups from JWT - if this does not contain roles (groups), fall back to identity-provider
	 * 
	 * @param userId
	 * @param accessToken
	 * @param engine
	 * @return List of user-groups the user belongs to
	 */
	private List<String> getUserGroups(String userId, AccessToken accessToken, ProcessEngine engine) {
		String claimGroups;
		String camundaResourceServer;
		List<String> groupIds = new ArrayList<>();

		if (System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS") != null
				&& !System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS").isEmpty()) {
			claimGroups = System.getenv("KEYCLOAK_FILTER_CLAIM_GROUPS");

			log.debug("Getting camunda-groups from claim {}", claimGroups);

			Map<String, Object> otherClaims = accessToken.getOtherClaims();
			if (otherClaims.containsKey(claimGroups)) {
				groupIds = (ArrayList<String>) otherClaims.get(claimGroups);
				log.debug("Found groups in token " + groupIds.toString());
			}

		} else if (System.getenv("KEYCLOAK_CLIENT_ID") != null && !System.getenv("KEYCLOAK_CLIENT_ID").isEmpty()) {
			camundaResourceServer = System.getenv("KEYCLOAK_CLIENT_ID");
			log.debug("Getting camunda-groups from roles of resource-server {}", camundaResourceServer);
			if (accessToken.getResourceAccess().containsKey(camundaResourceServer)) {
				Set<String> roles = accessToken.getResourceAccess(camundaResourceServer).getRoles();
				groupIds = (ArrayList<String>) roles.stream().collect(Collectors.toList());
				log.debug("Found groups in resource-access " + groupIds.toString());
			}
		} else {
			log.warn(
					"Neither KEYCLOAK_FILTER_CLAIM_GROUPS nor KEYCLOAK_CLIENT_ID are configured - we won't be able to get groups from JWTs - falling back to identity-provider");

		}
		if (groupIds.isEmpty()) {
			// query groups using KeycloakIdentityProvider plugin
			groupIds = engine.getIdentityService().createGroupQuery().groupMember(userId).list().stream()
					.map(x -> x.getId()).collect(Collectors.toList());
		}

		return groupIds;
	}

}
