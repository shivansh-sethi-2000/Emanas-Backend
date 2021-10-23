package com.ehrc.user.dao;



import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehrc.db.ConsentDb;
import com.ehrc.db.HospitalDb;
import com.ehrc.db.UserDb;
import com.ehrc.user.co.ConsentCO;
import com.ehrc.user.co.UserCO;
import com.ehrc.utility.HibernateUtil;
import com.ehrc.utility.LoadConfig;

public class UserDAOImpl implements UserDAO{

	Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
	
	@Override
	public UserCO saveUser(UserCO objUserCO){
		Session ses = null;
		Transaction tx = null;
		UserCO finalResponse = new UserCO();
		// get Session
		ses = HibernateUtil.getSession();
		
		if(objUserCO == null) {
			logger.info(" People Object is null");
			
		} else {
			
			try	{
				UserDb objUserDb = UserDb.mapUserCOToDB(objUserCO);
				objUserDb.setCreated_at(new Date());
				logger.info("Created Date ="+objUserDb.getCreated_at());
				logger.info("User record to be created -> "+objUserDb.getUsername());
				
				tx = ses.beginTransaction();
				ses.saveOrUpdate(objUserDb);
				logger.info("User record created");
				tx.commit();
				
				
				finalResponse.setUsername(objUserDb.getUsername());
				finalResponse.setPassword(objUserDb.getPassword());
				finalResponse.setFirstName(objUserDb.getFirst_name());
				finalResponse.setLastName(objUserDb.getLast_name());
				finalResponse.seteManasID(objUserDb.geteManasID());
				
				
			} catch (Exception e) {
				logger.error("User record creation problem", e);
				tx.rollback();
				finalResponse = null;

			} finally {
				HibernateUtil.closeSession();
			}
		}
		return finalResponse;
	}

	@Override
	public UserDb getUser(String username) {
		Session ses = null;
		UserDb userDomain = null;
		// get Session

		logger.info("Fetching data for username =>>>>>> " + username);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<UserDb> query = ses.createQuery("Select u from UserDb u where LOWER(u.username) = :username", UserDb.class);
			query.setParameter("username", username.toLowerCase());
			userDomain = query.getSingleResult();
		} catch (Exception e) {
			logger.error("ID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userDomain;
	}
	
	@Override
	public UserDb getUserById(int userId) {
		Session ses = null;
		UserDb userDomain = null;
		// get Session

		logger.info("Fetching data for userID =>>>>>> " + userId);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<UserDb> query = ses.createQuery("Select u from UserDb u where u.userId = :userId", UserDb.class);
			query.setParameter("userId", userId);
			userDomain = query.getSingleResult();
		} catch (Exception e) {
			logger.error("ID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userDomain;
	}

	
	@Override
	public List<UserDb> getAllUserPass() {
		Session ses = null;
		List<UserDb> userDomain = null;
		// get Session

		logger.info("Fetching password data for all user =>>>>>> ");
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<UserDb> query = ses.createQuery("Select u from UserDb u", UserDb.class);
			logger.info("Fetching user data for orgId query =>>>>>> " + query);
			userDomain = query.getResultList();
		} catch (Exception e) {
			logger.error("ID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userDomain;
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean updatePassword(UserCO objUserCO) {
		Session ses = null;
		Transaction tx = null;
		int result = 0;
		boolean response = false;
		// get Session

		logger.info("**Updating password for all users with Salt**");
		try {
			
			ses = HibernateUtil.getSession();
			tx = ses.beginTransaction();
				UserDb objUserDb = new UserDb();
				objUserDb = objUserDb.mapUserCOToDB(objUserCO);
				
				Query query = ses.createQuery("update UserDb u set u.password = pgp_sym_encrypt(:password, 'abcd'), u.updated_at = :updated_at where u.userId = :userId");
				query.setParameter("password", objUserDb.getPassword());
				query.setParameter("updated_at", objUserDb.getUpdated_at());
				query.setParameter("userId", objUserDb.getUserId());
				
				System.out.println("UserId = >>"+objUserDb.getUserId());
				
				
				result = query.executeUpdate();
				
				if(result != 0 ) {
					tx.commit();
					query = null;
					response = true;
					
				} else {
					tx.rollback();
					response = false;
				}
		} catch (Exception e) {
			logger.error("ID not found ::", e);
			tx.rollback();
			response = false;
		}finally {
			HibernateUtil.closeSession();
		}
		
		return response;
	}

	
	@Override
	public String getStatus(int userId) {
		Session ses = null;
		UserDb objUser = null;
		String status = null;
		
		logger.info("Fetching user data for userId =>>>>>> " + userId);
		try {
			ses = HibernateUtil.getSession();
			TypedQuery<UserDb> query = ses.createQuery("Select u from UserDb u where u.userId = :userId", UserDb.class);
			query.setParameter("userId", userId);			
			logger.info("Fetching user data for orgId query =>>>>>> " + query);
			objUser = query.getSingleResult();
			
			if(objUser != null) {
				status = objUser.getStatus();
			}
			
		} catch(Exception e) {
			logger.error("ID not found :: ",e);
		} finally {
			HibernateUtil.closeSession();
		}
		return status;
	}

	@Override
	public boolean updateUser(UserDb objUserDb) {
		Session ses = null;
		Transaction tx = null;
		boolean response = false;
		// get Session

		logger.info("**Updating user for all users**");
		try {
			ses = HibernateUtil.getSession();
			tx = ses.beginTransaction();
			int result = 0;
			
			if(objUserDb != null) {
				
				Query query = ses.createQuery("update UserDb u set u.eManasID = eManasID  where u.userId = :userId");
				query.setParameter("eManasID", objUserDb.geteManasID());
				query.setParameter("status", objUserDb.getStatus());
				query.setParameter("updated_at", new Date());
				query.setParameter("userId", objUserDb.getUserId());
				
				System.out.println("UserId for password update = >>"+objUserDb.getUserId());
				
				result = query.executeUpdate();
				
				if(result != 0 ) {
					
					tx.commit();
					logger.info("User updated.");
					query = null;
					response = true;
					
				} else {
					tx.rollback();
					response = false;
				}
			}
		} catch (Exception e) {
			logger.error("ID not found ::", e);
			tx.rollback();
			response = false;
		}finally {
			HibernateUtil.closeSession();
		}
		return response;
	}

	@Override
	public boolean updatePass(int userId, String password) {
		Session ses = null;
		Transaction tx = null;
		int result = 0;
		boolean response = false;
		// get Session

		logger.info("**Updating password for user**");
		logger.info("User ID = "+userId);
		try {
			
			ses = HibernateUtil.getSession();
			tx = ses.beginTransaction();
								
				Query query = ses.createQuery("update UserDb u set u.password = pgp_sym_encrypt(:password, 'abcd'), u.updated_at = :updated_at where u.userId = :userId");
				query.setParameter("password", password);
				query.setParameter("updated_at", new Date());
				query.setParameter("userId", userId);
				
				System.out.println("UserId for password update = >>"+userId);
				
				result = query.executeUpdate();
				
				if(result != 0 ) {
					tx.commit();
					query = null;
					response = true;
					
				} else {
					tx.rollback();
					response = false;
				}
		} catch (Exception e) {
			logger.error("ID not found ::", e);
			tx.rollback();
			response = false;
		}finally {
			HibernateUtil.closeSession();
		}
		
		return response;
	}
	

	@Override
	public ConsentCO addConsent(ConsentCO objConsentCO) {
		Session ses = null;
		Transaction tx = null;
		ConsentCO finalResponse = new ConsentCO();
		// get Session
		ses = HibernateUtil.getSession();
		
		if(objConsentCO == null) {
			logger.info(" Consent Object is null");
			
		} else {
			
			try	{
				ConsentDb objConsentDb = ConsentDb.mapConsentCOToDB(objConsentCO);
				Date someDate = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				String timestamp = df.format(someDate);
				objConsentDb.setTimestamp(timestamp);
				tx = ses.beginTransaction();
				ses.saveOrUpdate(objConsentDb);
				logger.info("User record created");
				tx.commit();
				
				
				finalResponse.setUserID(objConsentDb.getUserID());
				finalResponse.setConsentid(objConsentDb.getConsentid());
				finalResponse.setConsentRequestid(objConsentDb.getConsentRequestid());
				finalResponse.setEnd(objConsentDb.getEnd());
				finalResponse.setHospitalname(objConsentDb.getHospitalname());
				finalResponse.setStart(objConsentDb.getStart());
				finalResponse.setTableid(objConsentDb.getTableid());
				finalResponse.setTimestamp(objConsentDb.getTimestamp());
				finalResponse.setType(objConsentDb.getType());
				finalResponse.setUserID(objConsentDb.getUserID());
				
				
			} catch (Exception e) {
				logger.error("User record creation problem", e);
				tx.rollback();
				finalResponse = null;

			} finally {
				HibernateUtil.closeSession();
			}
		}
		return finalResponse;
	}

	@Override
	public boolean authenticateServer(){
		try {
			URL url = new URL("http://65.1.244.69:8080/MHMS_FHIR/fhir/hiu/authenticate");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
	        
	        JSONObject input = new JSONObject();
	        input.put("hiu_username", LoadConfig.getConfigValue("HIU_USERNAME"));
	        input.put("hiu_password", LoadConfig.getConfigValue("HIU_PASSWORD"));

	        OutputStream os = conn.getOutputStream();
	        os.write(input.toString().getBytes());
	        os.flush();
	        
	        if (conn.getResponseCode() != 200) {
	            logger.info("Connection Failed : HTTP error code : "+ conn.getResponseCode());
	            return false;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        
	        JSONObject jObject = new JSONObject(output);
	        String token = jObject.getString("response");
    		if(token == null) {
	        	logger.info(jObject.getString("errors"));
	        	return false;
	        }
    		LoadConfig.editPropertyFile("API_TOKEN", token);
    		
	        logger.info("token received from emanas -> " + token);
	        conn.disconnect();
	        sc.close();
	        return true;
	        
		}catch (Exception e) {
			logger.info("token could not be generated from eManas Server" + e);
		}
		return false;
	}
	
	@Override
	public boolean verifyConsentRequest(String otp, ConsentCO objConsentCO) {
		try {
			UserDb obDb = getUserById(objConsentCO.getUserID());
			
			String EmanasIp = "65.1.244.69:8080";
			URL url = new URL("http://65.1.244.69/MHMS_DEV/rest/consent/verifyandCreateConsent/");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Authentication",LoadConfig.getConfigValue("API_TOKEN"));
	        conn.setRequestProperty("X-Forwarded-Host ", LoadConfig.getConfigValue(EmanasIp));
	        
	        JSONObject input = new JSONObject();

	        input.put("otp", otp);
	        input.put("consentRequestID", LoadConfig.getConfigValue("CONSENT_TOKEN"));
	        input.put("orgmapping", false);
	        input.put("patientId", obDb.geteManasID());
	        input.put("mhpRegId", getHospitalId(objConsentCO.getHospitalname()));
	        
	        OutputStream os = conn.getOutputStream();
	        os.write(input.toString().getBytes());
	        os.flush();

	        if (conn.getResponseCode() != 200) {
	            logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
	            return false;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        logger.info(output);
	        
	        JSONObject jObject = new JSONObject(output);
	        
	        JSONObject jObject2 = (JSONObject) jObject.get("output");
	        if(jObject2 == null) {
	        	JSONArray jsonArray_1 = (JSONArray) jObject.get("errors");
	        	String msgString = jsonArray_1.getString(1);
	        	logger.info(msgString);
	        	return false;
	        }
	        String consentID = jObject2.getString("consentID");
	        conn.disconnect();
	        objConsentCO.setConsentid(consentID);
			objConsentCO.setConsentRequestid(LoadConfig.getConfigValue("CONSENT_TOKEN"));
			addConsent(objConsentCO);
			sc.close();
	        return true;
	        
		}catch (Exception e) {
			logger.info("Consent ID could not be generated from eManas Server" + e);
		}
		return false;
	}
	
	@Override
	public boolean consentRequest(ConsentCO objCo) {
		try {

			String EmanasIp = "65.1.244.69:8080";
			
			UserDb objUserDb = getUserById(objCo.getUserID());
			URL url = new URL("http://65.1.244.69/MHMS_DEV/rest/consent/consentRequest/"+objUserDb.geteManasID());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Authentication",LoadConfig.getConfigValue("API_TOKEN"));
	        conn.setRequestProperty("X-Forwarded-Host ", LoadConfig.getConfigValue(EmanasIp));
	        
	        Date someDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String timestamp = df.format(someDate);
	        
	        JSONObject input = new JSONObject();
	        JSONObject consent = new JSONObject();
	        JSONObject purpose = new JSONObject();
	        JSONObject patient = new JSONObject();
	        JSONObject hip = new JSONObject();
	        JSONObject hiu = new JSONObject();
	        JSONObject requester = new JSONObject();
	        JSONObject identifier = new JSONObject();
	        JSONObject permission = new JSONObject();
	        JSONObject dateRange = new JSONObject();
	        
	        List<String> typeStrings = new ArrayList<>();
	        typeStrings.add(objCo.getType());
	        
	        purpose.put("text", "string");
	        purpose.put("code", "CAREMGT");
	        purpose.put("refUri", "string");
	        patient.put("id", objUserDb.geteManasID());
	        hip.put("id", getHospitalId(objCo.getHospitalname()));
	        hiu.put("id", LoadConfig.getConfigValue("PORTAL_ID"));
	        identifier.put("type", "REGNO");
	        identifier.put("value", LoadConfig.getConfigValue("REQUESTOR_ID"));
	        identifier.put("system", "https://e-manas.karnataka.gov.in/");
	        requester.put("name", LoadConfig.getConfigValue("REQUESTOR_NAME"));
	        requester.put("identifier", identifier);
	        consent.put("purpose", purpose);
	        consent.put("patient", patient);
	        consent.put("hip", hip);
	        consent.put("hiu", hiu);
	        consent.put("requester", requester);
	        dateRange.put("from", objCo.getStart());
	        dateRange.put("to", objCo.getEnd());
	        permission.put("accessMode", "VIEW");
	        permission.put("dateRange", dateRange);
	        input.put("timestamp", timestamp);
	        input.put("Authorization", "SMSOTP");
	        input.put("consent", consent);
	        input.put("hiTypes", typeStrings);
	        input.put("permission", permission);

	        OutputStream os = conn.getOutputStream();
	        os.write(input.toString().getBytes());
	        os.flush();
	        
	        if(conn.getResponseCode() != 200) {
	        	logger.info("Connection Failed : HTTP error code : "+ conn.getResponseCode());
	            return false;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        
	        JSONObject jObject = new JSONObject(output);
	        JSONObject jObject2 = (JSONObject) jObject.get("output");
	        
	        
	        if(jObject2.getString("ConsentRequestID") == null) {
	        	JSONObject error = (JSONObject) jObject.get("errors");
	        	logger.info(error.getString("errorMessage"));
	        	return false;
	        }
	        logger.info("otp send -> " + jObject2.getString("OTP"));
	        String consentRequestToken = jObject2.getString("ConsentRequestID");
	        LoadConfig.editPropertyFile("CONSENT_TOKEN", consentRequestToken);
	        conn.disconnect();
	        sc.close();
	        return true;
		}catch (Exception e) {
			logger.info("consent Request could not be generated from eManas Server" + e);
		}
		return false;
	}
	
	@Override
	public JSONObject getHealthRecords(ConsentCO objCo, String username) {
		Session ses = null;
		try {
			UserDb obDb = getUser(username);
			ses = HibernateUtil.getSession();
			TypedQuery<ConsentDb> query = ses.createQuery("Select u from ConsentDb u where u.userid = :userid and u.hospitalname= :hospitalname and u.start= :start and u.end= :end and u.type= :type", ConsentDb.class);
			query.setParameter("userid", obDb.getUserId());
			query.setParameter("hospitalname", objCo.getHospitalname());
			query.setParameter("start", objCo.getStart());
			query.setParameter("end", objCo.getEnd());
			query.setParameter("type", objCo.getType());
			
			ConsentDb consentDomain = query.getSingleResult();
			
			String EmanasIp = "65.1.244.69:8080";
//			URL url = new URL("http://65.1.244.69/MHMS_DEV/rest/fhir/getCompositionForHIU/");
			URL url = new URL("http://65.1.244.69/MHMS_FHIR/Fhir/Hiu/viewTreatments");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Authentication",LoadConfig.getConfigValue("API_TOKEN"));
	        conn.setRequestProperty("X-Forwarded-Host ", LoadConfig.getConfigValue(EmanasIp));

	        Date someDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String timestamp = df.format(someDate);
			
	        JSONObject input = new JSONObject();
	        JSONObject consent = new JSONObject();
	        JSONObject purpose = new JSONObject();
	        JSONObject requester = new JSONObject();
	        JSONObject identifier = new JSONObject();
	        JSONObject permission = new JSONObject();
	        JSONObject dateRange = new JSONObject();
	        JSONObject hirequest = new JSONObject();
	        
	        List<String> typeStrings = new ArrayList<>();
	        typeStrings.add(objCo.getType());
	        purpose.put("code", "CAREMGT");
	        purpose.put("text", "string");
	        purpose.put("refUri", "string");
	        
//	        identifier.put("type", "REGNO");
//	        identifier.put("value", LoadConfig.getConfigValue("REQUESTOR_ID"));
//	        identifier.put("system", "https://e-manas.karnataka.gov.in/");
	        
//	        requester.put("name", LoadConfig.getConfigValue("REQUESTOR_NAME"));
//	        requester.put("identifier", identifier);
	        
	        permission.put("accessMode", "VIEW");
	        
	        consent.put("purpose", purpose);
	        consent.put("id", consentDomain.getConsentid());
//	        consent.put("requester", requester);
	        consent.put("permission", permission);
	        consent.put("hiTypes", typeStrings);
	        
	        dateRange.put("from", consentDomain.getStart());
	        dateRange.put("to", consentDomain.getEnd());
	        
	        hirequest.put("consent", consent);
	        hirequest.put("dateRange", dateRange);
	        
//	        input.put("dateRange", dateRange);
	        input.put("timestamp", timestamp);
	        input.put("patientID", obDb.geteManasID());
	        input.put("hiRequest", hirequest);
	        
	        logger.info(input.toString(4));
	        
	        OutputStream os = conn.getOutputStream();
	        os.write(input.toString().getBytes());
	        os.flush();

	        if (conn.getResponseCode() != 200) {
	            logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
	            return null;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        logger.info(output);
	        
	        JSONObject jObject = new JSONObject(output);
	        sc.close();
	        return jObject;
	        
		}catch (Exception e) {
			logger.info("could not get records from emanas" + e);
		}
		return null;
		
	}

	@Override
	public String getHospitalId(String hospitalname) {
		Session ses = null;
		HospitalDb hospitalDomain = null;
		// get Session

		logger.info("Fetching id for hospital =>>>>>> " + hospitalname);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<HospitalDb> query = ses.createQuery("Select u from HospitalDb u where u.hospitalname = :hospitalname", HospitalDb.class);
			query.setParameter("hospitalname", hospitalname);
			hospitalDomain = query.getSingleResult();
		} catch (Exception e) {
			logger.error("hospital not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return hospitalDomain.getHospitalid();
	}
	
	@Override
	public List<HospitalDb> getHospitalList() {
		Session ses = null;
		List<HospitalDb> hospitalDomain = null;
		// get Session

		logger.info("Fetching all hospitals =>>>>>> ");
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<HospitalDb> query = ses.createQuery("Select u from HospitalDb u", HospitalDb.class);
			hospitalDomain = query.getResultList();
		} catch (Exception e) {
			logger.error("hospital not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		
		return hospitalDomain;
	}

	@Override
	public boolean CheckConsent(ConsentCO objCo, int userid) {
		try {
			List<ConsentDb> objConsentDb = getConsentsbyhospital(objCo.getHospitalname(),userid);
			if(objConsentDb == null) {
				logger.info("sql error from table");
				return false;
			}
			
			for(int i=0 ; i<objConsentDb.size() ; i++) {
				if(objCo.getEnd().compareTo(objConsentDb.get(i).getEnd()) <= 0) {
					if(objConsentDb.get(i).getType().equals(objCo.getType())) {
						logger.info("valid consent found");
						return true;
					}
				}
			}
			logger.info("consent not found");
			return false;
		}
		catch (Exception e) {
			
		}
		
		return false;
	}
	
	@Override
	public boolean authenticateUser(String eManasID) {		
		try {
			URL url = new URL("http://65.1.244.69/MHMS_DEV/rest/consent/sendOTPForPatient/" + eManasID);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
	        
	        if (conn.getResponseCode() != 200) {
	            logger.info("Connection Failed : HTTP error code : "+ conn.getResponseCode());
	            return false;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        
	        JSONObject jObject = new JSONObject(output);
	        LoadConfig.editPropertyFile("registration_tokenID".toUpperCase(), jObject.getString("tokenID"));
	        LoadConfig.editPropertyFile("registration_refID".toUpperCase(), jObject.getString("refID"));
	        LoadConfig.editPropertyFile("registration_contactType".toUpperCase(), jObject.getString("contactType"));
	        LoadConfig.editPropertyFile("registration_contactVal".toUpperCase(), jObject.getString("contactVal"));
	        LoadConfig.editPropertyFile("registration_createdAt".toUpperCase(), String.valueOf(jObject.getInt("createdAt")));
	        LoadConfig.editPropertyFile("registration_purpose".toUpperCase(), jObject.getString("purpose"));
	        LoadConfig.editPropertyFile("registration_status".toUpperCase(), jObject.getString("status"));
	        
	        String otp = jObject.getString("otp");
	        logger.info("otp send to user at " + jObject.getString("contactVal") + " otp -> " + otp);
	        conn.disconnect();
	        sc.close();
	        return true;
	        
		}catch (Exception e) {
			logger.info("otp could not be send from the Server" + e);
		}
		return false;
	}
	
	@Override
	public boolean VerifyRegistrationOTP(String otp) {
		try {
			URL url = new URL("http://65.1.244.69/MHMS_DEV/user/verifyOTP");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
	        
	        JSONObject input = new JSONObject();
	        
	        input.put("tokenID", LoadConfig.getConfigValue("registration_tokenID".toUpperCase()));
	        input.put("refID", LoadConfig.getConfigValue("registration_refID".toUpperCase()));
	        input.put("contactType", LoadConfig.getConfigValue("registration_contactType".toUpperCase()));
	        input.put("contactVal", LoadConfig.getConfigValue("registration_contactVal".toUpperCase()));
	        input.put("createdAt", Integer.parseInt(LoadConfig.getConfigValue("registration_createdAt".toUpperCase())));
	        input.put("purpose", LoadConfig.getConfigValue("registration_purpose".toUpperCase()));
	        input.put("status", LoadConfig.getConfigValue("registration_status".toUpperCase()));
	        input.put("otp", otp);
	        
	        OutputStream os = conn.getOutputStream();
	        os.write(input.toString().getBytes());
	        os.flush();
	        
	        if (conn.getResponseCode() != 200) {
	        	
	            logger.info("Connection Failed : HTTP error code : "+ conn.getResponseCode());
	            return false;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        
	        JSONObject jObject = new JSONObject(output);
	        if(jObject.getString("message").equalsIgnoreCase("OTP Verified")) {
		        logger.info(jObject.getString("message"));
		        conn.disconnect();
		        return true;
	        }
	        logger.info(jObject.getString("message"));
	        
		}catch (Exception e) {
			logger.info("invalid user" + e);
		}
		return false;
	}
	
	@Override
	public List<ConsentDb> getAllConsents(int userid) {
		Session ses = null;
		List<ConsentDb> userOrgDomain = null;
		// get Session

		logger.info("Fetching Consent data for userid =>>>>>> " + userid);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<ConsentDb> query = ses.createQuery("Select u from ConsentDb u where u.userid = :userid", ConsentDb.class);
			query.setParameter("userid", userid);
			
			logger.info("Fetching Consent data for username query =>>>>>> " + query);
			userOrgDomain = query.getResultList();
		} catch (Exception e) {
			logger.error("userid not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userOrgDomain;
	}
	
	@Override
	public List<ConsentDb> getConsentsbyhospital(String hospitalname,int userid) {
		Session ses = null;
		List<ConsentDb> userOrgDomain = null;
		// get Session

		logger.info("Fetching Consent data for hospital =>>>>>> " + hospitalname);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<ConsentDb> query = ses.createQuery("Select u from ConsentDb u where u.userid = :userid and hospitalname= :hospitalname", ConsentDb.class);
			query.setParameter("hospitalname", hospitalname);
			query.setParameter("userid", userid);
			
			logger.info("Fetching Consent data for hospital query =>>>>>> " + query);
			userOrgDomain = query.getResultList();
		} catch (Exception e) {
			logger.error("hospital not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userOrgDomain;
	}
	
	@Override
	public ConsentDb getConsentbyID(String consentid) {
		Session ses = null;
		ConsentDb consentDomain = null;
		// get Session

		logger.info("Fetching Consent for consentid =>>>>>> " + consentid);
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<ConsentDb> query = ses.createQuery("Select u from ConsentDb u where u.consentid = :consentid", ConsentDb.class);
			query.setParameter("consentid", consentid);
			consentDomain = query.getSingleResult();
		} catch (Exception e) {
			logger.error("consentID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return consentDomain;
	}
	
	@Override
	public JSONObject getPatientDetails(String eManasId) {
		try {
			URL url = new URL("http://65.1.244.69:8080/MHMS_FHIR/fhir/hiu/getPatientDetails?emanasID=" + eManasId);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Authentication",LoadConfig.getConfigValue("API_TOKEN"));
	        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
	        
	        if (conn.getResponseCode() != 200) {
	        	
	            logger.info("Connection Failed : HTTP error code : "+ conn.getResponseCode());
	            return null;
	        }

	        Scanner sc = new Scanner(conn.getInputStream());
	        String output = "";
	        while (sc.hasNext()) {
	            output += sc.nextLine();
	        }
	        
	        JSONObject jObject = new JSONObject(output);
	        logger.info(jObject.getString("got patient deatails for id -> " + eManasId));
	        sc.close();
	        return (JSONObject) jObject.getJSONObject("response");
	        
		}catch (Exception e) {
			logger.info("invalid user" + e);
		}
		return null;
	}

	
}
