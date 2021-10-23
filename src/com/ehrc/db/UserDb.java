package com.ehrc.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ehrc.user.co.UserCO;

@Entity
@Table(name = "userdb")
public class UserDb {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int userId;
	
	@Column(name = "username", length = 50)
	String username;
	
//	@ColumnTransformer(read = "pgp_sym_decrypt(password, 'abcd')", write = "pgp_sym_encrypt(?, 'abcd')")
//	@Column(columnDefinition = "bytea", name="password")
	@Column(name = "password", length = 50)
	String password;
	
//	@ColumnTransformer(read = "pgp_sym_decrypt(eManasID, 'abcd')", write = "pgp_sym_encrypt(?, 'abcd')")
//	@Column(columnDefinition = "bytea", name="emanasid")
	@Column(name = "emanasid", length = 50)
	String eManasID;
	
	@Column(name = "first_name", length = 100)
	String first_name;
	
	@Column(name = "last_name", length = 100)
	String last_name;
	
	@Column(name = "created_at")
	Date created_at;
	
	@Column(name = "updated_at")
	Date updated_at;
	
	@Column(name = "status", length = 50)
	String status;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	
	public Date getCreated_at() {
		return created_at;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static UserDb mapUserCOToDB(UserCO objUserCO) throws ParseException
	{
		UserDb objUserDb = new UserDb();
		objUserDb.setUsername(objUserCO.getUsername().toLowerCase());
		objUserDb.setPassword(objUserCO.getPassword());
		objUserDb.seteManasID(objUserCO.geteManasID());
		objUserDb.setFirst_name(objUserCO.getFirstName());
		objUserDb.setLast_name(objUserCO.getLastName());
		objUserDb.setStatus(objUserCO.getStatus());
		objUserDb.setUpdated_at(new Date());
		if(objUserCO.getCreated_at() != null) {
			objUserDb.setCreated_at(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(objUserCO.getCreated_at()));
		}
		
		if(objUserCO.getUserId() != null )
		{
			objUserDb.setUserId(Integer.parseInt(objUserCO.getUserId()));
		}
		return objUserDb;
	}
	
	public String geteManasID() {
		return eManasID;
	}

	public void seteManasID(String eManasID) {
		this.eManasID = eManasID;
	}

	public static UserCO mapUserDBToCO(UserDb objUserDb) throws ParseException
	{
		UserCO objUserCO = new UserCO();
		objUserCO.setUsername(objUserDb.getUsername());
		//objUserCO.setEmail(objUserDb.getEmail());
		//objUserCO.setRole(objUserDb.getRole());
		objUserCO.setFirstName(objUserDb.getFirst_name());
		objUserCO.setLastName(objUserDb.getLast_name());
		//objUserCO.setMobileNo(objUserDb.getMobile_no());
		objUserCO.setStatus(objUserDb.getStatus());
		//objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		//objUserCO.setUpdated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getUpdated_at()));
		
		if(objUserDb.getCreated_at() != null) {
			//objUserCO.setCreated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getCreated_at()));
		}
		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		
		return objUserCO;
	}
	
	public static UserCO mapUserLoginDBToCO(UserDb objUserDb) throws ParseException
	{
		UserCO objUserCO = new UserCO();
		objUserCO.setUsername(objUserDb.getUsername());
		objUserCO.setPassword(objUserDb.getPassword());
		objUserCO.seteManasID(objUserDb.geteManasID());
		objUserCO.setFirstName(objUserDb.getFirst_name());
		objUserCO.setLastName(objUserDb.getLast_name());
		objUserCO.setStatus(objUserDb.getStatus());
		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		objUserCO.setUpdated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getUpdated_at()));
		if(objUserDb.getCreated_at() != null) {
			objUserCO.setCreated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getCreated_at()));
		}
		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		
		return objUserCO;
	}
	
	public static UserCO mapUserUpdateDBToCO(UserDb objUserDb) throws ParseException
	{
		UserCO objUserCO = new UserCO();
		objUserCO.setUsername(objUserDb.getUsername());
		objUserCO.seteManasID(objUserDb.geteManasID());
		objUserCO.setFirstName(objUserDb.getFirst_name());
		objUserCO.setLastName(objUserDb.getLast_name());
		objUserCO.setStatus(objUserDb.getStatus());
		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		objUserCO.setUpdated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getUpdated_at()));
		if(objUserDb.getCreated_at() != null) {
			objUserCO.setCreated_at(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(objUserDb.getCreated_at()));
		}
		objUserCO.setUserId(Integer.toString(objUserDb.getUserId()));
		
		return objUserCO;
	}
	public static UserDb mapUserUpdateCOToDB(UserCO objUserCO) throws ParseException
	{
		UserDb objUserDb = new UserDb();
		objUserDb.seteManasID(objUserCO.geteManasID());
		objUserDb.setStatus(objUserCO.getStatus());
		objUserDb.setUpdated_at(new Date());
		if(objUserCO.getUserId() != null )
		{
			objUserDb.setUserId(Integer.parseInt(objUserCO.getUserId()));
		}
		return objUserDb;
	}

}
