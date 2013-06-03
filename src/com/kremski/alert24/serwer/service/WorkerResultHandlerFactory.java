package com.kremski.alert24.serwer.service;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class WorkerResultHandlerFactory {
	private HashMap<String, WorkerResultHandler> keyToHandlerMapping = Maps.newHashMap();
	
	public WorkerResultHandler registerWorkerResultHandler(String key, WorkerResultHandler handler) {
		return keyToHandlerMapping.put(key, handler);
	}
	
	public WorkerResultHandler getWorkerResultHandler(String workerKey) {
		return keyToHandlerMapping.get(workerKey);
	}
}