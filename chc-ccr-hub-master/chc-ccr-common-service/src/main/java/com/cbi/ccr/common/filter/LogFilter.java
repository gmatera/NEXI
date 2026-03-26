package com.cbi.ccr.common.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.logging.CcrLogData;
import com.cbi.ccr.common.logging.LogSystemEnum;

public class LogFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String correlationId = request.getHeader("correlationId");
			CcrLog.setLogData(CcrLogData.builder()
				.system(LogSystemEnum.CCR.name())
				.correlationId(correlationId != null ? correlationId :  UUID.randomUUID().toString())
				.build());
			filterChain.doFilter(request, response);
		}finally {
			CcrLog.uset();
		}
		
	}

}
