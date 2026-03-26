package com.cbi.ccr.app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.FileUtils;
import com.cbi.frw.http.ChcStubException;
import com.cbi.repo.stub.RepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = CcrAllApp.class)
public class CcrRepoTest {

	@Autowired
	RepoStub repoStub;
	
	@Autowired
	protected CCRJweJwtService ccrJweJwtService;
	
	@Test
	void testRepo() throws ChcStubException, ChcException, IOException {
		InputStream is = getClass().getResourceAsStream("/2MB.pdf");
		File fileUpload = FileUtils.saveTemp(is);
		
		String id = UUID.randomUUID().toString();
		
		String jwt = ccrJweJwtService.getJwt();
		repoStub.upload(fileUpload, id, "test1", jwt);
		
		File f = repoStub.download(id, jwt);
		assertEquals(fileUpload.length(), f.length());
		assertTrue( org.apache.commons.io.FileUtils.contentEquals(f, fileUpload));
	}
	
	@Test
	void stubFileUploadBig() throws ChcException, ChcStubException, IOException {
		File fileUpload = new File("/home/mek/Downloads/robo3t.zip");;
		try {
			InputStream is = new FileInputStream(fileUpload);
			//input = createBigFile();
			
			UUID id = UUID.randomUUID();
			log.info("FILE ID {}", id);
			
			String jwt = ccrJweJwtService.getJwt();
			System.out.println(jwt);
			
			assertDoesNotThrow( () -> repoStub.upload(is, id.toString(), "big-file.zip", ccrJweJwtService.getJwt(), fileUpload.length()));
			
			assertDoesNotThrow( () -> {
				File f = repoStub.download(id.toString(), ccrJweJwtService.getJwt());
				
				assertNotNull(f);
				assertEquals(fileUpload.length(), f.length());
				assertTrue( org.apache.commons.io.FileUtils.contentEquals(f, fileUpload));
				
				//f.delete();
			});
			
		} finally {
			//Files.delete(input.toPath());
		}
		
	}
	
	@Test
	void stubFileUploadBig100MB() throws ChcException, ChcStubException, IOException {
		File fileUpload = createBigFile();
		try {
			InputStream is = new FileInputStream(fileUpload);
			//input = createBigFile();
			
			UUID id = UUID.randomUUID();
			log.info("FILE ID {}", id);
			
			String jwt = ccrJweJwtService.getJwt();
			System.out.println(jwt);
			
			assertDoesNotThrow( () -> repoStub.upload(is, id.toString(), "big-file.zip", ccrJweJwtService.getJwt(), fileUpload.length()));
			
			assertDoesNotThrow( () -> {
				File f = repoStub.download(id.toString(), ccrJweJwtService.getJwt());
				
				assertNotNull(f);
				assertEquals(fileUpload.length(), f.length());
				assertTrue( org.apache.commons.io.FileUtils.contentEquals(f, fileUpload));
				
				//f.delete();
			});
			
		} finally {
			//Files.delete(input.toPath());
		}
		
	}
	
	@Test
	void stubFileDownloadBig() throws ChcException, ChcStubException, IOException {
		
		try {
			UUID id = UUID.fromString("2be61565-9f0b-424a-85ee-9dc092f6fbfd");
			
			assertDoesNotThrow( () -> {
				File f = repoStub.download(id.toString(), ccrJweJwtService.getJwt());
				
				assertNotNull(f);
				f.delete();
			});
		} finally {
			
		}
		
	}
	
	
	File createBigFile() throws IOException {
		File f = new File("/tmp/zzzzzz");
		
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter br = new BufferedWriter(osw);
		
		long len = 0;
		String aa = "aaaa";
		int l = aa.getBytes().length;
		// 100MB
		while(len < 100000000L) {
			br.append(aa);
			len+= l;
			if(len % 5000 == 0) {
				br.flush();
			}
		}
		br.flush();
		br.close();
		fos.close();
		return f;
	}
}
