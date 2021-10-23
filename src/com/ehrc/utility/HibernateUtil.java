package com.ehrc.utility;
import java.io.File;

import javax.persistence.EntityManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
   static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
   private static ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
   private static SessionFactory factory = null;
   public synchronized static SessionFactory getSessionFactory() {
       if (factory == null) {
           StandardServiceRegistry registry = null;
           try {
               // Create registry
               registry = new StandardServiceRegistryBuilder().configure(new File(LoadConfig.getConfigValue("DB_CONFIG"))).build();
               // Create MetadataSources
               MetadataSources sources = new MetadataSources(registry);
               // Create Metadata
               Metadata metadata = sources.getMetadataBuilder().build();
               // Create SessionFactory
               factory = metadata.getSessionFactoryBuilder().build();
               
//               logger.info("-------------------- Instantiating MHMS DBConfig. ----------------------");
           } catch (Exception e) {
               logger.error("Error initializing database", e);
               if (registry != null) {
                   StandardServiceRegistryBuilder.destroy(registry);
               }
           }
       }
       return factory;
   }
   public static EntityManager getEntityManagerFactory() {
       return factory.createEntityManager();
   }
   public static Session getSession() {
       Session ses = null;
       ses = threadLocal.get();
       if (ses == null || !ses.isOpen()) {
           try {
        	   getSessionFactory();
//        	   logger.debug("************** Did not find a session in thread local. Creating new session. ***************");
               ses = factory.openSession();
               threadLocal.set(ses);
           } catch (HibernateException e) {
               logger.error("hibernate util session = >>", e);
           }
       } else {
//    	   logger.debug("************** Found a session in thread local. *************");
       }
       return ses;
   }
   public static void closeSession() {
       Session ses = null;
       ses = threadLocal.get();
       if (ses != null) {
           ses.close();
           threadLocal.remove();
       }
   }
   public static void closeSessionFactory() {
       factory.close();
   }
}