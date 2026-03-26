package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1413SecPosCreateFileind extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1413";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer syncFlag;
	@NotNull
	private String vfn;
	private byte[]  fillerOne;
	@NotNull
	private ZonedDateTime acceptTms;
	
	private String corrId;
	private byte[]  fillerTwo;
	
	@NotNull
	private ZonedDateTime startCreateTms;
	@NotNull
	private ZonedDateTime endCreateTms;
	@NotNull
	private String queueFileName;
	@NotNull
	private byte[] groupId = createByteArray((byte)0x00 , 24);
	@NotNull
	private Integer lineSeparator;
	@NotNull
	private Integer recType;
	@NotNull
	private Integer maxRecLen;
	private byte[]  fillerThree;
	@NotNull
	private Integer baFileSize;
	@NotNull
	private Integer charType;
	@NotNull
	private String compressAlgo;
	@NotNull
	private Integer netFileSize;
	
	private byte[]  localBaData;
	private byte[]  fillerFour;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
	private byte[] createByteArray(byte byteChar, int size) {
		byte[] bytes = new byte[size + 1];
		for (int i = 1; i <= size; i++) {
			bytes[i] = byteChar;
		}
		return bytes;
	}

	
}
