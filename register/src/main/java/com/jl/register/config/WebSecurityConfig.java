package com.jl.register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置拦截设置

        http
                .cors().and()
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/login/**").permitAll()

                .anyRequest().authenticated()
                // .anyRequest().permitAll()
                .and().formLogin();
    }
}
