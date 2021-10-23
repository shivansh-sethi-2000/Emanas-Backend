package com.ehrc.controller.exceptionmapping;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IOExceptionMapper implements ExceptionMapper<IOException> {
	private Logger logger = LoggerFactory.getLogger(IOExceptionMapper.class);

	@Override
	public Response toResponse(IOException exception) {
		logger.error("Exception in REST API", exception);
		
		com.ehrc.utility.ResponseConfig responses = new com.ehrc.utility.ResponseConfig();
		responses.setLink("http://iiitb.ac.in/");
		responses.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());	
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responses).build();
	}

}
