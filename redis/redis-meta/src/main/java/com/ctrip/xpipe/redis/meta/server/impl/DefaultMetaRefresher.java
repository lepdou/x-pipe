package com.ctrip.xpipe.redis.meta.server.impl;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.xpipe.api.lifecycle.TopElement;
import com.ctrip.xpipe.lifecycle.AbstractLifecycle;
import com.ctrip.xpipe.redis.core.entity.XpipeMeta;
import com.ctrip.xpipe.redis.core.meta.impl.DefaultMetaOperation;
import com.ctrip.xpipe.redis.meta.server.MetaRefresher;
import com.ctrip.xpipe.redis.meta.server.config.MetaServerConfig;
import com.ctrip.xpipe.rest.RestRequestClient;
import com.ctrip.xpipe.utils.XpipeThreadFactory;
import com.ctrip.xpipe.zk.ZkClient;


/**
 * @author wenchao.meng
 *
 * Jun 25, 2016
 */
@Component
public class DefaultMetaRefresher extends AbstractLifecycle implements MetaRefresher, Runnable, TopElement{
	
	
	@Autowired
	private MetaServerConfig metaServerConfig;
	
	@Autowired
	private ZkClient zkClient;
	
	private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1, XpipeThreadFactory.create("Meta-Refresher"));
	private ScheduledFuture<?> future;
	
	protected void doStart() throws Exception {
		
		future = scheduled.scheduleAtFixedRate(this, 0, metaServerConfig.getMetaRefreshMilli(), TimeUnit.MILLISECONDS);
		
	};
	
	@Override
	public void update() throws Exception {
		//TODO if not modified, not update
		String address = String.format("%s/api/v1/xpipe", metaServerConfig.getConsoleAddress());
		XpipeMeta result = RestRequestClient.get(address, XpipeMeta.class);
		logger.debug("[update]");
		new DefaultMetaOperation(zkClient.get()).update(result);
	}

	@Override
	public void run() {
		try{
			update();
		}catch(ProcessingException e){
			
			if(e.getCause() instanceof IOException){
				logger.error("[run]" + e.getMessage());
			}else{
				logger.error("[run]", e);
			}
		} catch (Exception e) {
			logger.error("[run]", e);
		}
	}
	
	@Override
	protected void doStop() throws Exception {
		future.cancel(true);
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
