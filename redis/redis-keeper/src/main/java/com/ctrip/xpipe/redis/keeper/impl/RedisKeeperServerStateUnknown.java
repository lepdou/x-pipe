package com.ctrip.xpipe.redis.keeper.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.ctrip.xpipe.redis.core.meta.KeeperState;
import com.ctrip.xpipe.redis.keeper.RedisClient;
import com.ctrip.xpipe.redis.keeper.RedisKeeperServer;
import com.ctrip.xpipe.redis.keeper.RedisKeeperServer.PROMOTION_STATE;

/**
 * @author wenchao.meng
 *
 * Jun 8, 2016
 */
public class RedisKeeperServerStateUnknown extends AbstractRedisKeeperServerState{

	public RedisKeeperServerStateUnknown(RedisKeeperServer redisKeeperServer) {
		super(redisKeeperServer);
	}



	@Override
	public void becomeBackup(InetSocketAddress masterAddress) {
		
		redisKeeperServer.setRedisKeeperServerState(new RedisKeeperServerStateBackup(redisKeeperServer, masterAddress));
		reconnectMaster();
	}

	@Override
	public void becomeActive(InetSocketAddress masterAddress) {
		
		redisKeeperServer.setRedisKeeperServerState(new RedisKeeperServerStateActive(redisKeeperServer, masterAddress));
		reconnectMaster();
	}

	@Override
	protected void keeperMasterChanged() {
		//nothing to do
		logger.info("[keeperMasterChanged][nothing to do]");
	}

	@Override
	public void setPromotionState(PROMOTION_STATE promotionState, Object promitionInfo) {
		throw new IllegalStateException("state unknown, promotion unsupported!");
	}

	@Override
	public boolean psync(RedisClient redisClient, String[] args) {
		
		logger.error("[psync][server state unknown, close connection.]" + redisClient + "," + this);
		try {
			redisClient.close();
		} catch (IOException e) {
			logger.error("[doHandle][close redisClient]" + redisClient, e);
		}
		
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public KeeperState keeperState() {
		return KeeperState.UNKNOWN;
	}

}
