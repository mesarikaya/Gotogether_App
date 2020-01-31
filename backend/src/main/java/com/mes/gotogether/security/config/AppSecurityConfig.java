package com.mes.gotogether.security.config;

import com.mes.gotogether.domains.Role;
import com.mes.gotogether.security.jwt.JWTReactiveAuthenticationManager;
import com.mes.gotogether.services.domain.AddressService;
import com.mes.gotogether.services.domain.UserService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class AppSecurityConfig {

    private JWTReactiveAuthenticationManager JWTReactiveAuthenticationManager;
    private SecurityContextRepository securityContextRepository;
    private UserService userService;
    private AddressService addressService;
    private final Environment env;

    public AppSecurityConfig(JWTReactiveAuthenticationManager JWTReactiveAuthenticationManager, 
                                                  SecurityContextRepository securityContextRepository,
                                                  UserService userService, AddressService addressService,
                                                  Environment env) {
        this.JWTReactiveAuthenticationManager = JWTReactiveAuthenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.env = env;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
            .exceptionHandling()
            .authenticationEntryPoint((swe, e) -> {
                log.info("Inside access entry handler");
                    return Mono.fromRunnable(() -> {
                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                });
            })
            .accessDeniedHandler((swe, e) -> {
                log.info("Inside access denied handler");
                    return Mono.fromRunnable(() -> {
                        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                });
            }).and()
            .cors()
            .and()
            .csrf().disable()
            .authorizeExchange()
            .and()
            .httpBasic().disable()
            .formLogin().disable()
            .authenticationManager(JWTReactiveAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .matchers(EndpointRequest.to("health")).permitAll()
            .matchers(EndpointRequest.to("info")).permitAll()
            .matchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name())
            .pathMatchers(HttpMethod.POST, "/api/v1/users**").hasRole(Role.ADMIN.name())
            .pathMatchers(HttpMethod.DELETE, "/api/v1/users**").hasRole(Role.ADMIN.name())
            .pathMatchers("/user").permitAll()
            .pathMatchers("/group").permitAll()
            .pathMatchers("/verify/validate/**/**").permitAll()
            .pathMatchers("/").permitAll()
            .pathMatchers("/manifest.json", "/favicon.ico").permitAll()
            .pathMatchers("/static/js/**", "/static/media/**","/static/css/**", "/service-worker.js").permitAll()
            .pathMatchers("/api/auth/login**").permitAll()
            .pathMatchers("/api/auth/logout**").permitAll()
            .pathMatchers("/api/auth/register**").permitAll()
            .pathMatchers("/api/auth/verify**").permitAll()
            .pathMatchers("/api/auth/verify/validate**").permitAll()
            .pathMatchers("/api/v1/groups**").permitAll()
            .anyExchange().authenticated()
            .and();
        
        return http.build();
    }

    @Bean 
    @Profile("dev")
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Allowed origin is: " + env.getProperty("allowed_origin").toString());
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList(env.getProperty("allowed_origin").toString()));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS", "PUT"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
 
    public ServerCsrfTokenRepository csrfTokenRepository() {

        WebSessionServerCsrfTokenRepository repository =
                new WebSessionServerCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TK");

        return repository;
    }


/*
    public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrationRepository,
                        ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                        authorizedClientRepository);
        oauth.setDefaultClientRegistrationId("google");
        return WebClient.builder()
                .filter(oauth)
                .build();
    }*/

    public ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
        RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(uri));
        return successHandler;
    }
}
