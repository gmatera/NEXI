package com.cbi.ccr.csw.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.JwtAuthEntryPoint;
import com.cbi.ccr.csw.dashboard.jwt.JwtAuthTokenFilter;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.frw.api.dto.InternalControllerPath;

//@Profile("!POLLER")
//@EnableWebSecurity
//@Configuration
//@EnableGlobalMethodSecurity(
//		securedEnabled = true,
//		jsr250Enabled = true,
//		prePostEnabled = true)
public class WebSecurityCSWApi extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtAuthEntryPoint unauthorizedHandler;

	private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/#/ui/login",
            "/api/lmi/signin",
            "/api/lmi/user/resetpassword",
            "/api/lmi/user/secret",
            "/api/lmi/login",
            "/assets/fonts/material-icons/flUhRq6tzZclQEJ-Vdg-IuiaDsNc.woff2",
            CSWOutboundControllerPath.BASE + "/**"
            // other public endpoints of your API may be appended to this array
    };
	 
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }
	
    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }
    

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.authorizeRequests()
//		.antMatchers(HttpMethod.POST, "/api/lmi/signin").permitAll()
		
		// Dashboard LMI
		.antMatchers(ControllerPath.LMI + ControllerPath.SIGN_IN).permitAll() // public
		.antMatchers(ControllerPath.LMI + ControllerPath.USER_LIST + ControllerPath.RESET_PASSWORD).permitAll() 
		.antMatchers(ControllerPath.LMI + ControllerPath.USER_LIST + ControllerPath.SECRET_DETAILS).permitAll() // public

		//.antMatchers(ControllerPath.LMI + "/**").permitAll() // to be protected
		.antMatchers(ControllerPath.UI + "/**").permitAll() // permitAll
		
//		// outbound REST
//		.antMatchers(CSWOutboundControllerPath.BASE + CSWOutboundControllerPath.REST_OUTBOUND+ "/**").permitAll() // to be protected
				
		// outbound
		.antMatchers(CSWOutboundControllerPath.BASE + "/**").permitAll() // to be protected
		// inbound
		.antMatchers(CSWInboundControllerPath.BASE + "/**").permitAll() // to be protected
		
       .anyRequest().authenticated()
		//.anyRequest().permitAll()

        .and()
        	.cors().and()
        	.csrf().disable()
        	.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        // 401-UNAUTHORIZED when anonymous user tries to access protected URLs
            .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler);
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
}
