package com.cbi.ccr.inbound.util;

import java.util.HashMap;
import java.util.Map;

public class LocalCache {
	private static LocalCache instance = new LocalCache();
	
	private Map<String,String> cache;
	
	private LocalCache() {
		
	}
	public static LocalCache getInstance() {
		return instance;
	}
	
	public synchronized void put(String key, String value) {
		if(cache == null)
			cache = new HashMap<>();
		
		cache.put(key, value);
	}
	
	public synchronized String get(String key) {
		if(cache == null) return null;
		return cache.get(key);
	}
	
	public synchronized String remove(String key) {
		if(cache == null) return null;
		return cache.remove(key);
	}
	
	public synchronized int size() {
		if(cache == null) return 0;
		return cache.size();
	}
	public synchronized void clear() {
		if(cache == null) return;
		cache.clear();
	}
}
