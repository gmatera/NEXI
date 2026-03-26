package com.cbi.frw.ibmmq.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.http.truststore.TrustStoreManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsTest {

	public static void main(String[] args) {
		try {
			
			Path tsPath = Paths.get("src/test/clientkey.p12").toAbsolutePath();
			
			//SSLSocketFactory socketFactory = TrustStoreManager.configureTrustStore(tsPath.toString(), "changeit");

			
			 System.setProperty("javax.net.ssl.trustStore", tsPath.toString()); 
		     System.setProperty("javax.net.ssl.trustStorePassword","changeit");

			
			ConnectionConfig connConfig = ConnectionConfig.custom().setBufferSize(10000).setFragmentSizeHint(10000)
					.build();
			RequestConfig config = RequestConfig.custom()
					.setSocketTimeout(10000)
					.setConnectTimeout(10000)
					.setConnectionRequestTimeout(10000)
					.build();
			
			CloseableHttpClient httpClient = HttpClients.custom()
					  .setDefaultRequestConfig(config)
					  .setDefaultConnectionConfig(connConfig)
					  .disableAutomaticRetries()
					  .build();
			
			HttpGet httpget = new HttpGet("https://intapi.nexi.it/chc/ccr/jwks.json");
			
			
			
			try(CloseableHttpResponse resp = httpClient.execute(httpget)){
				
				String entity = EntityUtils.toString(resp.getEntity());
				
				if(resp.getStatusLine().getStatusCode() == 200) {
					log.info("ssl OK \n{}", entity);
				}else {
					log.error("ssl err", entity);
				}
			}
			
		} catch (IOException e) {
			
			log.error("ssl err", e);
		}	
	}
	
}
