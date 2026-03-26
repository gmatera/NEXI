package com.cbi.ccr.csw.dto.api.gateway;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiGatewayResponseDTO {
	 public ArrayList<Key> keys;
}
