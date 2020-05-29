package com.toledo.minhasfinancas.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.toledo.minhasfinancas.security.HttpUnauthorizedEntryPoint;
import com.toledo.minhasfinancas.security.JwtAuthenticationFilter;
import com.toledo.minhasfinancas.security.JwtAuthorizationFilter;
import com.toledo.minhasfinancas.security.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private Environment env;
	private JwtUtil jwtUtil;
	private UserDetailsService userDetailService;

	@Autowired
	public SecurityConfig(Environment env, UserDetailsService userDetailService, JwtUtil jwtUtil) {
		this.env = env;
		this.userDetailService = userDetailService;
		this.jwtUtil = jwtUtil;
	}

	// Allow public access to these urls
	private static final String[] PUBLIC_MATCHERS = { "/auth/**" };

	// Allow public access via POST to these urls
	private static final String[] PUBLIC_MATCHERS_POST = { "/users/**"};

	/**
	 * Defines the system http security.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Allow access to h2 database (if is used)
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}

		// Enable cors and disable csrf security (calling the bean defined in
		// 'CorsConfigurationSource' method)
		http.cors().and().csrf().disable();
		
		// Return 401 status code when the Authorization token is missing
		http.exceptionHandling().authenticationEntryPoint(getAuthenticationEntryPoint());
		
		// Allow authenticated access to these urls
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll().antMatchers(PUBLIC_MATCHERS).permitAll()
				.anyRequest().authenticated();
		
		// Add filters by jwt
		http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), env, jwtUtil), UsernamePasswordAuthenticationFilter.class);
		
		// Add authorization filters
		http.addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailService, jwtUtil, env));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	/**
	 * Configure the system authentication provider.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.userDetailsService(userDetailService).passwordEncoder(getPasswordEncoder());
	}

	/**
	 * Enabling cross origin access.
	 * 
	 * @return System cross origin access configuration source.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}

	@Bean
	public HttpUnauthorizedEntryPoint getAuthenticationEntryPoint() {
		return new HttpUnauthorizedEntryPoint();
	}
	
	/**
	 * Create a bean to access the system password encoder.
	 * 
	 * @return System password encoder.
	 */
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
