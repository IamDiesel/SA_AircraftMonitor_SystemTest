package exception;

import redis.clients.jedis.exceptions.JedisDataException;
import domain.AdsMessage;


public class AdsMessageException extends JedisDataException
{
	private AdsMessage adsMessage;
	
	public AdsMessageException(int errNo, String errText)
	{
		super("AdsMessageException No"+ errNo +": " + errText);

	}
	
}
