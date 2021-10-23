package com.ehrc.authentication;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehrc.controller.exception.NotAuthorizedException;
import com.ehrc.utility.JWTUtil;

@Secured
public class JaxRsFilterAuthentication implements ContainerRequestFilter {
	Logger logger = LoggerFactory.getLogger(JaxRsFilterAuthentication.class);

	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void filter(ContainerRequestContext containerRequest)
			throws WebApplicationException, UnsupportedEncodingException {
		String authorizationHeader = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
		String token = null;
		if(authorizationHeader == null) {
			throw new NotAuthorizedException();
		}
		
		if(authorizationHeader.length() > "Bearer".length()){
			token = authorizationHeader.substring("Bearer".length());
			
		}
		// Extract the token from the HTTP Authorization header
		
		if(token != null) {
			token = token.trim();
		}
	
		try {
			// Validate the token
			if (new JWTUtil().verify(token)) {
				logger.debug("#### valid token");
			} else {
				logger.error("#### Invalid token");
				throw new NotAuthorizedException();
			}

		} catch (Exception e) {
			logger.error("#### invalid token" , e);
			throw new NotAuthorizedException();
		}
	}
}