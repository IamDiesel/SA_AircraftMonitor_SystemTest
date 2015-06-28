package server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.sun.net.httpserver.HttpServer;

import exception.AdsMessageException;
import exception.AdsMessageFactoryException;
import exception.AircraftException;
import exception.AircraftFactoryException;
import exception.Http2RedisException;
import exception.SixBitASCIIException;

public final class Control {
@SuppressWarnings("deprecation")
public static void main(String[] args) throws IOException
{
	String testFile = null;
	

	
	try{
	
	//AircraftServerSubsriber aircraftServer = new AircraftServerSubsriber(aircraftServerJed,jedisAircraftServer);
	Thread jedisAircraftServerThread = new Thread(new Runnable(){
		@Override
		public void run() {
			Jedis jedisClient = new Jedis("localhost");
			jedisClient.subscribe(new JedisAircraftServer(), "ads.msg.identification", "ads.msg.velocity", "ads.msg.position","exitJedisAircraftServer");
		}});

	

	HttpServer server = HttpServer.create(new InetSocketAddress(3333), 0); 
    server.createContext( "/map.basic", new WebServer.MapBasic());
    server.createContext( "/active.kml", new WebServer.ActiveKML());
    server.setExecutor(null); // create a default executor
    server.start();
    jedisAircraftServerThread.start();
    
    LinkedList<Thread> adsbPublisherList = new LinkedList<Thread>();
    LinkedList<HttpServer> flugmonSimuServerList = new LinkedList<HttpServer>();
    if(args.length < 1)
    	{
    	adsbPublisherList.addLast(new Thread(new A_Http2Redis("http://flugmon-it.hs-esslingen.de/subscribe/ads.sentence")));
    	}
    else	//Test-Mode
    	{
	    	for(int i = 0; i < args.length; i++) //run multiple Test2Redis components in order to receive all data from all input files
	    	{
	    		flugmonSimuServerList.addLast(HttpServer.create(new InetSocketAddress(3333+i+1),0));
	    		flugmonSimuServerList.getLast().createContext("/flugmonSimu", new FlugmonSimu(args[i]));
	    		flugmonSimuServerList.getLast().setExecutor(null);
	    		
	    		adsbPublisherList.addLast(new Thread(new A_Http2Redis("http://localhost:"+String.valueOf(3333+i+1)+"/flugmonSimu")));
	    	}
	    		
    	}
	for(int i = 0; i < args.length; i++) //start threads
	{
		flugmonSimuServerList.get(i).start();
		adsbPublisherList.get(i).start();
	}
	
	boolean inputServerRunning = true;
	int count = 0;
	while(inputServerRunning) //wait until all input servers have terminated
	{
		count = 0;
		for(int i=0; i < adsbPublisherList.size(); i++)
		{
			if(adsbPublisherList.get(0).isAlive() == false) //count webservers that are finished
				count++;	
		}
		if(count == adsbPublisherList.size())
			inputServerRunning = false;
	}
	for(int i = 0; i < flugmonSimuServerList.size(); i++)//End Simulation servers
		flugmonSimuServerList.get(i).stop(1);
	server.stop(1);

	//jedisAircraftServer.unsubscribe();
	//jedisAircraftServerThread.interrupt();
	//while(mainJedThread.isAlive());
	
	
	System.out.println("EOP--!");

	
	}
	catch(JedisConnectionException e)
	{
		JOptionPane.showMessageDialog(new JFrame(), "No Connection to Jedis.");
	}
	catch(AdsMessageException e)
	{
		System.out.println(e.getMessage());
	}
	catch(AdsMessageFactoryException e)
	{
		System.out.println(e.getMessage());
	}
	catch(AircraftException e)
	{
		System.out.println(e.getMessage());
	}
	catch(AircraftFactoryException e)
	{
		System.out.println(e.getMessage());
	}
	catch(SixBitASCIIException e)
	{
		System.out.println(e.getMessage());
	}
	catch(Http2RedisException e)
	{
		//System.out.println(e.getMessage());
		String errorText = e.getMessage();
		if(e.getErrorNumber() == 500)
			errorText += "\n Maybe there is missing a OPEN-VPN Connection to HS-Esslingen";
		JOptionPane.showMessageDialog(new JFrame(), errorText);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
}
}