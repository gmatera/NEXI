package com.cbi.ccr.csw.domain.mss;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface MSSSendDBRepository extends CSWCommonRepositoryDB<MSSSend, Long>{
	
	List<MSSSend> findByStsCode(Integer stsCode);
	
	List<MSSSend> findByStsCodeAndStatus(Integer stsCode, ClientTaskStatus status);
	
	@Query(value = "FROM MSSSend WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<MSSSend> findAllMessagesToProcess();
	
	@Query(value = "select count(s) from MSSSend s where s.baInsertTimestamp >= :from and s.baInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from MSSSend s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
	List<MSSSend> findAllByStatus(MSSSendStatus status);
	
//	@Override
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.status = ?1 where mss.id = ?2")
//	int setStatus(ClientTaskStatus status, Long id);
//	
//	@Override
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.cswStatus = ?1, mss.retryCnt = ?2 where mss.id = ?3")
//	int setStatusAndUpdateCounter(ClientTaskStatus cswStatus, int retryCounter, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.stsCode = ?1, mss.status = ?2 where mss.id = ?3")
//	int updateStatus(Integer stsCode, MSSSendStatus status,  Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.stsCode = ?1, mss.status = ?2, mss.cswStatus = ?3 where mss.id = ?4 and version =?5")
//	int updateAllStatus(Integer stsCode, MSSSendStatus status, ClientTaskStatus cswStatus, Long id, Long version);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.stsCode = ?1, mss.status = ?2, mss.cswStatus = ?3, mss.statusInfo= ?4 where mss.id = ?5 and version =?6")
//	int updateAllStatusWithInfo(Integer stsCode, MSSSendStatus status, ClientTaskStatus cswStatus, String statusInfo, Long id, Long version);
	
//	@Modifying
//	@Query("update FTSSend fts set fts.stsCode = ?1, fts.status = ?2, fts.cswStatus = ?3, fts.sendRequestTimestamp = ?4, fts.sendConfirmedTimestamp = ?4, fts.sendCompletedTimestamp = ?4  where fts.id = ?5")
//	int updateFtsAfterNotify(Integer stsCode, FTSSendStatus status, ClientTaskStatus cswStatus, LocalDateTime sendRequestTimestamp, Long id);
	
	/**
	 * TIMESTAMPS
	 */
	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.stsCode = ?1, mss.status = ?2, mss.cswStatus = ?3, mss.createDate = ?4  where mss.id = ?5")
//	int updateAllStatusWithAcceptTS(Integer stsCode, MSSSendStatus status, ClientTaskStatus cswStatus, String createDate, Long id);
	
	// TODO rivedere questi metodi
	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.firstEasSubTime = ?1 where mss.id = ?2")
//	int updateFirstEasSubTime(String ftimestamp, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.ferSubTime = ?1 where mss.id = ?2")
//	int updateFerSubTime(LocalDateTime ftimestamp, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.sendReqTimestamp = ?1 where mss.id = ?2")
//	int updateSendReqTmp(LocalDateTime ftimestamp, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.fenDelTime = ?1 where mss.id = ?2")
//	int updateFenDelTime(LocalDateTime ftimestamp, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.ferDelTime = ?1 where mss.id = ?2")
//	int updateFerDelTime(LocalDateTime ftimestamp, Long id);
//	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.lastEasSubTime = ?1 where mss.id = ?2")
//	int updateLastEasSubTime(LocalDateTime ftimestamp, Long id);
	
	// fine TODO
	
//	@Modifying
//	@Query("update MSSSend mss set version=version+1, mss.stsCode = ?1, mss.status = ?2, mss.cswStatus = ?3, mss.sendReqTimestamp = ?4, mss.ferDelTime = ?5 where mss.id = ?6 and version =?7")
//	int updateAllStatusWithEndTS(Integer stsCode, MSSSendStatus status, ClientTaskStatus cswStatus, LocalDateTime ftimestamp, String timeStamp, Long id, Long version);

//	@Modifying
//	@Query("update MSSSend mss set mss.stsCode = ?1, mss.status = ?2, mss.cswStatus = ?3, mss.sendReqTimestamp = ?4, mss.ferDelTime = ?4, where mss.id = ?5")
//	int updateMssAfterNotify(int stsCode, MSSSendStatus mssSendStatus, ClientTaskStatus cswStatus, LocalDateTime now, Long messageKey);
	
}
