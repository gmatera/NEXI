package com.cbi.ccr.csw.app.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;

public class LogFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String correlationId = request.getHeader("correlationId");
			
			CswLog.setLogData(CswLogData.builder()
				.system(LogSystemEnum.CSW.name())
				.correlationId(correlationId != null ? correlationId :  UUID.randomUUID().toString())
				.build());
			filterChain.doFilter(request, response);
		}finally {
			CswLog.uset();
		}
		
	}

}
