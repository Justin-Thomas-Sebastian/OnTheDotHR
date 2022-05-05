package com.codeup.onthedothr;

import com.codeup.onthedothr.services.UserDetailsLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsLoader usersLoader;

    public SecurityConfiguration(UserDetailsLoader usersLoader) {
        this.usersLoader = usersLoader;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usersLoader) // How to find users by their username
                .passwordEncoder(passwordEncoder()) // How to encode and verify passwords
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()// enables GET request/redirect to logout
                /* Login configuration */
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard") // user's home page, it can be any URL
                .permitAll() // Anyone can go to the login page
                /* Logout configuration */
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout") // append a query string value
                .invalidateHttpSession(true)  // not sure if needed, added for some troubleshooting
                /* Pages that can be viewed without having to log in */
                .and()
                .authorizeRequests()
                .antMatchers("/") // anyone can see the home
                .permitAll()
                /* Pages that require authentication */
                .and()
                .authorizeRequests()
                .antMatchers(
                        // only authenticated users
                        "/dashboard",
                        "/supervisor-dashboard",
                        "/profile",
                        "/profile/{id}",
                        "/details",
                        "/decision-deliverable/{id}",
                        "/details/{id}",
                        "/deliverables/{id}",
                        "/deliverables/show",
                        "/deliverables/create",
                        "/deliverables/{id}/create",
                        "/deliverables/{id}/edit",
                        "/request-appointment",
                        "/deliverables/edit",
                        "/request-appointment",
                        "/requests",
                        "/appointments/{id}/update-status",
                        "/appointments/{id}/create",
                        "/appointments/{id}/manage",
                        "/cancel-appointment/{id}",
                        "/user/{id}/delete",
                        "/users/delete"
                )
                .authenticated()
        ;
    }
}