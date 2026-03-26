package com.cbi.ccr.csw.poller.db.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.TransactionException;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.i.CswOutboundEntity;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.poller.db.bean.SyncList;
import com.cbi.ccr.csw.poller.db.repository.CSWCommonPollerRepositoryDB;
import com.cbi.ccr.csw.poller.db.util.InternalPingService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.api.dto.InternalControllerPath;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsNoProxy;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.frw.persistence.service.GenericDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PollerAbstractService<E extends CswOutboundEntity, S extends CSWCommonPollerRepositoryDB<E,Long>> extends AbstractService {

	@Value("#{configuration.outboundWorkers}")
	protected List<String> workers;
	
	@Value("${max_retry_attempts}")
	protected int maxRetryAttempts;
	
	@Value("${outbound_worker_threads}")
	private int outboundWorkerThreads;
	
	@Autowired
	protected InternalPingService internalPingService;
	
	@Autowired
	protected S repository;
	@Autowired
	protected GenericDAO genericDAO;
	
	private Pageable firstNElements;
	
	private SyncList<Long> syncList = new SyncList<>();
	private boolean running = false;
	private List<WorkerEnqueueProcessor> processor;
	
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	@Autowired
	private ApplicationContext appContext;
	
	@Override
	@PostConstruct
	public void init() {
		super.init();
		
		log.info("Initializing workers list...");
		
		if(workers.isEmpty()) {
			log.error("########## Workers  NOT configued, Please configure outbound_workers property ################");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		
		checkAndFixNotSubmitted();
		
		firstNElements = PageRequest.of(0, 50);
		this.setRunning(true);
		
		log.info(Color.y(String.format("Number of configured workers :%s ", workers.size())));
		log.info(Color.y(String.format("Number of thread for each workers :%s ", outboundWorkerThreads)));
		
		processor = new ArrayList<>(workers.size());
		for (String w : workers) {
			
			for (int i = 0; i < outboundWorkerThreads; i++) {
				WorkerEnqueueProcessor p = new WorkerEnqueueProcessor(w);
				processor.add(p);
				p.start();
			}
		}
	}
	
	// check all entities with not submitted status  
	private void checkAndFixNotSubmitted() {
		log.info("Checking not submitted entities...");
		transactionTemplate.executeWithoutResult(t -> {
			List<E> list = repository.findAllNotSubmitted();
			list.forEach(entity -> entity.setCswStatus(null));
		});
		log.info("Checking not submitted entities...DONE");
	}
	
	@PreDestroy
    public void destroy() {
		this.setRunning(false);
		syncList.shutdown(); // unlock
		log.debug("Shutting down Workers...");
		processor.forEach(p -> {
			log.debug("waiting for Thread to complete...{}", p.workerUrl);
			try {
				p.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
    }
	
	protected abstract String getSubmitURI();
	protected abstract String getSubmitPath();
	protected abstract void updateMessageAfterMaxRetry(E message);
	

//	private void generateAuthKey() {
//		Query query = genericDAO.getEntityManager().createQuery("from GlobalConfiguration where property='ENC_PRIVATE_KEY'");
//		GlobalConfiguration cfg = (GlobalConfiguration) query.getSingleResult();
//		String enc = cfg.getValue();
//	}
	
	@Scheduled(fixedDelayString = "${fixed_poller_delay}")
	public void pool() {
		
		if(syncList.size() < 10) {
			// getting the list of waiting files to submit and adding to  the local queue
			
			List<E> list = transactionTemplate.execute(t -> {
				List<E> list2 = getMessagesList();
				list2.forEach( m -> {
					// change status, so next poll will not take 
					if(m.getCswStatus() == null)
						m.setCswStatus(ClientTaskStatus.NEW);
					
					if(m.getCswStatus().equals(ClientTaskStatus.WAITING_FOR_RETRY))
						m.setCswStatus(ClientTaskStatus.NEW);
					
					
				});
				return list2;
			});
			
			if(list != null) {
				list.forEach( m -> {
					syncList.add(m.getId());
					
				});
			}
		}		
		
		log.debug("polling {}", getSubmitPath());
	}
	
	private List<E> getMessagesList() {
		return repository.findAllMessagesToProcess(firstNElements);
	}
	
	
	private class WorkerEnqueueProcessor extends Thread {
		private String workerHost;
		
		private String workerUrlLivenes;
		private String  workerUrl;
		
		public WorkerEnqueueProcessor(String workerHost) {
			this.workerHost = workerHost;
			workerUrlLivenes = String.format("%s%s%s", workerHost, getSubmitURI(), InternalControllerPath.LIVENESS);
			workerUrl = String.format("%s%s%s", workerHost, getSubmitURI(), getSubmitPath());
		}

		@Override
		public void run() {
			
			try {
				// wait for CSW startup, to avoid initial error message 
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			log.info(Color.g(String.format("Worker processor started for service:%s ", workerUrl)));
			
			while(isRunning()) {
				
				try {
					sendMessage();
					
				} catch (Exception e1) {
					log.error("Poller error sending ", e1);
				}
			}
			
			log.info(Color.g(String.format("Worker processor shutdown DONE for service:%s ", workerUrl)));
		}
		
		private void updateMessageAfterMaxRetryInternal(E message) {
			updateMessageAfterMaxRetry(message);
			message.setCswStatus(ClientTaskStatus.FAILED);
			message.setComplete(1);
//			transactionTemplate.executeWithoutResult(t -> {
//				message.setCswStatus(ClientTaskStatus.FAILED);
//				//message.setStatusInfo("Max attemps"); // original message must be keept
//				repository.save(message);
//			});
		}
		
		private void sendMessage() {
			// getting last message from the local queue
			Long messageId = syncList.getLast();
			
			if(messageId == null) 
				return;
			
			Optional<E> messaageOpt = repository.findById(messageId);
			if(!messaageOpt.isPresent())
				return;
			
			E message = messaageOpt.get();
			
			CswLog.getLogData().setId(messageId.toString());
			
			if(!isOutboundLive()) {
				// re queue the message, so it will get processed by another thread.
				requeue(message);
				return;
			}
			if(!checkRetriesAndPrepare(message)) {
				return;
			}
			
			try {
				log.debug("sending {} id:{}", getSubmitPath(), message.getId());
			
				httpUtilsNoProxy.postRequestWithBody(workerUrl, null,
						new SubmitDTO(message.getId()), Void.class, 20000);
			
			} catch (ChcStubException e) {
				
				// non deve farlo perchè se cade la connessione non è detto che il file 
				// non sia in stato sending
//				transactionTemplate.executeWithoutResult(t -> {
//					Optional<E> msgOpt = repository.findById(message.getId());
//					
//					if(msgOpt.isPresent()) {
//						
//						// an error occurred while sending the entity, but the status is still SENDING
//						// so changing the status to null, the scheduler will take it again
//						if(msgOpt.get().getCswStatus() == ClientTaskStatus.SENDING) 
//							msgOpt.get().setCswStatus(null);
//						else if(msgOpt.get().getCswStatus() == ClientTaskStatus.RETRYING) 
//							msgOpt.get().setCswStatus(ClientTaskStatus.RETRYING);
//					}
//					
//
//				});
				
				CswLog.error(log, String.format("Error occurred while sending to outbound controller. code:%s err:%s", e.getError().getErrorCode(), e.getError().getLocalizedMessage()));
			} 
		}
		
		private boolean isOutboundLive() {
			try {
				// check if the worker is alive
				internalPingService.pingService(workerUrlLivenes);
				return true;
			} catch (ChcStubException e1) {
				log.error(Color.r(String.format("Worker %s seems not active, please check!!, error is:%s", workerHost, e1.toString())));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			return false;
		}
		
		/**
		 * 
		 * @param message
		 * @return true to go ahead
		 */
		private boolean checkRetriesAndPrepare(E message) {
			
			
			try {
				
				return transactionTemplate.execute(t -> {
					
					if(message.getRetryCounter() == null) {
						message.setRetryCounter(0);
					}
					
					if(message.getRetryCounter() >= maxRetryAttempts) {
						updateMessageAfterMaxRetryInternal(message);
						repository.save(message);
						return false;
					} else {
						message.setRetryCounter(message.getRetryCounter() + 1);
					}
					// lo stato ClientTaskStatus.SENDING viene impostato subito nei primo passi del worker
					// in questo modo si evita la gestioen di timeout ed errori di connessione
					// con il worker.
					// assume stato ClientTaskStatus.SENDING solo quando il worker lo ha preso in carico
					repository.save(message);
					return true;
				});
				
			} catch (TransactionException e) {
				log.error("Error occurred whille updating entity type:{} id:{}", message.getClass().getSimpleName(), message.getId());
				// if a database error occur, the message must be re-queued until the database will come alive again
				requeue(message);
				return false;
			}
		}
		
		private void requeue(E message) {
			syncList.add(message.getId());
		}
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
}
