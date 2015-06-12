package server;

import redis.clients.jedis.Jedis;

public class AircraftServerSubsriber implements Runnable{
	private Jedis jedisClient;
	private JedisAircraftServer myAircraftServer;
	public AircraftServerSubsriber(Jedis jedisClient, JedisAircraftServer server)
	{
		this.jedisClient = jedisClient;
		 myAircraftServer = server;
	}
	@Override
	public void run() {
		jedisClient.subscribe(myAircraftServer, "ads.msg.identification", "ads.msg.velocity", "ads.msg.position","exit");	
	}

}
