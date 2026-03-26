package com.cbi.ccr.csw.domain;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.StringUtils;

import com.cbi.ccr.csw.domain.ds.MyHikariDataSource;
import com.cbi.ccr.csw.encryption.LocalEncryptionConfig;
import com.cbi.ccr.csw.encryption.LocalEncryptionService;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ConfigHttp;
import com.cbi.frw.http.HttpUtilsNoProxy;
import com.cbi.frw.persistence.ConfigJpa;
import com.zaxxer.hikari.HikariConfig;

import javapasswordsdk.exceptions.PSDKException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource({ "classpath:/csw-domain.properties" })
@EnableJpaRepositories(basePackageClasses = ConfigCswDomain.class, entityManagerFactoryRef = "entityManagerFactory")
@EntityScan(basePackageClasses = ConfigCswDomain.class)
@Import({ ConfigCswDomainMssql.class, ConfigJpa.class, ConfigCswDomainMssql.class, ConfigHttp.class, LocalEncryptionConfig.class })
public class ConfigCswDomain {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.hikari.connection-timeout}")
	private long connectionTimeout;
	@Value("${spring.datasource.hikari.minimum-idle}")
	private int minimumIdle;
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private int maximumPoolSize;
	@Value("${spring.datasource.hikari.idle-timeout}")
	private long idleTimeout;
	@Value("${spring.datasource.hikari.max-lifetime}")
	private long maxLifetime;


	@Value("${vault.databaseUser}")
	private String[] vaultDatabaseUsers;
	
	@Value("${vault.query}")
	private String vaultQuery;
	
	@Value("${vault.appID}")
	private String vaultAppID;

	@Value("${vault.enabled}")
	private Boolean vaultEnabled;
	
	@Autowired
	private LocalEncryptionService localEncryptionService;
	
	@Primary
	@Bean(name = "dataSource")
	public DataSource dataSource(HttpUtilsNoProxy utilsNoProxy, ApplicationContext appContext) {

		HikariConfig config = new HikariConfig();
		log.info("------------------------- DB IP:{} ", url);
		log.info("------------------------- DB Using CyberArk:{} ", vaultEnabled);
		MyHikariDataSource ds;
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(localEncryptionService.decrypt(password));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(maximumPoolSize);
		config.setMinimumIdle(minimumIdle);
		config.setKeepaliveTime(30000);
		config.setValidationTimeout(30000);
		config.setConnectionTimeout(connectionTimeout);
		config.setMaxLifetime(maxLifetime);
		config.setIdleTimeout(idleTimeout);
		
		ds = new MyHikariDataSource(config, vaultDatabaseUsers, vaultQuery, vaultAppID, vaultEnabled, utilsNoProxy);

//		switch (JdbcDialectUtil.getDialect(url)) {
//		case POSTGRESQL:
//		case SQLSERVER:
//			config.setConnectionInitSql("select 1");
//			config.setConnectionTestQuery("select 1");
//			break;
//		case ORACLE:
//			config.setConnectionInitSql("select 1 from dual");
//			config.setConnectionTestQuery("select 1 from dual");
//			break;
//		default:
//			break;
//		}

		if(Boolean.TRUE.equals(vaultEnabled)) {
			try {
				ds.loadPassword();
			} catch (PSDKException e) {
				log.error("Unable to get password from Vault", e);
				System.exit(SpringApplication.exit(appContext, () -> -1));
			}
		}
		
		return ds;
	}
}
