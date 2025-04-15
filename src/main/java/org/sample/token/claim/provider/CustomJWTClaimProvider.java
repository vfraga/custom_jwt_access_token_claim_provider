package org.sample.token.claim.provider;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.claims.JWTAccessTokenClaimProvider;

import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class CustomJWTClaimProvider implements JWTAccessTokenClaimProvider {
    private static final Log log = LogFactory.getLog(CustomJWTClaimProvider.class);

    @Override
    public Map<String, Object> getAdditionalClaims(final OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext)
            throws IdentityOAuth2Exception {
        return createJWTClaimSet(oAuthAuthzReqMessageContext, null);
    }

    @Override
    public Map<String, Object> getAdditionalClaims(final OAuthTokenReqMessageContext oAuthTokenReqMessageContext)
            throws IdentityOAuth2Exception {
        return createJWTClaimSet(null, oAuthTokenReqMessageContext);
    }

    private Map<String, Object> createJWTClaimSet(final OAuthAuthzReqMessageContext authAuthzReqMessageContext,
                                                  final OAuthTokenReqMessageContext tokenReqMessageContext)
            throws IdentityOAuth2Exception {

        final Map<String, Object> returnObject = new HashMap<>();

        try {
            final HttpServletRequestWrapper requestWrapper;

            if (tokenReqMessageContext != null) {
                // Extracting URL parameters in the query string from the request to the token endpoint
                // e.g., https://localhost:9443/oauth2/token?param1=value1&param2=value2 -> {param1=value1, param2=value2}
                requestWrapper = tokenReqMessageContext.getOauth2AccessTokenReqDTO().getHttpServletRequestWrapper();
            } else if (authAuthzReqMessageContext != null) {
                // Extracting URL parameters in the query string from the request to the authorization endpoint
                // e.g., https://localhost:9443/oauth2/authorize?param1=value1&param2=value2 -> {param1=value1, param2=value2}
                // Important: This code branch will most likely not get executed since there's no claims returned from the authorization endpoint.
                requestWrapper = authAuthzReqMessageContext.getAuthorizationReqDTO().getHttpServletRequestWrapper();
            } else {
                log.error("Unable to extract request wrapper. Returning an empty map.");
                return returnObject;
            }

            final String clientChannel = requestWrapper.getParameter(Constants.CLIENT_CHANNEL);
            final String clientVersion = requestWrapper.getParameter(Constants.CLIENT_VERSION);

            // Only add values if they are present
            if (StringUtils.isNotBlank(clientChannel)) {
                returnObject.put(Constants.CLIENT_CHANNEL, clientChannel);
            } else {
                log.warn("Client channel is null or empty. Not adding to JWT claim set.");
            }

            if (StringUtils.isNotBlank(clientVersion)) {
                returnObject.put(Constants.CLIENT_VERSION, clientVersion);
            } else {
                log.warn("Client version is null or empty. Not adding to JWT claim set.");
            }

        } catch (Exception e) {
            log.error("Error adding custom claims to JWT claim set. Returning default values only.", e);
        }

        return returnObject;
    }

    private static final class Constants {
        private static final String CLIENT_CHANNEL = "clientChannel";
        private static final String CLIENT_VERSION = "clientVersion";
    }
}
