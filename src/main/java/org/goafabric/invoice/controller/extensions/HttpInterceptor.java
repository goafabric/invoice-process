package org.goafabric.invoice.controller.extensions;

import io.micrometer.common.KeyValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class HttpInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Configuration
    static class Configurer implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HttpInterceptor());
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        TenantContext.setContext(request);
        configureLogsAndTracing(request);

        if (handler instanceof HandlerMethod handlerMethod) {
            log.info(" {} method called for user {} ", handlerMethod.getShortLogMessage(), TenantContext.getUserName());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.removeContext();
        MDC.remove("tenantId");
    }

    private static void configureLogsAndTracing(HttpServletRequest request) {
        MDC.put("tenantId", TenantContext.getTenantId());
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(
                context -> context.addHighCardinalityKeyValue(KeyValue.of("tenant.id", TenantContext.getTenantId())));
    }

    /*
    @Bean @ConditionalOnMissingClass("org.springframework.security.oauth2.client.OAuth2AuthorizationContext")
    SecurityFilterChain filterChain(HttpSecurity http, @Value("${security.authentication.enabled:true}") boolean isAuthenticationEnabled, HandlerMappingIntrospector introspector) throws Exception {
        return isAuthenticationEnabled
                ? http.authorizeHttpRequests(auth -> auth.requestMatchers(new MvcRequestMatcher(introspector, "/actuator/**")).permitAll().anyRequest().authenticated())
                .httpBasic(httpBasic -> {}).csrf(AbstractHttpConfigurer::disable).build()
                : http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).httpBasic(httpBasic -> {}).csrf(AbstractHttpConfigurer::disable).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override public String encode(CharSequence rawPassword) { return rawPassword.toString(); }
            @Override public boolean matches(CharSequence rawPassword, String encodedPassword) { return rawPassword.toString().equals(encodedPassword);}
        };
    }

    @Bean
    ObservationPredicate disableHttpServerObservationsFromName() { return (name, context) -> !(name.startsWith("spring.security.") || (context instanceof ServerRequestObservationContext serverContext && (serverContext).getCarrier().getRequestURI().startsWith("/actuator"))); }

     */
}