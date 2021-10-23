package com.ehrc.utility;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ehrc.db.UserSessionDb;
import com.ehrc.user.co.UserCO;
import com.ehrc.user.dao.UserSessionDAOImpl;

public class JWTUtil {

	Logger logger = LoggerFactory.getLogger(JWTUtil.class);

	static String token = null;
	static RSAPublicKey publicKey = null;
	static RSAPrivateKey privateKey = null;
	static KeyPair pair;
	String path = "/home/iiitb/mhms/config/";
	//String path = LoadConfig.getConfigValue("MHMS_KEY_FILE");

	public String createUserToken(String sesId, String sessionToken, UserCO objUser) throws IllegalArgumentException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException {
		try {
			Algorithm algorithm = Algorithm.HMAC256(LoadConfig.getConfigValue("JWT_HMAC256_KEY"));

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Host", "PHIA");
			map.put("Developed By", "Patharitech & IIIT Bangalore");
			
			token = JWT.create().withIssuer(LoadConfig.getConfigValue("JWT_ISSUER_KEY")).withIssuedAt(new Date())
					.withHeader(map).withNotBefore(new Date())
					.withSubject("Security Token")
					.withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(LoadConfig.getConfigValue("JWT_EXPIRY_TIMEOUT"))))
					.withClaim("userId", objUser.getUserId())
					.withClaim("username", objUser.getUsername())
					.withClaim("firstName", objUser.getFirstName())
					.withClaim("lastName", objUser.getLastName())
					.withClaim("status", objUser.getStatus())
					.withClaim("sessionToken", sessionToken)
					.withClaim("sessionId", sesId)
					.sign(algorithm);

		} catch (Exception exception) {
			logger.error("", exception);
		}
		logger.info("Token = "+token);
		return token;
	}

	public boolean verify(String token) {
		boolean result = false;
		try {

			JWTVerifier verifier = JWT.require(Algorithm.HMAC256("secret")).acceptExpiresAt(0).build();
			DecodedJWT jwt = verifier.verify(token);
			
			
			Map<String, Claim> claims = jwt.getClaims();
			Claim userIDClaim = claims.get("userId");
			Claim sessionTokenClaim = claims.get("sessionToken");
			Claim sessionIdClaim = claims.get("sessionId");
			
			
			UserSessionDb objUserSession = new UserSessionDAOImpl().getSession(sessionIdClaim.asString(), sessionTokenClaim.asString());
			logger.info("Value fetched = "+objUserSession.getSessionToken());
			logger.info("Value Expiry = "+objUserSession.getExpiryAt());
			logger.info("Value Status = "+objUserSession.getStatus());
			
			if(objUserSession != null && (objUserSession.getSessionToken().equals(sessionTokenClaim.asString())) && (objUserSession.getStatus().equals(LoadConfig.getConfigValue("ACTIVE_SESSION_STATUS")))) {
								
				logger.info("Value for UserID = "+objUserSession.getUserId());
				int expiryTimeDifference = Seconds.secondsBetween(new DateTime(new Date()), new DateTime(objUserSession.getExpiryAt())).getSeconds();
				
//				int updationTimeGap = Seconds.secondsBetween(new DateTime(objUserSession.getLastLogin()), new DateTime(new Date())).getSeconds();
//				logger.info("TIME GAP = "+updationTimeGap);
				
				Claim jwtExpiryClaim = claims.get("exp");
				int jwtExpiryGap = Seconds.secondsBetween(new DateTime(jwtExpiryClaim.asLong()), new DateTime(new Date())).getSeconds();
				logger.info("JWT Expiry = "+jwtExpiryGap);
				
				if(expiryTimeDifference>0) {
					if(expiryTimeDifference<600) {
						logger.info("Session is valid.");
						boolean objUpdateSession = new UserSessionDAOImpl().updateUserSession(sessionIdClaim.asString(), userIDClaim.asString(), sessionTokenClaim.asString());
						if(objUpdateSession) {
							logger.info("Session Updated.");
							result = true;
						} else {
							logger.info("unable to update Session. Login again.");
							result = false;
						}
					} else {
						logger.info("no need to update Session.");
						result = true;
					}
					
				} else {
					if(jwtExpiryGap<=0) {
						logger.info("JWT Expired = ");
					}
					logger.info("Token Expired");
					logger.info("Session expired. Login again.");
					result = false;
				}
				
			} else {
				logger.info("Token Failed");
				result = false;
			}			
		} catch (Exception e) {
			result = false;
			logger.error("Error in JWT verification", e);
		}
		return result;
	}
	
	public static void main(String[] args) {
//		System.out.println("Current Time = "+System.currentTimeMillis());
//		
//		String sesToken = UUID.randomUUID().toString();
//		String localtime = Long.toString(System.currentTimeMillis());
//		String timerToken = sesToken+localtime;
//		String finalToken = UUID.nameUUIDFromBytes(timerToken.getBytes()).toString();
//		
//		System.out.println("Current Token = "+finalToken);
		
		String token = "eyJEZXZlbG9wZWQgQnkiOiJQYXRoYXJpdGVjaCAmIElJSVQgQmFuZ2Fsb3JlIiwiSG9zdCI6IlBISUEiLCJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZWN1cml0eSBUb2tlbiIsImxhc3ROYW1lIjoiS3VtYXIiLCJyb2xlIjoiQWRtaW4iLCJpc3MiOiJLTUhNUyIsInNlc3Npb25JZCI6Ijc0MSIsInVzZXJJZCI6IjEiLCJmaXJzdE5hbWUiOiJOaXRlc2giLCJuYmYiOjE1ODY4NTY1MjIsInNlc3Npb25Ub2tlbiI6IjQ1NjRmZDg5LWFjODEtM2FkZi1hMmJkLWI0YTgzMmE2YmZjZiIsImV4cCI6MTU4Njg1NjcwMiwiaWF0IjoxNTg2ODU2NTIyLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwic3RhdHVzIjoiQUNUSVZFIn0.mt7ZXxt1CSlI741Bl3-stVr5SALf275Xlp9PoeB7Rbs";
		new JWTUtil().verify(token);
	}

}
