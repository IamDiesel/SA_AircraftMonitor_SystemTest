package domain;

import exception.AdsMessageException;


public final class AirbornePositionMessage extends AdsMessage{

	public AirbornePositionMessage(String binarySentence,int messageTypeD, int originatorD, long time) throws AdsMessageException 
	{
		super(binarySentence,messageTypeD, originatorD, time);
	}
	public AirbornePositionMessage(String jedisString) throws AdsMessageException
	{
		super(jedisString);
	}
	public int getAltitude()
	{
		int factor = -1;
		if(Integer.parseInt(binarySentence.substring(15,16),2) == 0)
			factor = 100;
		else
			factor = 25;
		int result = factor*Integer.parseInt(binarySentence.substring(8,15)+binarySentence.substring(16,20),2);
		if(result < -1000 || result > 50175)
			throw new AdsMessageException(3, "Plausibility Check failed at altitude calculation: Accepted range -1000 < x < 50175. x is: " + result);
		return result;
	}
	public boolean isOdd()
	{
		boolean result = false;
		if(binarySentence.charAt(21) != '0')
			result = true;
		return result;
	}
	public boolean getTimeFlag()
	{
		boolean result = false;
		if(binarySentence.charAt(20) == '1')
			result = true;
		return result;
	}
	public int getLatitude()
	{
		return Integer.parseInt(binarySentence.substring(22,39),2);
	}
	public int getLongitude()
	{
		return Integer.parseInt(binarySentence.substring(39,56),2);
	}
	
	public String toString()
	{
		return super.toString()+", Altitude: "+getAltitude()+", isOdd: "+isOdd()+", TimeFlag: "+getTimeFlag()+", Lat: "+getLatitude()+", Lon: "+getLongitude();
	}
	public String toJedisString()
	{
		return super.toJedisString();
	}
	

}

