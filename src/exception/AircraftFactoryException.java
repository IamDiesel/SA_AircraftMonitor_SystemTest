package exception;

import redis.clients.jedis.exceptions.JedisDataException;

public class AircraftFactoryException extends JedisDataException{
	private String msg;
	public AircraftFactoryException(int errNo, String errDescription,String msg)
	{
		super("AircraftFactoryException No. " + errNo + ": " + errDescription + "\n + msg: "+ msg);
		this.msg = msg;
	}
	public String getMsg()
	{
		return msg;
	}

}
