package com.keduit.bpro54.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.keduit.bpro54.security.handler.ClubLoginSuccessHandler;
import com.keduit.bpro54.security.service.ClubUserDetailsService;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private ClubUserDetailsService clubUserDetailsService;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); //비번 암호화해서 돌려줌
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		log.info("------------------ filterChain ------------------");
		http.formLogin().loginPage("/sample/login");
		
		http.authorizeHttpRequests((auth) -> {
			auth.antMatchers("/sample/all").permitAll();
			auth.antMatchers("/sample/member").hasRole("USER");
			auth.antMatchers("/sample/manager").hasRole("MANAGER");
			auth.antMatchers("/sample/admin").hasRole("ADMIN");
		});
		
		http.formLogin(); //인가, 인증에 문제가 발생할 때 로그인 화면을 띄움
		http.csrf().disable();
		http.logout();
		
//		http.oauth2Login().loginPage("/sample/login")
//			.successHandler(successHandler());
		http.oauth2Login().loginPage("/sample/login");
		http.rememberMe()
			.tokenValiditySeconds(60*60*7)
			.userDetailsService(clubUserDetailsService);
		return http.build();
	}

	@Bean
	public ClubLoginSuccessHandler successHandler() {
		return new ClubLoginSuccessHandler(passwordEncoder());
	}
	
	
}
