package com.saracveysel.security.config;

import com.saracveysel.security.users.exception.ExceptionMessages;
import com.saracveysel.security.users.exception.UserServiceException;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserDetailsService userDetailsService) throws Exception {

        return http

                // CSRF: Daha güvenli bir seçenek için Cookie bazlı CSRF koruması ekleniyor
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // Yetkilendirme Kuralları
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/login", "/login.html").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/books/**").hasRole("USER")
                        .anyRequest().authenticated()
                )

                // Kullanıcı Detay Servisi
                .userDetailsService(userDetailsService)

                // Basic Authentication (REST API için)
                .httpBasic(Customizer.withDefaults())

                // Form Login Ayarları (Eğer REST API kullanıyorsan kapalı kalabilir)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )

                // Session Yönetimi
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // Yeni oturum oluşturur, eski oturumu geçersiz kılar
                        .invalidSessionUrl("/login?expired") // Oturum süresi dolduğunda yönlendirme
                        .maximumSessions(1) // Aynı anda sadece 1 oturum açık olabilir
                        .maxSessionsPreventsLogin(false) // Yeni giriş yapıldığında eski girişleri geçersiz yapar
                )

                // Logout İşlemi
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) // Kullanıcı çıkınca oturum sil
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID") // Çerez temizleme
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            User user = userService.findByUserName(username);
            if (user == null) {
                throw new UserServiceException(ExceptionMessages.USER_NOT_FOUND.getMessage());
            }
            log.info("Loaded user: {}", user.getUserName());
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUserName())
                    .password(user.getPassword())
                    .authorities(user.getRole())
                    .build();
        };
    }
}
