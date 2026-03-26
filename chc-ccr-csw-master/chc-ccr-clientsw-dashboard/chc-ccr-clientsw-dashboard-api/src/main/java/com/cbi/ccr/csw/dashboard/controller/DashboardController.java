package com.cbi.ccr.csw.dashboard.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.dto.ChartNameValueDTO;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.fms.FMSRecv;
import com.cbi.ccr.csw.domain.fms.FMSRecvDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSRecvStatus;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.domain.fts.FTSRecv;
import com.cbi.ccr.csw.domain.fts.FTSRecvDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSRecvStatus;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.domain.i.CswEntity;
import com.cbi.ccr.csw.domain.i.CswInboundEntity;
import com.cbi.ccr.csw.domain.mss.MSSRecv;
import com.cbi.ccr.csw.domain.mss.MSSRecvDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSRecvStatus;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQRepository;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQRepository;
import com.cbi.ccr.csw.mq.domain.i.CswEntityMq;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQRepository;

@RestController
@RequestMapping(ControllerPath.DASH)
public class DashboardController {

	@Autowired
	private FMSSendDBRepository fmsSendDBRepository;
	@Autowired
	private FTSSendDBRepository ftsSendDBRepository;
	@Autowired
	private MSSSendDBRepository mssSendDBRepository;

	@Autowired
	private FMSRecvDBRepository fmsRecvDBRepository;
	@Autowired
	private FTSRecvDBRepository ftsRecvDBRepository;
	@Autowired
	private MSSRecvDBRepository mssRecvDBRepository;

	@Autowired
	private FMSSendMQRepository fmsSendMQRepository;

	@Autowired
	private FTSSendMQRepository ftsSendMQRepository;

	@Autowired
	private MSSSendMQRepository mssSendMQRepository;

	@Autowired
	private FMSRecvMQRepository fmsRecvMQRepository;

	@Autowired
	private FTSRecvMQRepository ftsRecvMQRepository;

	@Autowired
	private MSSRecvMQRepository mssRecvMQRepository;
	
	private @Autowired EntityManager entityManager;

	private static final String LABEL_FMS = "FMS";
	private static final String LABEL_FTS = "FTS";
	private static final String LABEL_MSS = "MSS";

	///////////////////////////// DB DATA

	@GetMapping(ControllerPath.DB + ControllerPath.CHART_MESSAGE_BY_MONTH_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> getMonthlyDBMessagesOutbound() {
		
		Long countFms = fmsSendDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countFts = ftsSendDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countMss = mssSendDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));

		return Arrays.asList(new ChartNameValueDTO(LABEL_FMS, countFms), new ChartNameValueDTO(LABEL_FTS, countFts),
				new ChartNameValueDTO(LABEL_MSS, countMss));
	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_MESSAGE_BY_MONTH_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> getMonthlyDBMessagesInbound() {

		Long countFms = fmsRecvDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countFts = ftsRecvDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countMss = mssRecvDBRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));

		return Arrays.asList(new ChartNameValueDTO(LABEL_FMS, countFms), new ChartNameValueDTO(LABEL_FTS, countFts),
				new ChartNameValueDTO(LABEL_MSS, countMss));
	}
	
	private <T extends CswEntity> long countOutboundDBByStatus(Class<T> entityClass, Enum<?> status) {
		Query q = entityManager.createQuery(String.format("select count(s) from %s s where status=?1 and s.baInsertTimestamp >=?2 and s.baInsertTimestamp <=?3", entityClass.getSimpleName()));
		q.setParameter(1, status);
		q.setParameter(2, LocalDateTime.now().minusMonths(1));
		q.setParameter(3, LocalDateTime.now().plusDays(1));
		
		return (Long) q.getSingleResult();
	}
	
	private <T extends CswEntity> long countInboundDBByStatus(Class<T> entityClass, Enum<?> status) {
		Query q = entityManager.createQuery(String.format("select count(s) from %s s where status=?1 and s.receiveTimestamp >=?2 and s.receiveTimestamp <=?3", entityClass.getSimpleName()));
		q.setParameter(1, status);
		q.setParameter(2, LocalDateTime.now().minusMonths(1));
		q.setParameter(3, LocalDateTime.now().plusDays(1));
		
		return (Long) q.getSingleResult();
	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_FMS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBFMSOutbound() {

		FMSSendStatus[] statuses = FMSSendStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countOutboundDBByStatus(FMSSend.class, statuses[i])));		
		}
		return values;
	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_FTS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBFTSOutbound() {

		FTSSendStatus[] statuses = FTSSendStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countOutboundDBByStatus(FTSSend.class, statuses[i])));		
		}
		return values;
	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_MSS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBMSSOutbound() {

		MSSSendStatus[] statuses = MSSSendStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countOutboundDBByStatus(MSSSend.class, statuses[i])));		
		}
		return values;
	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_FMS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBFMSInbound() {

		FMSRecvStatus[] statuses = FMSRecvStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countInboundDBByStatus(FMSRecv.class, statuses[i])));		
		}
		return values;

	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_FTS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBFTSInbound() {

		FTSRecvStatus[] statuses = FTSRecvStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countInboundDBByStatus(FTSRecv.class, statuses[i])));		
		}
		return values;

	}

	@GetMapping(ControllerPath.DB +ControllerPath.CHART_COUNT_MSS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countDBMSSInbound() {

		MSSRecvStatus[] statuses = MSSRecvStatus.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countInboundDBByStatus(MSSRecv.class, statuses[i])));		
		}
		return values;

	}

/////////////////////////////MQ DATA

	@GetMapping(ControllerPath.MQ +ControllerPath.CHART_MESSAGE_BY_MONTH_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> getMonthlyMessagesOutbound() {

		Long countFms = fmsSendMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countFts = ftsSendMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countMss = mssSendMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));

		return Arrays.asList(new ChartNameValueDTO(LABEL_FMS, countFms), new ChartNameValueDTO(LABEL_FTS, countFts),
				new ChartNameValueDTO(LABEL_MSS, countMss));
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_MESSAGE_BY_MONTH_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> getMonthlyMessagesInbound() {

		Long countFms = fmsRecvMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countFts = ftsRecvMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));
		Long countMss = mssRecvMQRepository.countMonthly(LocalDateTime.now().minusMonths(1),LocalDateTime.now().plusDays(1));

		return Arrays.asList(new ChartNameValueDTO(LABEL_FMS, countFms), new ChartNameValueDTO(LABEL_FTS, countFts),
				new ChartNameValueDTO(LABEL_MSS, countMss));
	}

	private <T extends CswEntity> long countMQByStatus(Class<T> entityClass, Enum<?> status) {
		// and s.baInsertTimestamp >=?2 and s.baInsertTimestamp <=?3
		Query q = entityManager.createQuery(String.format("select count(s) from %s s where status=?1 and s.cswInsertTimestamp >=?2 and s.cswInsertTimestamp <=?3 ", entityClass.getSimpleName()));
		q.setParameter(1, status);
		q.setParameter(2, LocalDateTime.now().minusMonths(1));
		q.setParameter(3, LocalDateTime.now().plusDays(1));
		return (Long) q.getSingleResult();
	}
	
	// OUTBOUND
	private <E extends CswEntityMq> List<ChartNameValueDTO> countMQOutbound(Class<E> entityClass) {
		SendStatusMQ[] statuses = SendStatusMQ.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countMQByStatus(entityClass, statuses[i])));		
		}
		return values;
	}
	
	// INBOUND
	private <E extends CswInboundEntity> List<ChartNameValueDTO> countMQInbound(Class<E> entityClass) {
		
		RecvStatusMQ[] statuses = RecvStatusMQ.values();
		List<ChartNameValueDTO> values = new ArrayList<>(statuses.length);
		
		for (int i = 0; i < statuses.length; i++) {
			values.add(new ChartNameValueDTO(statuses[i].name(), countMQByStatus(entityClass, statuses[i])));		
		}
		return values;
	}
	
	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_FMS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countFMSOutbound() {
		return countMQOutbound(FMSSendMQ.class);
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_FTS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countFTSOutbound() {
		return countMQOutbound(FTSSendMQ.class);
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_MSS_OUTBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countMSSOutbound() {
		return countMQOutbound(MSSSendMQ.class);
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_FMS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countFMSInbound() {
		return countMQInbound(FMSRecvMQ.class);
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_FTS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countFTSInbound() {
		return countMQInbound(FTSRecvMQ.class);
	}

	@GetMapping(ControllerPath.MQ + ControllerPath.CHART_COUNT_MSS_INBOUND)
	@PreAuthorizeRoleUser
	public List<ChartNameValueDTO> countMSSInbound() {
		return countMQInbound(MSSRecvMQ.class);
	}

}