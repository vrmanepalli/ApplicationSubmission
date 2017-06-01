/*
 * PushProfilesValidityCheck is a listener which runs when the application is launched on the Server
 * -> contextDestroyed(ServletContextEvent arg0) : When the application is stopped this method is called and closes the connection, unschedules the job
 * -> doSetupDatabase(ServletContext ctx) : Sets up the database connection, log4j Connection
 * -> contextInitialized(ServletContextEvent arg0)  : This method is called when the application is started on the Server, 
 * 		which calls doSetupDatabase method and schedules a Job to be called in the first week of every month.
 * 
 */
package com.nike.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.nike.util.DBConnectionManager;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.ProtectedConfigFile;
import com.nike.util.PushProfilesValidityCheckJob;
import com.nike.util.TableConstants;

public class PushProfilesValidityCheckListener implements ServletContextListener{

	public static  Scheduler scheduler;
	static Logger logger = LogManager.getLogger(PushProfilesValidityCheckListener.class.getName());
	private CronTrigger trigger;
	private String MAC_MINI_PWD;
	private String NIKE_GENERIC_PWD;
	public static String JENKINS_PWD;
	private String DATABASE_PWD;
	private String DATABASE_USER_NAME;
	private String DATABASE_URL;
	public static String MOBILE_IRON_PWD;
	public static String JENKINS_URL;
	public static String JENKINS_USER_NAME;
	public static String JENKINS_TOKEN;
	public static String JENKINS_JOB_NAME;
	 
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
        Connection con = (Connection) arg0.getServletContext().getAttribute("DBConnection");
        try {
        	TriggerKey triggerKey = new TriggerKey("PushProfileValidityCheck", "group1");
        	scheduler.unscheduleJob(triggerKey);
            con.close();
        } catch (SQLException e) {
        	logger.error("SQLException in closing connection " + ErrorUtils.getStackTrace(e));
        } catch (SchedulerException e) {
        	logger.error("SchedulerException in unscheduling the job from Quartz, " + ErrorUtils.getStackTrace(e));
		}
    }

	protected void doSetupDatabase(ServletContext ctx) {
        //initialize DB Connection
		readConfigurationFile();
         
        try {
            DBConnectionManager connectionManager = new DBConnectionManager(DATABASE_URL, DATABASE_USER_NAME, DATABASE_PWD);
            ctx.setAttribute("DBConnection", connectionManager.getConnection());
            System.out.println("DB Connection initialized successfully.");
            logger.info("DB Connection initialized successfully.");
        } catch (ClassNotFoundException e) {
        	logger.error("ClassNotFoundException" + ErrorUtils.getStackTrace(e));
        } catch (SQLException e) {
        	logger.error("SQLException " + ErrorUtils.getStackTrace(e));
        }
    }
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		ServletContext ctx = arg0.getServletContext();
		doSetupDatabase(ctx);
		Connection connection = MySqlDatabaseConnection.getConnection(ctx);
		if(connection == null) {
			connection = MySqlDatabaseConnection.getConnection(ctx);
		}
//		Scheduling the Push Notification Profile expiration dates
		JobDataMap map = new JobDataMap();
	    map.put("DBConnection", connection);
		JobDetail job = JobBuilder.newJob(PushProfilesValidityCheckJob.class).withIdentity("PushProfilesValidityCheckJob", "QuartzGroup").setJobData(map).build();
		trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("PushProfileValidityCheck", "QuartzGroup")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0/45 1-3 ? * WED"))  //Every 45th minute, between 1am and 3am on Every Wednesday.
				//.withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 6-7 4 * ?"))//Every month on 10th, at 11.05.. and 12.30 //Every month on 4th, at 6.30 and 7.30am
				.build();
		HashSet<Trigger> set = new HashSet<Trigger>();
		set.add(trigger);
		try {
			String quartzConfigPath = ctx.getInitParameter("quartz:properties");
			String webAppPath = ctx.getRealPath("/");
			
            String quartzProp = webAppPath + quartzConfigPath;
            
			scheduler = new StdSchedulerFactory(quartzProp).getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, set, true);
			logger.info("Scheduler initialization was successful.");
		} catch (SchedulerException e1) {
			logger.error("SchedulerException in scheduling a job " + ErrorUtils.getStackTrace(e1));
		}
	}
	
	private void readConfigurationFile() {
		File configFile = new File(TableConstants.CONFIG_FILE_PATH);
		if(configFile.exists()) {
			try {
				FileReader reader = new FileReader(configFile);
				BufferedReader bufferReader = new BufferedReader(reader);
				String line = null;
				while((line = bufferReader.readLine()) != null) {
					if(line.equals("MAC_MINI_CREDENTIALS")) {
						try {
							MAC_MINI_PWD = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("NIKE_GENERIC_CREDENTIALS")) {
						try {
							NIKE_GENERIC_PWD = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("JENKINS_URL")) {
						try {
							JENKINS_URL = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("JENKINS_TOKEN")) {
						try {
							JENKINS_TOKEN = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("JENKINS_USER_NAME")) {
						try {
							JENKINS_USER_NAME = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("JENKINS_CREDENTIALS")) {
						try {
							JENKINS_PWD = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("JENKINS_JOB_NAME")) {
						try {
							JENKINS_JOB_NAME = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("DATABASE_URL")) {
						try {
							DATABASE_URL = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("DATABASE_USER_NAME")) {
						try {
							DATABASE_USER_NAME = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("DATABASE_CREDENTIALS")) {
						try {
							DATABASE_PWD = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					} else if(line.equals("MOBILE_IRON_CREDENTIALS")) {
						try {
							MOBILE_IRON_PWD = ProtectedConfigFile.decrypt(bufferReader.readLine());
						} catch (GeneralSecurityException e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					}
				}
			} catch (FileNotFoundException e) {
				logger.error(ErrorUtils.getStackTrace(e));
			} catch (IOException e) {
				logger.error(ErrorUtils.getStackTrace(e));
			} catch (Exception e1) {
				logger.error(ErrorUtils.getStackTrace(e1));
			}
			
		}
	}
	
	public void getSystemProperty() {
		System.setProperty("CONFIG_PROFILE_PATH", "Z:/MobileApps/Test/ConfigProfile.txt");
		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
		  String key = (String)keys.nextElement();
		  String value = (String)p.get(key);
		  logger.debug(key + ": " + value);
		}
	}

	public String getMAC_MINI_PWD() {
		return MAC_MINI_PWD;
	}

	public void setMAC_MINI_PWD(String mAC_MINI_PWD) {
		MAC_MINI_PWD = mAC_MINI_PWD;
	}

	public String getNIKE_GENERIC_PWD() {
		return NIKE_GENERIC_PWD;
	}

	public void setNIKE_GENERIC_PWD(String nIKE_GENERIC_PWD) {
		NIKE_GENERIC_PWD = nIKE_GENERIC_PWD;
	}

	public String getDATABASE_PWD() {
		return DATABASE_PWD;
	}

	public void setDATABASE_PWD(String dATABASE_PWD) {
		DATABASE_PWD = dATABASE_PWD;
	}
	
	
}
