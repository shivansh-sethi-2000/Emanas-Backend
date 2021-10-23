package com.ehrc.utility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ResponseConfig {
	int code, status;
	String link = "", location="", message = "";
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	

	@Override
	public String toString() {
		return "ResponseConfig [code=" + code + ", status=" + status + ", link=" + link + ", location=" + location
				+ ", message=" + message + "]";
	}

	public String createResponse(int objCode, int objStatus, String objLocation, String objMessage)
	{
		
		
		
		String response="{\n" + 
				"		    \"code\": "+code+",\n" + 
				"		    \"link\": \"http://www.mhms.iiitb.ac.in/\",\n" + 
				"		    \"location\": \""+location+"\",\n" + 
				"		    \"message\": \""+message+"\",\n" + 
				"		    \"status\": "+status+"\n" + 
				"		}";
		
		return response;
	}

	public void setCode(int statusCode) {
		// TODO Auto-generated method stub
		
	}

}
