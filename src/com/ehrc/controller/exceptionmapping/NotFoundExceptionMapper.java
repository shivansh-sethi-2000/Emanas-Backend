package com.ehrc.controller.exceptionmapping;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
	private Logger logger = LoggerFactory.getLogger(NotFoundExceptionMapper.class);
	
	@Override
	public Response toResponse(NotFoundException exception) {
		logger.error("Exception in REST API", exception);
		
		com.ehrc.utility.ResponseConfig responses = new com.ehrc.utility.ResponseConfig();
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("NOT FOUND");
		responses.setMessage("Resource not found");
		responses.setStatus(Response.Status.NOT_FOUND.getStatusCode());
		return Response.status(Response.Status.NOT_FOUND).entity(responses).build();
	}

}
