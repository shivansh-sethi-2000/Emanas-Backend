package com.ehrc.user.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class UserCO {
	
	String userId;
	String username;
	String password;
	String eManasID;
	String firstName;
	String lastName;
	String updated_at;
	String created_at;
	String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String geteManasID() {
		return eManasID;
	}
	public void seteManasID(String eManasID) {
		this.eManasID = eManasID;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	@Override
	public String toString() {
		return "UserCO [userId=" + userId + ", username=" + username + ", password=" + password + ", eManasID="
				+ eManasID + ", firstName=" + firstName + ", lastName=" + lastName + ", updated_at=" + updated_at
				+ ", created_at=" + created_at + ", status=" + status + ", consentID=" + "]";
	}
	
}
