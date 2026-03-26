package com.cbi.ccr.domain;

import com.cbi.frw.persistence.DBExportBase;

public class DBExportCCR {

	public static void main(String[] args) throws Exception {
		DBExportBase.createSchemaOracle(DBExportCCR.class.getPackage());
	}
}
