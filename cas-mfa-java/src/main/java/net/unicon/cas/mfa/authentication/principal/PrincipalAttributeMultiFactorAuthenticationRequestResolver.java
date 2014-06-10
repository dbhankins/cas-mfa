package net.unicon.cas.mfa.authentication.principal;

import net.unicon.cas.mfa.authentication.MultiFactorAuthenticationRequestContext;
import net.unicon.cas.mfa.authentication.MultiFactorAuthenticationRequestResolver;

import net.unicon.cas.mfa.web.support.MfaWebApplicationServiceFactory;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.unicon.cas.mfa.web.support.MultiFactorAuthenticationSupportingWebApplicationService.AuthenticationMethodSource;

/**
 * Implementation of <code>MultiFactorAuthenticationRequestResolver</code> that resolves
 * potential mfa request based on the configured principal attribute.
 * <p/>
 * Note: It is assumed that the attribute value that specifies the
 * authentication method at this time is a single-valued attribute.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
public class PrincipalAttributeMultiFactorAuthenticationRequestResolver implements
        MultiFactorAuthenticationRequestResolver {

    /**
     * The logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Principal attribute name for requested mfa method.
     * Default value if not provided via constructor is <i>authn_method</i>
     */
    private final String mfaMethodAttributeName;

    /**
     * Mfa service factory.
     */
    private final MfaWebApplicationServiceFactory mfaServiceFactory;

    /**
     * Default principal attribute name for retrieving requested mfa authentication method.
     */
    public static final String DEFAULT_MFA_METHOD_ATTRIBUTE_NAME = "authn_method";

    /**
     * Ctor.
     *
     * @param mfaServiceFactory mfaServiceFactory
     */
    public PrincipalAttributeMultiFactorAuthenticationRequestResolver(final MfaWebApplicationServiceFactory mfaServiceFactory) {
        this(DEFAULT_MFA_METHOD_ATTRIBUTE_NAME, mfaServiceFactory);
    }

    /**
     * Ctor.
     *
     * @param mfaMethodAttributeName mfaMethodAttributeName
     * @param mfaServiceFactory mfaServiceFactory
     */
    public PrincipalAttributeMultiFactorAuthenticationRequestResolver(final String mfaMethodAttributeName,
                                                                      final MfaWebApplicationServiceFactory mfaServiceFactory) {
        this.mfaMethodAttributeName = mfaMethodAttributeName;
        this.mfaServiceFactory = mfaServiceFactory;
    }

    @Override
    public MultiFactorAuthenticationRequestContext resolve(final Authentication authentication, final WebApplicationService targetService) {
        if ((authentication != null) && (targetService != null)) {
            final String mfaMethod = String.class.cast(authentication.getPrincipal().getAttributes().get(this.mfaMethodAttributeName));

            if (StringUtils.isNotBlank(mfaMethod)) {
                logger.debug("Found mfa attribute [{}] with value [{}] for principal [{}]", this.mfaMethodAttributeName,
                        mfaMethod, authentication.getPrincipal().getId());

                return new MultiFactorAuthenticationRequestContext(this.mfaServiceFactory.create(targetService.getId(), targetService.getId(),
                        targetService.getArtifactId(), mfaMethod, AuthenticationMethodSource.PRINCIPAL_ATTRIBUTE));
            }
        }
        return null;
    }
}
