package com.mes.gotogether.security.config.oauthconfig;

import org.springframework.core.env.Environment;



public class OAuth2LoginConfig {


    private Environment env;

    private String basePropertyName = "spring.security.oauth2.client.registration.";

/*
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryReactiveClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        basePropertyName += "google.";
        return ClientRegistration.withRegistrationId("google")
                .clientId(env.getProperty(basePropertyName +"client-id"))
                .clientSecret(env.getProperty(basePropertyName +"client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(env.getProperty(basePropertyName +"redirect-uri"))
                .scope(env.getProperty(basePropertyName +"scope"))
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }*/
}
