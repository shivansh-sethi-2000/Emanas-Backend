package com.ehrc.user.dao;

import java.text.ParseException;
import java.util.List;

import org.json.JSONObject;

import com.ehrc.db.ConsentDb;
import com.ehrc.db.HospitalDb;
import com.ehrc.db.UserDb;
import com.ehrc.user.co.ConsentCO;
import com.ehrc.user.co.UserCO;

public interface UserDAO {
	
	public UserCO saveUser(UserCO user) throws ParseException;
	public UserDb getUser(String username);
	public UserDb getUserById(int userId);
	public List<UserDb> getAllUserPass();
	public boolean updatePassword(UserCO objUserCO);
	public boolean updatePass(int userId, String password);
	public boolean updateUser(UserDb objUserDb);
	public String getStatus(int userId);
	public boolean authenticateServer();
	public boolean authenticateUser(String eManasID);
	public ConsentCO addConsent(ConsentCO objConsentCO);
	public boolean consentRequest(ConsentCO objCo);
	public boolean verifyConsentRequest(String otp, ConsentCO objConsentCO);
	public JSONObject getHealthRecords(ConsentCO objRecords, String username);
	public String getHospitalId(String hospitalname);
	public boolean CheckConsent(ConsentCO objCo, int userid);
	public List<ConsentDb> getConsentsbyhospital(String hospitalname, int userid);
	public List<HospitalDb> getHospitalList();
	boolean VerifyRegistrationOTP(String otp);
	public List<ConsentDb> getAllConsents(int userid);
	public ConsentDb getConsentbyID(String consentid);
	JSONObject getPatientDetails(String eManasId);	
}
