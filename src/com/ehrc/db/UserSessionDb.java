package com.ehrc.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ehrc.utility.LoadConfig;

@Entity
@Table(name = "usersessiondb")
public class UserSessionDb {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	Integer id;
	
	@Column(name = "sessionToken", length = 50)
	String sessionToken;
	
	@Column(name = "userId", length = 50)
	String userId;
	
	@Column(name = "lastLogin")
	Date lastLogin;
	
	@Column(name = "expiryAt")
	Date expiryAt;
	
	@Column(name = "logoutAt")
	Date logoutAt;
	
	@Column(name = "status", length = 100)
	String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getExpiryAt() {
		return expiryAt;
	}

	public void setExpiryAt(Date expiryAt) {
		this.expiryAt = expiryAt;
	}

	public Date getLogoutAt() {
		return logoutAt;
	}

	public void setLogoutAt(Date logoutAt) {
		this.logoutAt = logoutAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserSessionDb [id=" + id + ", sessionToken=" + sessionToken + ", userId=" + userId + ", lastLogin="
				+ lastLogin + ", expiryAt=" + expiryAt + ", logoutAt=" + logoutAt + ", status=" + status + "]";
	}

	public static UserSessionDb mapUserSesCOToDB(String token, String userId) {
		
		UserSessionDb objUserSesDb = new UserSessionDb();
		objUserSesDb.setSessionToken(token);
		objUserSesDb.setUserId(userId);
		objUserSesDb.setLastLogin(new Date());
		objUserSesDb.setExpiryAt(new Date(System.currentTimeMillis() + Long.parseLong(LoadConfig.getConfigValue("JWT_EXPIRY_TIMEOUT"))));
		objUserSesDb.setStatus(LoadConfig.getConfigValue("ACTIVE_SESSION_STATUS"));
		objUserSesDb.setLogoutAt(null);

		return objUserSesDb;
	}
	
	
}