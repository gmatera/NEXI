package com.cbi.ccr.csw.domain.fts;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;

public interface FTSSendDBRepository  extends CSWCommonRepositoryDB<FTSSend, Long>{
	
	List<FTSSend> findByStsCode(Integer stsCode);
	
	@Query(value = "select count(s) from FTSSend s where s.fileName = ?1 and s.ftsInterface = ?2")
	long countByFileNameAndFtsInterface(String fileName, RouteInterface interfaceType);
	
	List<FTSSend> findByStsCodeAndStatus(Integer stsCode, ClientTaskStatus status);
	
	@Query(value = "FROM FTSSend WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<FTSSend> findAllMessagesToProcess();
	
	@Query(value = "select count(s) from FTSSend s where s.baInsertTimestamp >= :from and s.baInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from FTSSend s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
	FTSSend findOneByLocalBaIdAndRemoteBaIdAndFtsInterfaceAndFileName(String localBaId, String remoteBaId, RouteInterface ftsInterface, String fileName);
	
	List<FTSSend> findAllByStatus(FTSSendStatus status);
	
//	@Override
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.cswStatus = ?1 where fts.id = ?2")
//	int setStatus(ClientTaskStatus cswStatus, Long id);
//	
//	@Override
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.cswStatus = ?1, fts.retryCnt = ?2 where fts.id = ?3")
//	int setStatusAndUpdateCounter(ClientTaskStatus cswStatus, int retryCounter, Long id);
	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2 where fts.id = ?3 and version =?4")
//	int updateStatus(Integer stsCode, FTSSendStatus status, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3 where fts.id = ?4 and version =?5")
//	int updateAllStatus(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3, fts.statusInfo= ?4 where fts.id = ?5 and version =?6")
//	int updateAllStatusWithInfo(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, String statusInfo, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3, fts.sendRequestTimestamp = ?4, fts.sendConfirmedTimestamp = ?4, fts.sendCompletedTimestamp = ?4  where fts.id = ?5 and version =?6")
//	int updateFtsAfterNotify(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, LocalDateTime sendRequestTimestamp, Long id, Long version);
	
//	/**
//	 * TIMESTAMPS
//	 */
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3, fts.createDate = ?4  where fts.id = ?5 and version= ?6")
//	int updateAllStatusWithAcceptTS(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, String createDate, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.startTime = ?1, fts.sendAcceptedTimestamp = ?2 where fts.id = ?3 and version =?4")
//	int updateStartTimeandSendAcceptedTmp(String time,  LocalDateTime ftimestamp, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.easComplTime = ?1, fts.sendGftRequestTimestamp = ?2 where fts.id = ?3")
//	int updateEasComplTimeandSendGftRequestTmp(LocalDateTime ftimestamp, Long id, Long version);
//	
//	@Modifying
//	@Query("update FTSSend fts set version=version+1, fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3, fts.easComplTime = ?4, fts.sendGftRequestTimestamp = ?5 where fts.id = ?6 and version =?7")
//	int updateAllStatusWithEndTS(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, String timestamp, LocalDateTime ftimestamp, Long id, Long version);
	
}
