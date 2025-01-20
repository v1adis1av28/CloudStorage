package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Component
public class SecurityConfig {


    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users.username("user").password("password").roles("USER").build());
        manager.createUser(users.username("admin").password("admin").roles("ADMIN", "USER").build());
        return manager;
    }

    //TODO не работает из-за отсутствия аутетентификации здесь смотреть основу -> https://www.youtube.com/watch?v=h-9vhFeM3MY&list=PLhmKCS4rYkgPGi7n6O7AIKwOFHY9yrpIQ&index=55
    //TODO Разделить конфиг где хранится authentication provider, bcrypt etc. и на SecurityChainFilter фильтровать там входящие запросы

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/hello").permitAll()
                        .requestMatchers("/auth/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll())
                .formLogin(form -> form.loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/hello",true)
                        .failureUrl("/auth/login?error")
                        .permitAll());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
