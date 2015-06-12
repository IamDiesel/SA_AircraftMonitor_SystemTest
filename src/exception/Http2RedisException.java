package exception;

import java.net.URLConnection;

import domain.AdsMessage;
import redis.clients.jedis.exceptions.JedisDataException;

public class Http2RedisException extends JedisDataException{
	private byte[] buffer;
	private URLConnection con;
	private String message;
	private int errNo;
	public Http2RedisException(int errNo, String errDescription, byte [] buffer, URLConnection con, String message, String serverIdentifier)
	{
		super("Http2RedisException No. " + errNo + ": " + errDescription + "\n --Begin Buffer--\n" + buffer.toString() + "\n --end Buffer--\n --Begin URL--\n"+con.getURL()+"\n--End URL--\n--Begin Message--\n"+message+"\n--End Message--"
				+ "@Server:"+serverIdentifier);
		this.buffer = buffer;
		this.con = con;
		this.message = message;
		this.errNo = errNo;
	}
	public Http2RedisException(int errNo, String errDescription)
	{
		super("Http2RedisException No. " + errNo + ": "+errDescription);
		this.buffer = null;
		this.con = null;
		this.message = null;
	}
	public byte[] getBuffer(){return buffer;}
	public URLConnection getURLConnection(){return con;}
	public String getMessageString(){return message;}
	public int getErrorNumber(){return errNo;}


}
