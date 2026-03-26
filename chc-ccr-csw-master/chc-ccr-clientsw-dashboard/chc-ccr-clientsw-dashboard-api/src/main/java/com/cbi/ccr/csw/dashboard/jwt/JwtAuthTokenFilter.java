package com.cbi.ccr.csw.dashboard.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.frw.common.exception.AccessDeniedException;

public class JwtAuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtProvider tokenProvider;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// security filter used only for Dashboard
		try {

			String uri = request.getRequestURI();
			if (uri.contains( ControllerPath.UI) || 
					uri.contains( ControllerPath.SIGN_IN) || 
					uri.contains(ControllerPath.RESET_PASSWORD)) {
				filterChain.doFilter(request, response);
				return;
			}
			
			Collection<? extends GrantedAuthority> auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

			boolean authenticated = true;
			for (GrantedAuthority grantedAuthority : auth) {
				if(grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS")) {
					authenticated = false;
				}
			}
			if(authenticated) {
				filterChain.doFilter(request, response);
				return;
			}
			
			String jwt = getJwt(request);
			if (jwt != null && tokenProvider != null && tokenProvider.validateJwtToken(jwt)) {

				String username = tokenProvider.getUserNameFromJwtToken(jwt);
				UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} else {
				// response.sendRedirect("/ui/#/login");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().print("Unauthorized");
				return;
			}

		} catch (Exception e) {
			logger.error("Can NOT set user authentication -> Message: {}", e);
			//resolver.resolveException(request, response, null, e);
		}

		filterChain.doFilter(request, response);

	}
	
	public String getJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.replace("Bearer ", "");
		} 
		return null;
	}

	
}
