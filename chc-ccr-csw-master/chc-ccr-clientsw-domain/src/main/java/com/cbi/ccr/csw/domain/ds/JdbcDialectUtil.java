package com.cbi.ccr.csw.domain.ds;

import com.cbi.frw.common.exception.ChcSystemException;



public class JdbcDialectUtil {

	
	private static final String POSTGRES = "postgresql";
	private static final String ORACLE = "oracle";
	private static final String SQLSERVER = "sqlserver";
	
	
	private JdbcDialectUtil() {
		throw new UnsupportedOperationException("static");
	}
	
	public static JdbcDialect getDialect(String datasourceUrl) {
		
		if(datasourceUrl.toLowerCase().contains(POSTGRES))
			return JdbcDialect.POSTGRESQL;
		
		if(datasourceUrl.toLowerCase().contains(ORACLE))
			return JdbcDialect.ORACLE;
		
		if(datasourceUrl.toLowerCase().contains(SQLSERVER))
			return JdbcDialect.SQLSERVER;
		
		throw new ChcSystemException(String.format("Could not detect database Dialect from url:%s Abortng ", datasourceUrl));
	}
			
}
