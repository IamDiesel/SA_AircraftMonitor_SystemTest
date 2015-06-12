package domain;




import java.sql.Timestamp;
import exception.AdsMessageException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 Base class for all subTypes of Ads-B Messages
 */
public class AdsMessage {
private final int 		messageTypeD, //contains MessageType in decimal format
						originatorD;//Sender of the message in decimal format
protected final Timestamp tStamp; //contains time of the message
protected final String binarySentence;

public AdsMessage(String binarySentence, int messageTypeD, int originatorD, long time) throws AdsMessageException
{
	if(binarySentence.length() != 112)//14 bytes
		throw new AdsMessageException(0,"Binary Sentence mismatched size. Sentence size must be 112 bit (binarySentence size =" + binarySentence.length() + ").");
	
	this.binarySentence = binarySentence.substring(32); //from index 32 --> end
	this.messageTypeD=messageTypeD;
	this.originatorD=originatorD;
	this.tStamp= new Timestamp(time);
}
public AdsMessage(String jedisString) throws AdsMessageException
{
	if(jedisString == null || jedisString.length() == 0)
		throw new AdsMessageException(1,"Jedis String not available at ctor.");
		
	
	String entry [] = jedisString.split(";");
	
	if(entry.length < 4)
		throw new AdsMessageException(2,"Jedis String does not conain enough arguments at ctor (entrySize: "+entry.length+",/4");
	
	this.messageTypeD = Integer.parseInt(entry[0]);
	this.originatorD = Integer.parseInt(entry[1]);
	this.binarySentence = entry[2];
	this.tStamp= new Timestamp(Long.parseLong(entry[3]));
}

public int getMessageTypeD()
{
	return messageTypeD;
}
public int getOriginatorD()
{
	return originatorD;
}
public Timestamp getTimeStamp()
{
	return tStamp;
}
public String toString()
{
	DateFormat simpleDate = new SimpleDateFormat("EEEE', 'dd.MM.YYYY', 'H:mm:ss.S");
	//return "Originator:"+originatorD;
	return	"messageTypeD: " +getMessageTypeD()+
			", originatorD: " +getOriginatorD()+
			", time: "+simpleDate.format(tStamp)+
			", binarySentence: " + binarySentence;
	/* toString() hinzugefuegt - glkeit00 */
}
public String toJedisString()
{
	return messageTypeD+";"+originatorD+";"+binarySentence+";"+tStamp.getTime();

}

}
