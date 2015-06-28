package server;



// jedis import
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;
import domain.AdsMessage;
import domain.AirborneIdentificationMessage;
import domain.AirbornePositionMessage;
import domain.AirborneVelocityMessage;
import exception.AdsMessageFactoryException;
import exception.Http2RedisException;
import factory.AdsMessageFactory;

public final class A_Http2Redis implements Runnable {
    
    // url for reading the ads sentences
    private String url2 = "http://flugmon-it.hs-esslingen.de/subscribe/ads.sentence";
    private final AdsMessageFactory msgFactory = AdsMessageFactory.getInstance();
    private final String objIdentifier;
    private static int objCnt = 0;
	// redis instance
    private Jedis jedisClient;
    
    public A_Http2Redis(String serverURL)
    {
    	objCnt++;
    	objIdentifier = "A_Http2Redis#["+ objCnt + "]";
    	this.url2 = serverURL;
    }

    
    // server task
    @Override 
    public void run () 
    {
	    byte[] buffer = new byte[512];
	    URLConnection con = null;
	    String message = null;
	    String serverID = null;
	    AdsMessage msg = null;

		try {
			
		    // make connection with the flugmon-it web server
		    URL url = new URL(url2);
			con = url.openConnection();
			jedisClient = new Jedis("localhost");
			
			/*PrintWriter writer = new PrintWriter("./input.txt");
			int i = 0;*/
			
		   
	
		    // open input stream for reading data
			
			int bytesRead=0;
			boolean flag = true;
			int cnt = 0;
			BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
			int dataToReceive = con.getContentLength();
		    // we are a server, i.e. run forever
		    while (dataToReceive > 0 && flag) 
		    {
		    	bytesRead = bis.read(buffer);
		    	if(bytesRead < 1)
		    		flag = false;
		    	dataToReceive -= bytesRead;
		    	
		    	//System.out.println(con.getContentLength()+"bytesRead"+bytesRead);
		    	
		    	
		    	String [] messages = null;

		    	if(bytesRead > 0)
		    	{
		    		
		    		 messages = new String(buffer,0,bytesRead).split("}");//In test mode multiple messages are sent at once
		    	}
		    	for(int i = 0; i < messages.length; i++)
		    	{
		    		message = messages[i] + "}";
					if(bytesRead > 0)
					{
						cnt = 0;
						
						//System.out.println("message::::["+message+ "]bytesRead:"+bytesRead+" contentLength"+con.getContentLength());
						serverID = message.substring(message.indexOf('"')+1, message.indexOf(',')-1);
						//System.out.println(serverID);
						//message = message.substring(0, message.indexOf('{')+1) + message.substring(message.indexOf(',')+1, message.indexOf('}')+1);
					}
	
					//&& "{\"subscribe\":[\"message\",\"ads.sentence\"".equals(message.substring(0, 38)) 
					if(bytesRead > 0  && message.indexOf('!') > 0) //{"subscribe":["message","ads.sentence"....!ADS-B*...  <--Strings from Flugmon server look like this
					{
						String input [] = message.split("\"subscribe\":");
						String serverIdentifier = "";
						String sentence = "";
						if(input.length < 1)
							throw new Http2RedisException(503, "Searching for identifier \"subscribe:\" in message failed.");
						else
						{
							switch(input.length)
							{
							case 1: sentence = message; serverIdentifier = "empty";break;
							case 2: serverIdentifier = message.substring(message.indexOf("\""), message.indexOf(",\"subscribe\":"));
									String thisIdentifier = "\""+objIdentifier+"\",";
									message = "{"+serverIdentifier+","+thisIdentifier+"\"subscribe\":"+input[1]; break;
							default: break;
							}
						}
						
						msg = msgFactory.sentence2Message(message);
						if(msg != null)
						{
							publish(msg);
						}
					}
		    	}
				/*else if(!message.equals("{\"subscribe\":[\"subscribe\",\"ads.sentence\",1]}"))
				{
					throw new Http2RedisException(501, "String received from Flugmon werbserver does not match the pattern. Message is checked from pos 0 to 38 and must be equal to :\n{\"subscribe\":[\"message\",\"ads.sentence\".",buffer,con,message);
				}*/
				   message = "";	//clear data
				  buffer = message.getBytes();
		    } // while
		    jedisClient.publish("exitJedisAircraftServer","exitJedisAircraftServer");	//Call end of program

		} 
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(new JFrame(),  "No. 500: Unknown Error while trying to open connection or while trying to read data from Flugmon-server.\nMaybe no OpenVPN-Exception (HS-ESSLINGEN) is established.");
			//throw new Http2RedisException(500, "Unknown Error while trying to open connection or while trying to read data from Flugmon-server.");
		}
    } // @Override public void run()
    
    private void publish(AdsMessage msg)
    {
    	switch(msg.getMessageTypeD())
		{
		case 1: case 2: case 3: case 4: //Aircraft Identification Message
				jedisClient.publish("ads.msg.identification", ((AirborneIdentificationMessage)msg).toJedisString());
				break;
		case 9: //9-18, 20-22 Airborne Position Message
		case 10:case 11: case 12:case 13:case 14:case 15:case 16:case 17:case 18: case 20:case 21:
		case 22: jedisClient.publish("ads.msg.position", ((AirbornePositionMessage)msg).toJedisString());
				 break;
		case 19: jedisClient.publish("ads.msg.velocity", ((AirborneVelocityMessage)msg).toJedisString());
				 break;
		case 0: case 5: case 6: case 7: case 8: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30: case 31: //Message Types from 0 to 31 are allowed. Those listed here are reserverd and not handled in this program.
				System.err.println("Unhandeled Message-Type@A_Http2Redis. Type: "+ msg.getMessageTypeD());break;
		default:  throw new Http2RedisException(502, "Unknown Message type received from server. Message Type exeeded the given range (0-31): "+msg.getMessageTypeD());
		}
    }
}