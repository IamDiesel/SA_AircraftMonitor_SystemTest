package exception;

import redis.clients.jedis.exceptions.JedisDataException;

public class SixBitASCIIException extends JedisDataException{
	private String sixBitBinary;

	public SixBitASCIIException(int errNo, String errDescription, String sixBitBinary)
	{
		super("SixBitASCIIException No. " + errNo + ": " + errDescription + "\n --Begin sixBitBinary-Code--\n" + sixBitBinary + "\n --Ebd sixBitBinary-Code--" );
		this.sixBitBinary = sixBitBinary;
	}
	public String getSixBitBinaryCode()
	{
		return sixBitBinary;
	}
	
}
