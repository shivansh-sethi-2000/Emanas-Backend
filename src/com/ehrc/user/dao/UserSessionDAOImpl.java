package com.ehrc.user.dao;

import java.util.Date;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehrc.db.UserSessionDb;
import com.ehrc.utility.HibernateUtil;
import com.ehrc.utility.LoadConfig;

public class UserSessionDAOImpl implements UserSessionDAO {
	Logger logger = LoggerFactory.getLogger(UserSessionDAOImpl.class);

	@Override
	public UserSessionDb saveSession(String token, String userId) {
		Session ses = null;
		Transaction tx = null;
		UserSessionDb objUserSesDb = new UserSessionDb();
		// get Session
		ses = HibernateUtil.getSession();
		
			
			try	{
				objUserSesDb = UserSessionDb.mapUserSesCOToDB(token, userId);
				logger.info("User Session to be created for -> "+objUserSesDb.getUserId());
				
				tx = ses.beginTransaction();
				ses.saveOrUpdate(objUserSesDb);
				logger.info("User record created");
				tx.commit();
								
			} catch (Exception e) {
				logger.error("User record creation problem", e);
				tx.rollback();
				objUserSesDb = null;

			} finally {
				HibernateUtil.closeSession();
			}
		return objUserSesDb;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public long getSession(String sessionToken) {
		Session ses = null;
		long sesCount =0;
		// get Session

		logger.info("Fetching data for session Token =>>>>>> **" + sessionToken+"**");
		try {
			ses = HibernateUtil.getSession();
					
			TypedQuery query = ses.createQuery("Select count(*) from UserSessionDb u where u.sessionToken = :sessionToken");
			query.setParameter("sessionToken", sessionToken);
			sesCount = (long) query.getSingleResult();
		} catch (Exception e) {
			logger.info("session not found.");
			logger.error("ID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return sesCount;
	}
	
	@Override
	public UserSessionDb getSession(String sesId, String sessionToken) {
		Session ses = null;
		UserSessionDb userSesDomain = null;
		// get Session

		logger.info("Fetching data for session Token =>>>>>> **" + sessionToken+"**");
		try {
			ses = HibernateUtil.getSession();
			
			TypedQuery<UserSessionDb> query = ses.createQuery("Select u from UserSessionDb u where u.sessionToken = :sessionToken and u.id = :sesId", UserSessionDb.class);
			query.setParameter("sessionToken", sessionToken);
			query.setParameter("sesId", Integer.parseInt(sesId));
			userSesDomain = query.getSingleResult();
		} catch (Exception e) {
			logger.error("ID not found ::", e);
		}finally {
			HibernateUtil.closeSession();
		}
		return userSesDomain;
	}

	
	@SuppressWarnings({ "null", "unused" })
	@Override
	public boolean updateSession(String userId, String sessionToken) {
		Session ses = null;
		Transaction tx = null;
		UserSessionDb userSesDomain = null;
		int result = 0;
		// get Session

		logger.info("Fetching data for session Token =>>>>>> **" + sessionToken+"**");
		try {
			
			ses = HibernateUtil.getSession();
			tx = ses.beginTransaction();
			
			Query query = ses.createQuery("update UserSessionDb u set u.status = :status, u.logoutAt = :logoutAt where u.sessionToken = :sessionToken AND u.userId = :userId");
			query.setParameter("sessionToken", sessionToken);
			query.setParameter("status", LoadConfig.getConfigValue("INACTIVE_SESSION_STATUS"));
			query.setParameter("userId", userId);
			query.setParameter("logoutAt", new Date());
			result = query.executeUpdate();
			
			if(result != 0 ) {
				tx.commit();
				return true;
			} else {
				tx.rollback();
				return false;
			}
		} catch (Exception e) {
			logger.error("ID not found ::", e);
			tx.rollback();
			return false;
		}finally {
			HibernateUtil.closeSession();
		}
	}
	
	@Override
	public boolean updateUserSession(String sesId, String userId, String sessionToken) {
		Session ses = null;
		Transaction tx = null;
		int result = 0;
		// get Session

		logger.info("Fetching data for session Token =>>>>>> **" + sessionToken+"**");
		try {
			
			ses = HibernateUtil.getSession();
			tx = ses.beginTransaction();
			
			Date expiryDate = new Date(System.currentTimeMillis() + Long.parseLong(LoadConfig.getConfigValue("JWT_EXPIRY_TIMEOUT")));
			
			Query query = ses.createQuery("update UserSessionDb u set u.expiryAt = :expiryAt where u.id = :sesId and u.sessionToken = :sessionToken and u.userId = :userId and u.status = 'ACTIVE'");
			query.setParameter("expiryAt", expiryDate );
			query.setParameter("userId", userId );
			query.setParameter("sessionToken", sessionToken );
			query.setParameter("sesId", Integer.parseInt(sesId));
			result = query.executeUpdate();
			
			if(result != 0 ) {
				tx.commit();
				return true;
			} else {
				tx.rollback();
				return false;
			}
		} catch (Exception e) {
			logger.error("ID not found ::", e);
			tx.rollback();
			return false;
		}finally {
			HibernateUtil.closeSession();
		}
	}
}
