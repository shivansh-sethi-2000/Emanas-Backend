package com.ehrc.controller.exceptionmapping;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehrc.controller.exception.InvalidCredentialsException;

public class InvalidCredentialsExceptionMapper implements ExceptionMapper<InvalidCredentialsException> {
	private Logger logger = LoggerFactory.getLogger(NotFoundExceptionMapper.class);
	
	@Override
	public Response toResponse(InvalidCredentialsException exception) {
		logger.error("Exception in REST API", exception);
		com.ehrc.utility.ResponseConfig responses = new com.ehrc.utility.ResponseConfig();
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("login");
		responses.setMessage("Wrong Username or Password provided. Please try again");
		responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
		return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
	}

}
