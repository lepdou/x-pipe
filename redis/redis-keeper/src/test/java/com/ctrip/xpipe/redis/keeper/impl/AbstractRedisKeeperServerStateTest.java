package com.ctrip.xpipe.redis.keeper.impl;

import org.junit.Before;

import com.ctrip.xpipe.redis.core.entity.KeeperMeta;
import com.ctrip.xpipe.redis.core.entity.RedisMeta;
import com.ctrip.xpipe.redis.core.meta.ShardStatus;
import com.ctrip.xpipe.redis.keeper.AbstractRedisKeeperTest;
import com.ctrip.xpipe.redis.keeper.RedisKeeperServer;



/**
 * @author wenchao.meng
 *
 * Jun 12, 2016
 */
public class AbstractRedisKeeperServerStateTest extends AbstractRedisKeeperTest{
	
	protected RedisKeeperServer redisKeeperServer;

	protected RedisMeta redisMasterMeta;
	
	
	@Before
	public void beforeAbstractRedisKeeperServerStateTest() throws Exception{
		
		redisMasterMeta = createRedisMeta();
		redisKeeperServer = createRedisKeeperServer();
	}
	

	
	protected ShardStatus createShardStatus(KeeperMeta activeKeeper, KeeperMeta upstreamKeeper, RedisMeta redisMaster) {
		return new ShardStatus(activeKeeper, upstreamKeeper, redisMaster);
	}
	
}
