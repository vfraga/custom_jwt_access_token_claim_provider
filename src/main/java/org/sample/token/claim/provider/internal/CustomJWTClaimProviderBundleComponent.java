package org.sample.token.claim.provider.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.sample.token.claim.provider.CustomJWTClaimProvider;
import org.wso2.carbon.identity.oauth2.token.handlers.claims.JWTAccessTokenClaimProvider;


@Component(
        name = "custom.jwt.claim.provider.bundle",
        immediate = true)
public class CustomJWTClaimProviderBundleComponent {
    private static final Log log = LogFactory.getLog(CustomJWTClaimProviderBundleComponent.class);
    private ServiceRegistration<?> registration;

    @Activate
    protected void activate(final ComponentContext context) {
        try {
            registration = context.getBundleContext().registerService(JWTAccessTokenClaimProvider.class.getName(),
                    new CustomJWTClaimProvider(), null);
            if (registration != null) {
                log.info("CustomJWTClaimProvider successfully registered as a JWTAccessTokenClaimProvider.");
            } else {
                log.error("Failed to register CustomJWTClaimProvider.");
            }
        } catch (Exception e) {
            log.error("Error while registering CustomJWTClaimProvider as a JWTAccessTokenClaimProvider.", e);
        }
    }

    @Deactivate
    protected void deactivate(final ComponentContext ignored) {
        if (registration != null) {
            registration.unregister();
            log.info("Custom JWT Claim Provider Bundle stopped.");
        }
    }
}
