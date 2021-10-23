package com.ehrc.controller.exceptionmapping;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
	
@Provider
class JSONParseExceptionMapper implements ExceptionMapper< JsonParseException > {    
    private Logger logger = LoggerFactory.getLogger(IOExceptionMapper.class);

    @Override
	public Response toResponse(final JsonParseException jpe) {
		logger.error("Exception in REST API", jpe);
		
		com.ehrc.utility.ResponseConfig responses = new com.ehrc.utility.ResponseConfig();
		responses.setLink("http://iiitb.ac.in/");
		responses.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());	
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responses).build();
	}
}
