package com.cbi.ccr.csw.poller.db.bean;

import java.util.LinkedList;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized multi thread queue.
 *  
 *   the thread that call getLast will wait if the list is empty 
 *
 * @param <T>
 */
@Slf4j
public class SyncList<T> {

	private LinkedList<T> list = new LinkedList<>();
	private boolean shutdown = false;
	
	public synchronized T getLast() {
        while (list.isEmpty() && !shutdown) {
        	log.debug("------SyncList waiting...");
            try { 
                wait();
            } catch (InterruptedException e)  {
            	Thread.currentThread().interrupt();
            }
        }
       
        if(!list.isEmpty())
        	return list.removeLast();
        return null;
    }
  
	
	public synchronized void add(T t) {
		list.add(t);
		notifyAll();
	}
	
	public synchronized void shutdown() {
		shutdown = true;
		notifyAll();
	}
	
	public synchronized int size() {
		return list.size();
	}
}
