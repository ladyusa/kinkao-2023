package ku.kinkao.config;

import ku.kinkao.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private OidcUserService oidcUserService;

    @Autowired
    private ApplicationContext context;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()

                        // unauthenticated users can read restaurants and reviews.
                        .requestMatchers(
                                new AntPathRequestMatcher("/restaurants")).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/reviews/show/**")).permitAll()

                        // members and admins can also add reviews
                        .requestMatchers(
                                new AntPathRequestMatcher("/reviews/add/**"))
                        .hasAnyRole("USER", "ADMIN")

                        // admins can add restaurants
                        .requestMatchers(
                                new AntPathRequestMatcher("/restaurants/add")).hasRole("ADMIN")

                        // other url must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        http.headers(headers -> headers
                .xssProtection(Customizer.withDefaults())
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("form-action 'self'; script-src 'self'"))
        );

        ClientRegistrationRepository repository =
                context.getBean(ClientRegistrationRepository.class);

        if (repository != null) {
            http
                    .oauth2Login((oauth2Login) -> oauth2Login
                            .clientRegistrationRepository(repository)
                            .userInfoEndpoint(userInfo -> userInfo
                                    .oidcUserService(oidcUserService)
                            )
                            .loginPage("/login").permitAll()
                    );
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }
}
