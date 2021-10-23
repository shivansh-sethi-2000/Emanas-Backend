package com.ehrc.user.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehrc.authentication.Secured;
import com.ehrc.db.ConsentDb;
import com.ehrc.db.HospitalDb;
import com.ehrc.db.UserDb;
import com.ehrc.db.UserSessionDb;
import com.ehrc.user.co.ConsentCO;
import com.ehrc.user.co.UpdatePasswordCO;
import com.ehrc.user.co.UserCO;
import com.ehrc.user.dao.UserDAOImpl;
import com.ehrc.user.dao.UserSessionDAOImpl;
import com.ehrc.utility.Encryptions;
import com.ehrc.utility.JWTUtil;
import com.ehrc.utility.LoadConfig;
import com.ehrc.utility.ResponseConfig;

@Path("/user")
public class User {
	
	Logger logger = LoggerFactory.getLogger(User.class);
	UserDAOImpl objUserDAOImpl;
	UserSessionDAOImpl objUserSesDAOImpl;
	String saltValue = LoadConfig.getConfigValue("SALT_KEY");
	
	public User()
	{
		objUserDAOImpl = new UserDAOImpl();
		objUserSesDAOImpl = new UserSessionDAOImpl();
		
	}
	
	@GET
	@Path("/start")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CreateUser() throws Exception
	{
		ResponseConfig responses = new ResponseConfig();
		if (objUserDAOImpl.authenticateServer()) {
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("server authentication");
			responses.setMessage("{ \"status\":\"server authenticated successfully.\"}");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		}
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("server authentication");
		responses.setMessage("{ \"status\":\"Server authentication failed\"}");
		responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
		return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
	}


	@SuppressWarnings("unused")
	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CreateUser(UserCO objUserCO ) throws Exception
	{
		logger.info("Entering method with "+ objUserCO.getUsername());
		UserCO result = null;
		String strMessage="";
		com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("create user");
		Response.Status objRespStat = null;
		
		Response objCheckUserResponse = new User().checkUsername(objUserCO.getUsername());
		if(objCheckUserResponse.getStatus() == 200) {
			if(objUserCO != null)
			{
				objUserCO.setStatus(LoadConfig.getConfigValue("ACTIVE_STATUS"));
				
				String decryptedPassString = Encryptions.decrypt(objUserCO.getPassword());
//				Storing the Decrypted password.
				objUserCO.setPassword(decryptedPassString);
//				objUserCO.setPassword(objUserCO.getPassword());
				
				result = objUserDAOImpl.saveUser(objUserCO);
				logger.info("Result received from DAO---- "+ result );
				if(result.getUsername() != null)
				{
					UserDb objUserDb = objUserDAOImpl.getUser(result.getUsername());
					
					logger.info("user ID from DB---- "+ objUserDb.getUserId());
					
					StringBuilder objBody = new StringBuilder();
					objBody.append(LoadConfig.getConfigValue("EMAIL_BODY_LINE1"));
					objBody.append("\nFirst Name : "+result.getFirstName());
					objBody.append("\nLast Name : "+result.getLastName());
					objBody.append("\nUsername : "+result.getUsername());
					objBody.append("\nPassword : "+result.getPassword());
					objBody.append(LoadConfig.getConfigValue("EMAIL_NOTICE"));
					objBody.append(LoadConfig.getConfigValue("EMAIL_SALUTATION"));
					
					String emailBody = objBody.toString();
					
					
					result.setPassword(null);
					return Response.status(Response.Status.ACCEPTED).entity(result).build();
				}
				else
				{
					strMessage="Record could not be added";
					responses.setMessage(strMessage);
					objRespStat = Response.Status.UNAUTHORIZED;
					responses.setStatus(objRespStat.getStatusCode());	
					return Response.status(objRespStat).entity(responses).build();

				}
			} else {
				strMessage="No data available to create user";
				responses.setMessage(strMessage);
				objRespStat = Response.Status.BAD_REQUEST;
				responses.setStatus(objRespStat.getStatusCode());	
				return Response.status(objRespStat).entity(responses).build();
			}
		} else {
			logger.info("Username already taken.");
			responses.setMessage("Username already taken.");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
	}
	
	@SuppressWarnings("static-access")
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userLogin(UserCO objUserCO) throws Exception {
		
		String username = objUserCO.getUsername();
		String password = objUserCO.getPassword();
		
		String decryptedPassString = Encryptions.decrypt(password);
		
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		UserCO objUser = new UserCO();
		objUser.setPassword(objUserDb.getPassword());
		objUser = objUserDb.mapUserLoginDBToCO(objUserDb);
		
		if(objUser.getStatus().equals(LoadConfig.getConfigValue("ACTIVE_STATUS")) && objUser.getPassword().equals(decryptedPassString)) {
			objUser.setPassword(null);
			logger.info("Valid user");
			return Response.status(Response.Status.ACCEPTED).entity(objUser).build();
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("login");
			responses.setMessage("Login Failed");
			responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
			return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
		}
	}
	
	@SuppressWarnings("static-access")
	@POST
	@Path("/loginj")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userJWTLogin(UserCO objUserCO) throws Exception {
		
		String username = objUserCO.getUsername();
		String password = objUserCO.getPassword();

		String decryptedPassString = Encryptions.decrypt(password);
		
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		UserCO objUser = new UserCO();
		objUser.setPassword(objUserDb.getPassword());
		objUser = objUserDb.mapUserLoginDBToCO(objUserDb);
		
		if(objUser.getStatus().equals(LoadConfig.getConfigValue("ACTIVE_STATUS")) && objUser.getPassword().equals(decryptedPassString)) {
			objUser.setPassword(null);
			
			
			String fetchedToken = getToken();
			logger.info("Token generated for user ->"+objUser.getUsername()+" as "+fetchedToken);
			long sesCount = objUserSesDAOImpl.getSession(fetchedToken);
									
			if(sesCount == 0) {
				logger.info("Unique Token found for user ->"+username+" as "+fetchedToken);
				UserSessionDb objUserSes = objUserSesDAOImpl.saveSession(fetchedToken, objUser.getUserId());
				if(objUserSes != null) {
					logger.info("Valid user");
					logger.info("User value = "+objUserSes.toString());
					String jwtToken = new JWTUtil().createUserToken(Integer.toString(objUserSes.getId()), objUserSes.getSessionToken(), objUser); 
					String token = "{\"token\":\""+jwtToken+"\"}";
					logger.info("Token Recieved = "+token);
					return Response.status(Response.Status.ACCEPTED).entity(token).build();
				}else {
					logger.info("Unable to create a session for the user. Login failed");
					com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
					responses.setLink("http://iiitb.ac.in/");
					responses.setLocation("login");
					responses.setMessage("Unable to create a session for the user. Login Failed");
					responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
					return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
				}
			} else {
				logger.info("Duplicate token found for user ->"+objUser.getUsername()+" as "+fetchedToken);
				logger.info("Invalid user");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("login");
				responses.setMessage("Session Already active");
				responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
				return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
			}
			
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("login");
			responses.setMessage("Invalid Username/ Password");
			responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
			return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
		}
	}
	
	public String getToken() {
		String sesToken = UUID.randomUUID().toString();
		String localtime = Long.toString(System.currentTimeMillis());
		String timerToken = sesToken+localtime;
		String finalToken = UUID.nameUUIDFromBytes(timerToken.getBytes()).toString();
		return finalToken;
	}

	
	
	@SuppressWarnings({ "static-access", "unused" })
	@GET
	@Path("/loginj/refresh/{sesId}/{sesToken}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userJWTRefresh(@PathParam("sesId") String sesId, @PathParam("sesToken") String sesToken, @PathParam("username") String username) throws ParseException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {
		logger.info("refreshing JWT for  = "+sesId);
		
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		UserCO objUser = objUserDb.mapUserDBToCO(objUserDb);
		UserSessionDb objUserSesDB = objUserSesDAOImpl.getSession(sesId, sesToken);
		logger.info("Date diff =>>>>>>>>>>>>>>>>>>>> " + objUserSesDB.getExpiryAt().before(new Date()));
		if(objUserSesDB.getExpiryAt().after(new Date()) && objUserDb.getStatus().equals(LoadConfig.getConfigValue("ACTIVE_STATUS"))) {
			if (objUserSesDAOImpl.updateUserSession(sesId, objUser.getUserId(), sesToken)) {
				String jwtToken = new JWTUtil().createUserToken(sesId, sesToken, objUser);
				String token = "{\"token\":\"" + jwtToken + "\"}";
				logger.info("Token Recieved = " + token);
				return Response.status(Response.Status.ACCEPTED).entity(token).build();
			} else {
				Response res = logout(objUser.getUserId(), sesToken);
				logger.info("Unable to update the session for the user. Logging out.");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("refresh token");
				responses.setMessage("Unable to update the session for the user. Logging out.");
				responses.setStatus(Response.Status.FORBIDDEN.getStatusCode());	
				return Response.status(Response.Status.FORBIDDEN).entity(responses).build();
			}
		} else {
			Response res = logout(objUser.getUserId(), sesToken);
			logger.info("Session already expired. Please login again.");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("refresh token");
			responses.setMessage("Session already expired. Please login again.");
			responses.setStatus(Response.Status.FORBIDDEN.getStatusCode());	
			return Response.status(Response.Status.FORBIDDEN).entity(responses).build();
		}
	}
	
	@GET
	@Path("/logout/{userId}/{sessionToken}")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("userId") String userId, @PathParam("sessionToken") String sessionToken) throws ParseException {
		
		boolean result = new UserSessionDAOImpl().updateSession(userId, sessionToken);
		
		if(result != false ) {
			logger.info("Logged out Successfully.");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("logout");
			responses.setMessage("User logged out successfully");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		} else {
			logger.info("Logout failed");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("logout");
			responses.setMessage("Logout failed");
			responses.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());	
			return Response.status(Response.Status.UNAUTHORIZED).entity(responses).build();
		}
		
	}

		

	@GET
	@Path("/check/{username}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkUsername(@PathParam("username") String username) throws ParseException {
		
		UserDb objUser = objUserDAOImpl.getUser(username);
		if(objUser != null) {
			logger.info("Username already taken.");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("check");
			responses.setMessage("Username already taken");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		} else {
			logger.info("Username available for use.");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("check");
			responses.setMessage("Username available for use");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		}
	}
	
	@SuppressWarnings("static-access")
	@GET
	@Path("/secure/All/password")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkUsername() throws ParseException {
		List<UserDb> objUser = objUserDAOImpl.getAllUserPass();
		UserCO objUserCO = null;
//		String saltValue = genSalt();
//		String saltValue = LoadConfig.getConfigValue("SALT_KEY");
		logger.info("Salt value = "+saltValue);
		
		String[] userId = {new String()};
		if(objUser != null) {
			int i=0;
			for(UserDb userPass : objUser) {
				objUserCO = new UserCO();
				objUserCO = userPass.mapUserLoginDBToCO(userPass);
				String finalPassword = BCrypt.hashpw(objUserCO.getPassword(), saltValue);
				logger.info(i+". Original Password = "+objUserCO.getPassword());
				logger.info(i+". Hashed Password = "+finalPassword);
				objUserCO.setPassword(finalPassword);
				
				if(!objUserDAOImpl.updatePassword(objUserCO)) {
					userId[i] = objUserCO.getUserId();
					i++;
				}
				
			}
			
			if(userId.length == 0) {
				logger.info("Password secured for all.");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("secure password");
				responses.setMessage("Password secured for all.");
				responses.setStatus(Response.Status.ACCEPTED.getStatusCode());	
				return Response.status(Response.Status.ACCEPTED).entity(responses).build();
			} else {
				logger.info("Unable to secure Passwords for all.");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("secure password");
				responses.setMessage("Unable to secure Passwords for all. Please find the list of failed users "+userId);
				responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
				return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
			}
			
		} else {
			logger.info("No data to update");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("check");
			responses.setMessage("no data to update.");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
	}
	
	public static String genSalt() {

		return BCrypt.gensalt();
	}
	
	
	@SuppressWarnings({ "static-access" })
	@POST
	@Path("/update")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(UserCO objUserCo) throws ParseException {
		
		int userId = Integer.parseInt(objUserCo.getUserId());
		String status = new UserDAOImpl().getStatus(userId);
		
		UserDb objUserDb = new UserDb().mapUserUpdateCOToDB(objUserCo);
		
		if(status == null || status.equals("null")) {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("updateUser");
			responses.setMessage("{ \"status\":\"Update User failed.\"}");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		} else {
			if(status.equals(LoadConfig.getConfigValue("ACTIVE_STATUS"))) {
				
				
				
				if(new UserDAOImpl().updateUser(objUserDb)) {
					return Response.status(Response.Status.OK).entity("{ \"status\":\"User updated successfully.\"}").build();
				} else {
					logger.info("Invalid user");
					com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
					responses.setLink("http://iiitb.ac.in/");
					responses.setLocation("updateUser");
					responses.setMessage("{ \"status\":\"Update User failed.\"}");
					responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
					return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
				}
			} else if(status.equals(LoadConfig.getConfigValue("INACTIVE_STATUS"))) {
				if(new UserDAOImpl().updateUser(objUserDb)) {
					return Response.status(Response.Status.OK).entity("{ \"status\":\"User updated successfully.\"}").build();
				} else {
					logger.info("Invalid user");
					com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
					responses.setLink("http://iiitb.ac.in/");
					responses.setLocation("updateUser");
					responses.setMessage("{ \"status\":\"Update User failed.\"}");
					responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
					return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
				}
			} else {
				logger.info("Invalid user");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("updateUser");
				responses.setMessage("{ \"status\":\"Update User failed.\"}");
				responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
				return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
			}
		}
		
				
	}
	
	@POST
	@Path("/updatePassword")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePassword(UpdatePasswordCO objUpdatePass) throws ParseException {
		
		UserDb objUser = new UserDAOImpl().getUserById(objUpdatePass.getUserId());
		
		if(objUser.getStatus().equals(LoadConfig.getConfigValue("ACTIVE_STATUS"))) {
			
			
			try {
				String decryptedCurrentPass = Encryptions.decrypt(objUpdatePass.getCurrentPassword());
				String decryptedNewPass = Encryptions.decrypt(objUpdatePass.getNewPassword());				
				if(decryptedCurrentPass.equals(objUser.getPassword())) {
					if(new UserDAOImpl().updatePass(objUpdatePass.getUserId(), decryptedNewPass)) {
						logger.info("Password Updated Successfully.");
						com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
						responses.setLink("http://iiitb.ac.in/");
						responses.setLocation("updatePassword");
						responses.setMessage("{ \"status\":\"Password updated successfully.\"}");
						responses.setStatus(Response.Status.OK.getStatusCode());	
						return Response.status(Response.Status.OK).entity(responses).build();
					} else {
						logger.info("Current Password did not match the provided string.");
						com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
						responses.setLink("http://iiitb.ac.in/");
						responses.setLocation("updatePassword");
						responses.setMessage("{ \"status\":\"Something went wrong. Password updation failed. Please contact Administrator.\"}");
						responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
						return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
					}
				} else {
					logger.info("Current Password did not match the provided string.");
					com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
					responses.setLink("http://iiitb.ac.in/");
					responses.setLocation("updatePassword");
					responses.setMessage("{ \"status\":\"Current Password did not match the provided string.\"}");
					responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
					return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Current Password did not match the provided string.");
				com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("updatePassword");
				responses.setMessage("{ \"status\":\"Something went wrong. Password updation failed. Please contact Administrator.\"}");
				responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
				return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
			}
			
			
		} else  {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("updatePassword");
			responses.setMessage("{ \"status\":\"Update Password Failed. Seems like the account has been deactivated.\"}");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}		
	}
	
	
	@POST
	@Path("/getHealthRecords/{username}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getHealthRecords(ConsentCO objCo, @PathParam("username") String username) {
		
		ResponseConfig responses = new ResponseConfig();
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		if(objUserDAOImpl.CheckConsent(objCo, objUserDb.getUserId()) == false) {
			objCo.setUserID(objUserDb.getUserId());
			if(objUserDAOImpl.consentRequest(objCo)) {
				responses.setLink("http://iiitb.ac.in/");
				responses.setLocation("getHealthRecords");
				responses.setMessage("{ \"status\":\"otp send to user.\"}");
				responses.setStatus(Response.Status.OK.getStatusCode());	
				return Response.status(Response.Status.OK).entity(responses).build();
			}
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getHealthRecords");
			responses.setMessage("{ \"status\":\"otp could not be send.\"}");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
		if(objUserDAOImpl.getHealthRecords(objCo, username) != null) {
			
		}
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("getHealthRecords");
		responses.setMessage("{ \"status\":\"could not get health records\"}");
		responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
		return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		
	}
	
	
	@POST
	@Path("/verifyConsentOTP/{otp}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response verifyConsentOTP(@PathParam("otp") String otp, ConsentCO objConsentCO, @PathParam("username") String username) {
		ResponseConfig responses = new ResponseConfig();
		UserDb objDb = objUserDAOImpl.getUser(username);
		objConsentCO.setUserID(objDb.getUserId());
		if(objUserDAOImpl.verifyConsentRequest(otp, objConsentCO)) {
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("verifyConsentOTP");
			responses.setMessage("{ \"status\":\"Consent Verified successfully.\"}");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		}
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("verifyConsentOTP");
		responses.setMessage("{ \"status\":\"Consent Verification failed\"}");
		responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
		return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
	}
	
	@POST
	@Path("/authenticateUser/{eManasId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authencticateUser(@PathParam("eManasId") String eManasId) {
		ResponseConfig responses = new ResponseConfig();
		if(objUserDAOImpl.authenticateUser(eManasId)) {
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("authenticateUser");
			responses.setMessage("{ \"status\":\"otp send successfully.\"}");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		}
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("authenticateUser");
		responses.setMessage("{ \"status\":\"Verification failed\"}");
		responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
		return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
	}
	
	@POST
	@Path("/verifyRegistrationOTP/{otp}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response verifyRegistrationOTP(@PathParam("otp") String otp) {
		ResponseConfig responses = new ResponseConfig();
		if(objUserDAOImpl.VerifyRegistrationOTP(otp)) {
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("verifyRegistrationOT");
			responses.setMessage("{ \"status\":\"user verified successfully.\"}");
			responses.setStatus(Response.Status.OK.getStatusCode());	
			return Response.status(Response.Status.OK).entity(responses).build();
		}
		responses.setLink("http://iiitb.ac.in/");
		responses.setLocation("verifyRegistrationOT");
		responses.setMessage("{ \"status\":\"Verification failed\"}");
		responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
		return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
	}
	
	@SuppressWarnings("static-access")
	@GET
	@Path("/getConsentByHospital/{hospitalname}/{username}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getConsentbyhospital(@PathParam("hospitalname") String hospitalname, @PathParam("username") String username) throws ParseException, org.apache.tomcat.util.json.ParseException {
		
		logger.info("hospitalname = "+hospitalname);
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		List<ConsentDb> objConsentDbList = objUserDAOImpl.getConsentsbyhospital(hospitalname, objUserDb.getUserId());
		ConsentDb objConsentDb = null;
		List<ConsentCO> objConsentCOList = new ArrayList<ConsentCO>();
		if(objConsentDbList.size() != 0) {
			
			logger.info("List Size = "+objConsentDbList.size());
			for(ConsentDb objDb : objConsentDbList) {
				objConsentCOList.add(objConsentDb.mapConsentDBToCO(objDb));
			}
			
			return Response.status(Response.Status.ACCEPTED).entity(objConsentCOList).build();
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getConsentbyhospital");
			responses.setMessage("Get Consent By hospital Failed");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}		
	}
	
	@SuppressWarnings("static-access")
	@GET
	@Path("/getConsentByusername/{username}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getConsentbyusername(@PathParam("username") String username) throws ParseException, org.apache.tomcat.util.json.ParseException {
		
		logger.info("username = "+username);
		UserDb objUserDb = objUserDAOImpl.getUser(username);
		List<ConsentDb> objConsentDbList = objUserDAOImpl.getAllConsents(objUserDb.getUserId());
		ConsentDb objConsentDb = null;
		List<ConsentCO> objConsentCOList = new ArrayList<ConsentCO>();
		if(objConsentDbList.size() != 0) {
			
			logger.info("List Size = "+objConsentDbList.size());
			for(ConsentDb objDb : objConsentDbList) {
				objConsentCOList.add(objConsentDb.mapConsentDBToCO(objDb));
			}
			
			return Response.status(Response.Status.ACCEPTED).entity(objConsentCOList).build();
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getConsentbyusername");
			responses.setMessage("Get Consent By username Failed");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}		
	}
	
	@SuppressWarnings("static-access")
	@GET
	@Path("/getConsentByConsentId/{consentid}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getConsentbyConsentID(@PathParam("consentid") String consentid) throws ParseException, org.apache.tomcat.util.json.ParseException{
		
		logger.info("consentid = "+consentid);
		ConsentDb objConsentDb = objUserDAOImpl.getConsentbyID(consentid);
		ConsentCO objConsentCO = null;
		if(objConsentDb != null) {
			objConsentCO = objConsentDb.mapConsentDBToCO(objConsentDb);
			return Response.status(Response.Status.ACCEPTED).entity(objConsentCO).build();
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getConsentbyusername");
			responses.setMessage("Get Consent By username Failed");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
	}
	
	@GET
	@Path("/getHospitalList")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getHospitalList() throws ParseException, org.apache.tomcat.util.json.ParseException{
		
		List<HospitalDb> hospitalDbs = objUserDAOImpl.getHospitalList();
		if(hospitalDbs != null) {
			return Response.status(Response.Status.ACCEPTED).entity(hospitalDbs).build();
		} else {
			logger.info("Invalid user");
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getConsentbyusername");
			responses.setMessage("Get Consent By username Failed");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
	}
	
	@GET
	@Path("/getPatientDetails/{emanasid}")
//	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPatientDetails(@PathParam("emanasid") String emanasid) throws ParseException, org.apache.tomcat.util.json.ParseException{
		
		JSONObject details = objUserDAOImpl.getPatientDetails(emanasid);
		if(details != null) {
			return Response.status(Response.Status.ACCEPTED).entity(details).build();
		} else {
			com.ehrc.utility.ResponseConfig responses = new ResponseConfig();
			responses.setLink("http://iiitb.ac.in/");
			responses.setLocation("getPatientDetails");
			responses.setMessage("Get patient Details failed");
			responses.setStatus(Response.Status.BAD_REQUEST.getStatusCode());	
			return Response.status(Response.Status.BAD_REQUEST).entity(responses).build();
		}
	}

	
	
	public static void main(String[] args) {
		
	
	}
	
}