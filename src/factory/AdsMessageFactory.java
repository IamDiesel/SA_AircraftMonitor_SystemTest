package factory;





import java.math.BigInteger;

import domain.AdsMessage;
import domain.AirborneIdentificationMessage;
import domain.AirbornePositionMessage;
import domain.AirborneVelocityMessage;
import exception.AdsMessageException;
import exception.AdsMessageFactoryException;

/*
 * This class creates Messages and holds Methods to parse the Messages
 */
public final class AdsMessageFactory 
{
	private static AdsMessageFactory instance;
	private AdsMessageFactory(){};
	public static AdsMessageFactory getInstance()
	{
		if(instance == null)
			instance = new AdsMessageFactory();
		return instance;
	}
	public AdsMessage sentence2Message(String message) throws AdsMessageException
	{
		//sentence = sentence.substring(0, sentence.indexOf('{')+1) + sentence.substring(sentence.indexOf(',')+1, sentence.indexOf('}')+1);
		String input [] = message.split("\"subscribe\":");
		String serverIdentifier = "";
		String sentence = "";
		if(input.length < 1)
			throw new AdsMessageFactoryException(204, "Searching for identifier \"subscribe:\" in message failed. Given message instead of sentence",message,"","",-1,-1,-1,serverIdentifier);
		else
		{
			switch(input.length)
			{
			case 1: sentence = message; serverIdentifier = "empty";break;
			case 2: sentence = "{\"subscribe\":"+input[1]; serverIdentifier = message.substring(message.indexOf("\""), message.indexOf(",\"subscribe\":")); break;
			default: break;
			}
		}
		System.out.println("MSG:"+sentence);
		System.out.println("SRV:"+serverIdentifier);
			
		if(sentence == null || sentence.length() == 0)
			throw new AdsMessageFactoryException(200, "Sentence not available at sentece2Message()-conversion",sentence,"","",-1,-1,-1,serverIdentifier);
		if(!(sentence.length() == 101 || sentence.length() == 99)  )//99?
			throw new AdsMessageFactoryException(203, "Sentence has wrong length (should be 99) at sentece2Message()-conversion. Length = "+sentence.length(),sentence,"","",-1,-1,-1,serverIdentifier);
		if(sentence.indexOf('*') == -1 || sentence.indexOf(';') == -1)
			throw new AdsMessageFactoryException(201, "JSON Start identifier (*) or JSON End Identifier (;) not available in sentence at sentece2Message()-conversion",sentence,"","",-1,-1,-1,serverIdentifier);
		if(sentence.indexOf('*') >= sentence.indexOf(';'))
			throw new AdsMessageFactoryException(202, "JSON Start identifier (*) index is greater than JSON End Identifier (;) index in sentence at sentece2Message()-conversion",sentence,"","",-1,-1,-1,serverIdentifier);
		
		String payload = sentence.substring(sentence.indexOf('*'),sentence.indexOf(';'));
		
		if(sentence.indexOf(';') - sentence.indexOf('*') != 29) //TODO: Double Check Me!
			throw new AdsMessageFactoryException(203, "Wrong Payload length in sentence at sentece2Message()-conversion. Distance from end-Identifier (;) to start-identifier should be 29, but is: " + String.valueOf(sentence.indexOf(';') - sentence.indexOf('*')) ,sentence,payload,"",-1,-1,-1,serverIdentifier);

		
		String binarySentence = (String.format("%112s",(new BigInteger(payload.substring(1),16) ).toString(2))).replace(' ','0');
		
		//Decoding of Message(Sub-)Type and so on
		int messageType = Integer.parseInt(binarySentence.substring(32,37),2);
		int originator = Integer.parseInt(binarySentence.substring(8,32),2);
		long time = Long.valueOf(  (sentence.substring(sentence.lastIndexOf(',')+2,sentence.lastIndexOf('.')+4)).replace(".", "") );
		/*System.out.println("payload: "+payload);
		System.out.println("binarySentence:"+binarySentence);
		System.out.println("messageType: "+messageType);
		System.out.println("originator: "+originator);
		System.out.println("time: "+time);*/
		
		//Switching the messageTypes in order to deliver the correct MessageType-Objects
		switch(messageType)
		{
		case 1: case 2: case 3: case 4: //Aircraft Identification Message
				return new AirborneIdentificationMessage(binarySentence,messageType,originator,time,serverIdentifier);
	
		case 9: //9-18, 20-22 Airborne Position Message
		case 10:case 11: case 12:case 13:case 14:case 15:case 16:case 17:case 18: case 20:case 21:
		case 22: 
			AirbornePositionMessage msg = new AirbornePositionMessage(binarySentence,messageType,originator,time,serverIdentifier);
			//System.out.println("Position: "+msg);
			return msg;
			//return new AirbornePositionMessage(binarySentence,messageType,originator,time);
		
		case 19: return new AirborneVelocityMessage(binarySentence, messageType, originator, time,serverIdentifier);
		default: return new AdsMessage(binarySentence,messageType,originator,time,serverIdentifier);
		}
	}
}
//All static decoding methods here
