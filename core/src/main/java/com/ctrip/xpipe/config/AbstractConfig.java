package com.ctrip.xpipe.config;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.xpipe.api.config.Config;
import com.ctrip.xpipe.api.config.ConfigChangeListener;

/**
 * @author wenchao.meng
 *
 * Jul 21, 2016
 */
public abstract class AbstractConfig implements Config{
	
	private List<ConfigChangeListener> listeners = new LinkedList<>();
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
		
		synchronized (listeners){
			listeners.add(configChangeListener);
		}
	}
	
	@Override
	public synchronized void removeConfigChangeListener(ConfigChangeListener configChangeListener) {
		synchronized (listeners){
			listeners.remove(configChangeListener);
		}
	}

	protected void notifyConfigChange(String key, String oldValue, String newValue){
		
		Object[] listenersCopy = null;
		
		synchronized (listeners){
			listenersCopy = listeners.toArray();
		}
		
		for(Object listenerCopy : listenersCopy){
			
			try{
				ConfigChangeListener listener = (ConfigChangeListener) listenerCopy;
				listener.onChange(key, oldValue, newValue);
			}catch(Throwable th){
				logger.error("[notifyConfigChange]{}:{}->{},{}", key, oldValue, newValue, listenerCopy, th);
			}
		}
	}
}
