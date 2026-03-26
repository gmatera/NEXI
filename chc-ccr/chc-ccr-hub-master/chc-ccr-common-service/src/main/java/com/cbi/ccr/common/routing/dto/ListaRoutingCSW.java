package com.cbi.ccr.common.routing.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListaRoutingCSW {
	private ResultDetails resultDetails;
	private List<RoutingClient> routingClients;
}
