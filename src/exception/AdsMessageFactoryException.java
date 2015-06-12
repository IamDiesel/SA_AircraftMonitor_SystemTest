package exception;

import redis.clients.jedis.exceptions.JedisDataException;

public class AdsMessageFactoryException extends JedisDataException{
	private String sentence, payload, binarySentence;
	int messageType, originator;
	long time;

	public AdsMessageFactoryException(int errNo, String errDescription, String sentence, String payload, String binarySentence, int messageType, int originator, long time, String serverIdentifier) {
		super("AdsMessageFactoryException No. "+errNo + ": " + errDescription + "--Begin Err Data--"+ 
				"\nSentence: " + sentence + "\nPayload: " + payload + "\nbinarySentence: " + binarySentence + "\n@Server: "+serverIdentifier+"\n"
				+"--End Err Data--");
		this.sentence = sentence;
		this.payload = payload;
		this.binarySentence = binarySentence;
		this.messageType = messageType;
		this.originator = originator;
		this.time = time;
	}

	public String getSentence(){return sentence;}
	public String getPayload(){return payload;}
	public String getBinarySentence(){return binarySentence;}
	public int getMessageType(){return messageType;}
	public int getOriginator(){return originator;}
	public long getTimeStamp(){return time;}
	

}
