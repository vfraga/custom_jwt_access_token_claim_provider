## Custom JWT Access Token Claim Provider (IS 7.1.0)

This project includes an implementation of the `JWTAccessTokenClaimProvider` class to include additional claims to the
JWT access token claim set from the URL parameters in HTTP request's query string.

**Important:** Since the custom claim values doesn't actually exist in the user store, they won't show up in the ID token or the userinfo response.

---

### Implementation Steps:

1. Create a maven project with a custom class implementing the `JWTAccessTokenClaimProvider` class, and overriding the `getAdditionalClaims` methods.
2. Add the necessary dependencies in the `pom.xml` (e.g., `org.wso2.carbon.identity.inbound.auth.oauth2:org.wso2.carbon.identity.oauth`).
    - The correct dependency versions can be found in the WSO2 Identity Server's `pom.xml` file in the `product-is` [repository](https://github.com/wso2/product-is/blob/v7.1.0/pom.xml#L2457).
3. Make the Maven project an OSGi bundle by adding the necessary plugins (e.g., `maven-bundle-plugin` and `maven-scr-plugin`) and dependencies (e.g., `org.apache.felix.scr.ds-annotations`),
 creating an internal package with a bundle activator class with methods with the necessary OSGi Java annotations (e.g., `@Component`, `@Activate`, and `@Deactivate`),
 adding the necessary required OSGi bundle dependencies to the `Import-Package`, and mapping the remaining configuration as per the project structure. 
4. Include the logic to pull data from the HTTP request wrapper and add it to the returning `Map` object.
5. Build the Maven project (e.g., `mvn clean install`).

---

### Configuration:
1. Copy the JAR file from the `<PROJECT_HOME>/target` folder to the `<IS_HOME>/repository/components/dropins` directory.
2. Start the WSO2 Identity Server.
3. [Create a custom attribute](https://is.docs.wso2.com/en/7.1.0/guides/users/attributes/manage-attributes/#add-custom-attributes) for each of the claims you want to add to the JWT claim set.
   For example, if you want to add `custom_claim_1` and `custom_claim_2`, create the attribute with the same name.
4. Include the new [OIDC claims](https://is.docs.wso2.com/en/7.1.0/guides/users/attributes/manage-oidc-attribute-mappings/) to an existing or new [OIDC scope](https://is.docs.wso2.com/en/7.1.0/guides/users/attributes/manage-scopes/) (the application must include the scope in the authorisation/token flow).
5. [Create a new Service Provider or edit an existing one](https://is.docs.wso2.com/en/7.1.0/guides/applications/), and [include the OIDC scope in the Service Provider's OIDC scopes](https://is.docs.wso2.com/en/7.1.0/guides/authentication/user-attributes/enable-attributes-for-oidc-app/#select-user-attributes).
6. As an alternative, you can define the custom OIDC claims under the [JWT token issuer access token attributes](https://is.docs.wso2.com/en/7.1.0/references/app-settings/oidc-settings-for-app/#:~:text=Access%20Token%20Attributes), which don't require adding the claims to OIDC scopes.

---

### Logging:

For this component's logs to be printed, you need to do the following steps in to the `<IS_HOME>/repository/conf/log4j2.properties` file:

1. Create a [Log4J2 Logger](https://logging.apache.org/log4j/2.x/manual/configuration.html#configuring-loggers) named `org-sample` mapped to the `org.sample` package:
   ```properties
   logger.org-sample.name = org.sample
   logger.org-sample.level = DEBUG
   ```
2. Add the new `org-sample` logger to the `loggers` variable:
   ```properties
   loggers = AUDIT_LOG, . . ., org-sample
   ```

#### Example output:
```
. . . WARN {org.sample.token.claim.provider.CustomJWTClaimProvider} - Both client channel and client version are null or empty. Not adding to JWT claim set.
. . . WARN {org.sample.token.claim.provider.CustomJWTClaimProvider} - Client channel is null or empty. Not adding to JWT claim set.
. . . WARN {org.sample.token.claim.provider.CustomJWTClaimProvider} - Client version is null or empty. Not adding to JWT claim set.
```
