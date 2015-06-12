package domain;
import exception.AdsMessageException;
import exception.SixBitASCIIException;
import translator.SixBitASCII;



public final class AirborneIdentificationMessage extends AdsMessage{
	public AirborneIdentificationMessage(String binarySentence, int messageTypeD, int originatorD, long time) throws AdsMessageException
	{
		super(binarySentence,messageTypeD,originatorD,time);
	}
	public String getAircraftID() throws SixBitASCIIException
	{
		return SixBitASCII.bin2ASCII(binarySentence.substring(8,56));
	}
	public String toString()
	{
		return super.toString()+", ID: "+getAircraftID();
	}
	public String toJedisString()
	{
		return super.toJedisString()+";"+getAircraftID();
	}



	
}
