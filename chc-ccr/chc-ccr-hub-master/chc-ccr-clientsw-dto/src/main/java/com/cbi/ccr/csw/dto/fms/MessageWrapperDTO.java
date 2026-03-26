package com.cbi.ccr.csw.dto.fms;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapperDTO {
	@NotNull
	private ServiceType type;
	// contains the encrypted JSON of ClientMessageDTO type
	@NotNull
	private String encryptedBody;
	
	@NotNull
	private UUID wrapperKey;
	private UUID fileId;
	private UUID messageId;
	
}
