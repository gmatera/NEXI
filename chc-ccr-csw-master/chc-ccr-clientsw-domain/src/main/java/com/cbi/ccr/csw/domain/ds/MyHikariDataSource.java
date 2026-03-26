package com.cbi.ccr.csw.domain.ds;

import java.sql.Connection;
import java.sql.SQLException;

import com.cbi.frw.http.HttpUtilsNoProxy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javapasswordsdk.PSDKPassword;
import javapasswordsdk.PSDKPasswordRequest;
import javapasswordsdk.exceptions.PSDKException;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class MyHikariDataSource extends HikariDataSource{
	
	
	private int lastUsedUserIndex = -1;
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	private String[] vaultDatabaseUsers;
	private String vaultQuery;
	private String vaultAppID;
	private Boolean vaultEnabled;
	
	public MyHikariDataSource(HikariConfig configuration, 
			String[] vaultDatabaseUsers,  String vaultQuery, String vaultAppID,
			Boolean vaultEnabled, HttpUtilsNoProxy httpUtilsNoProxy) {
		
		super(configuration);
		this.vaultDatabaseUsers = vaultDatabaseUsers;
		this.vaultQuery = vaultQuery;
		this.vaultAppID = vaultAppID;
		this.vaultEnabled = vaultEnabled;
		
		this.httpUtilsNoProxy = httpUtilsNoProxy;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		
		// to test this feature read the readme.md in this project
		
		try {
			return super.getConnection();
		} catch (SQLException e) {
			
			// Oracle: ORA-01017: invalid username/password; logon denied
			// SQL Server: Login failed for user
			// postgres: password authentication failed
			
			if(e.getCause() != null && 
					(e.getCause().toString().contains("password authentication failed") || 
							e.getCause().toString().contains("invalid username/password") || 
							e.getCause().toString().contains("Login failed for user"))) {
				
				reloadPassword(e);
				return super.getConnection();
			}else {
				throw e;
			}
		}
	}
	
	public void reloadPassword(SQLException e) throws SQLException {
		// vault not present, throwing original exception. 
		if(Boolean.FALSE.equals(vaultEnabled)) throw e;
				
				
		try {
			loadPassword();
		} catch (PSDKException e1) {
			log.error("Unable to get password from Vault", e1);
			throw e;
		}
	}
	
	public void loadPassword() throws PSDKException {
		
		lastUsedUserIndex++;
		
		if(lastUsedUserIndex > vaultDatabaseUsers.length -1) {
			lastUsedUserIndex = 0;
		}
		
		String user = vaultDatabaseUsers[lastUsedUserIndex];
		String pwd;
		
		log.info("######### Try to get a new password from Vault for the user {}, vaultQuery:{} appID:{}", user, vaultQuery, vaultAppID);

		PSDKPassword password = null;
		PSDKPasswordRequest passRequest = new PSDKPasswordRequest();

         // Set request properties
         passRequest.setAppID(vaultAppID);
         passRequest.setQuery(vaultQuery);
         passRequest.setReason("Get pwd for CSW");

         // Get password object
         password = javapasswordsdk.PasswordSDK.getPassword(passRequest);

         // Get password content
         pwd = password.getContent();
		
		log.info("######### Got a new password. Now using user:{}", user);
		
		getHikariConfigMXBean().setUsername(user);
		getHikariConfigMXBean().setPassword(pwd);
		getHikariPoolMXBean().softEvictConnections();
		
	}
}
