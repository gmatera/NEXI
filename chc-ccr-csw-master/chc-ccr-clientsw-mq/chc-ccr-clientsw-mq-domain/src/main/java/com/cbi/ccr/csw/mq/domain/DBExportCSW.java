package com.cbi.ccr.csw.mq.domain;

import com.cbi.frw.persistence.DBExportBase;

public class DBExportCSW {

	public static void main(String[] args) throws Exception {
//		DBExportBase.createSchemaPostgress(DBExportCSW.class.getPackage());
//		DBExportBase.createSchemaOracle(DBExportCSW.class.getPackage());
		DBExportBase.createSchemaMssql(DBExportCSW.class.getPackage());
	}
}
