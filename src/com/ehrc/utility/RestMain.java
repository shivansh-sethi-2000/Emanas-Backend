package com.ehrc.utility;

import javax.servlet.ServletContext;

import org.glassfish.jersey.server.ResourceConfig;

public class RestMain extends ResourceConfig{
	
	@SuppressWarnings("unused")
	public RestMain(@javax.ws.rs.core.Context ServletContext context) throws Exception {
		
		LoadConfig loadconfig = new LoadConfig();
		packages("com.ehrc.user.rest");
//		packages("com.ehrc.common.rest");
//		packages("com.ehrc.controller.rest");

	}

}
