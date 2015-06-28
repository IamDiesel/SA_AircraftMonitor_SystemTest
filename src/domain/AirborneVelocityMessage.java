package domain;

import exception.AdsMessageException;





public final class AirborneVelocityMessage extends AdsMessage{
										
	//Constructor for Subtype 1/2:
	public AirborneVelocityMessage(String binarySentence, int messageTypeD,int originatorD,long time,String dataFlow) throws AdsMessageException 
	{
		super(binarySentence,messageTypeD, originatorD, time,dataFlow);
	}

	
	
	//GENERAL getTER METHODS / DECODING
	public int getVerticalRateSource()
	{
		return Integer.parseInt(binarySentence.substring(35,36),2);
		//0..geometry source, 1.. barometric source
	}
	public int getVerticalRateSign()
	{
		return Integer.parseInt(binarySentence.substring(36,37),2);
		//0..going up, 1.. going down
	}
	public int getVerticalRate()//nochmal (factor-1)*64 überprüfen
	{
		int factor = Integer.parseInt(binarySentence.substring(37,46),2);
		if(factor == 0)
			throw new AdsMessageException(18,"Bit 35..45 is set to 0 meaning there is no vertical movement information available.");
		else
			return (factor-1)*64;
	}
	public boolean getGeoGreaterBaroAttitude()
	{
		switch(Integer.parseInt(binarySentence.substring(48,49),2))
		{
		case 0: return true;
		case 1: return false;
		default: throw new AdsMessageException(12,"Data has exeeded binary range while checking sign of baro-alto sign bit (48). Allowed: (0..positive 1..negative.). Value is: "+Integer.parseInt(binarySentence.substring(48,49),2) );
		}
	}
	//Returns the Difference (in ft) between BarometricAltitude and BarometricAltitude
	public int getDeltaBaroAltoAttitude()
	{
		int delta = Integer.parseInt(binarySentence.substring(49,56),2);
		if(delta == 0)
		{
			throw new AdsMessageException(11,"Difference from baro-altitude information is not available. This is indicated by delta value of 0.");
		}
		else
		{
			if(this.getGeoGreaterBaroAttitude())
				return (delta-1)*25;
			else
				return -(delta-1)*25;
		}
	}
	//END GENERAL getTER MEHTODS / DECODING
	
	
	//SUBTYPE 1/2 getter Methods / Decoding
	public double getNavigationAccuracy()
	{
		// Parsing Navigation Accuracy Bits 10-12
		int navAccFactor = Integer.parseInt(binarySentence.substring(10,13),2);
		switch(navAccFactor)
		{
		case 1: return 10;
		case 2: return 3;
		case 3: return 1;
		case 4: return 0.3;
		default:throw new AdsMessageException(13,"Invalid value at calculation navigation accuracy. Allowed values are 1..4. 0 is an error indicated by airplane. Value is: " + navAccFactor);
		}
	}
	public boolean getMovingEast()
	{
		boolean result = true;
		switch(getMessageSubType())
		{
		case 1:
		case 2: if(binarySentence.charAt(13) == '1')
					result = false; 
				break;
		default: throw new AdsMessageException(10,"Invalid Subtype for checking EastWest direction bit. Only Subtypes 1/2 may check for East/West-movement. Subtype has value: "+getMessageSubType());
		}
		return result;
	}
	public boolean getMovingWest()
	{
		
		boolean result = false;
		switch(getMessageSubType())
		{
		case 1:
		case 2: if(binarySentence.charAt(13) == '1')
					result = true; 
				break;
		default: throw new AdsMessageException(9,"Invalid Subtype for checking EastWest direction bit. Only Subtypes 1/2 may check for East/West-movement. Subtype has value: "+getMessageSubType());
		}
		return result;
		
	}
	public int getEastWestVelocity()
	{
		int knots = Integer.parseInt(binarySentence.substring(14,24),2);
		
		if(knots > 0)
			return knots-1;
		else
			{
			 throw new AdsMessageException(6,"Plausibility Check at getEastWestVelocity failed. EastWest direction must have negative velocity. Knots-Value is:: "+String.valueOf(knots-1));
			}
	}
	public boolean getMovingSouth()
	{
		boolean result = false;
		switch(getMessageSubType())
		{
		case 1: case 2:
			if(binarySentence.charAt(24) == '1')
				result = true;
			break;
		default: throw new AdsMessageException(8,"Invalid Subtype at moving South Check. Only subtypes 1 and 2 may check for direction. SubTypeValue is: "+getMessageSubType());
		}
		return result;
		
	}
	public int getNorthSouthVelocity()
	{
		int knots = Integer.parseInt(binarySentence.substring(25,35),2);
		
		if(knots > 0)
			return knots-1;
		else
			{
			 throw new AdsMessageException(7,"Plausibility Check at getNorthSouthVelocity failed. NortSouth direction must have positive velocity. Knots-Value is:: "+String.valueOf(knots-1));
			}
	}
	//END Subtype 1/2 getter Methods / Decoding
	
	//SUBTYPE 3/$ getter Methods / Decoding
	//SUBTYPE 3/4 Methods

	public boolean getHeadingStatusAvailable()
	{
		boolean result = false;
		switch(Integer.parseInt(binarySentence.substring(13,14),2))
		{
		case 0: break;
		case 1: result = true;break;
		default: throw new AdsMessageException(15,"Data has exeeded binary range while checking availability of heading status bit (13). Allowed: (0..heading data not available 1..available.). Value is: "+Integer.parseInt(binarySentence.substring(13,14),2) );
		}
		return result;
	}
	public double getHeadingDegrees()
	{
		double angle=0;
		switch(getMessageSubType())
		{
		case 1: case 2:
			angle = Math.atan((double)getNorthSouthVelocity()/getEastWestVelocity())*180/Math.PI;	
			if(getMovingSouth())
			{
				if(getMovingWest()) //Moving South-West
					angle = 180 + angle;
				else					//Moving South-East
					angle = 360 - angle;	
			}
			else
			{
				if(getMovingWest()) //Moving North-West
					angle = 180 - angle;
				else					//Moving North-East
					angle = angle;
			}
			break;
		case 3: case 4:
			if(getHeadingStatusAvailable() == false)
				throw new AdsMessageException(16,"Error while accessing Heading data. Heading data bit is set to 0 meaning no heading data is available.");
			int factor = Integer.parseInt(binarySentence.substring(14,24),2);
			angle = factor*360.0/1024.0;
			break;
		default: throw new AdsMessageException(17,"Unknown Subtype while accessing heading data. Allowed subtypes are 1..4. Subtype is: "+getMessageSubType());
		}
		return angle;
	}
	public boolean getTrueAirspeedType()
	{
		boolean result = false;
		switch(binarySentence.substring(24,25))
		{
		case "0": break;
		case "1": result = true; break;
		default: throw new AdsMessageException(14,"Bit at pos 24 (indicating true airspeed) is exeeding binary limits. Allowed values are 1..true airspeed and 0...indicated airspeed. Value is:" + binarySentence.substring(24,25));
		}
		return result;
	}
	public double getAirspeed()
	{
		double airspeed=0.0;
		switch(getMessageSubType())
		{
		case 1: case 2:
			airspeed = Math.sqrt(Math.pow((double)getEastWestVelocity(), 2)+Math.pow((double)getNorthSouthVelocity(),2));
			break;
		case 3: case 4:
			int knots = Integer.parseInt(binarySentence.substring(25,35),2);
			if(knots > 0)
				airspeed = knots-1;
			else
			{
				throw new AdsMessageException(4,"Plausibility Check failed at airspeed calculation. Airspeed must be positive. 0 Indicates no Airspeed data is available. Calculated Airspeed: "+knots);
			}
			break;
		default: throw new AdsMessageException(5,"Unknown Message Subtype at calculating Airspeed. Known Subtypes are: 1..4. Subtype has value: "+getMessageSubType());

		}
		return airspeed;
	}
	public int getMessageSubType()
	{
		return Integer.parseInt(binarySentence.substring(5,8),2);
	}
	//END SUBTYPE 3/4 getter Methods / Decoding
	public String toString()
	{
		String res = super.toString();
		res += ", Airsp.: "+getAirspeed()+ ", H-Degr: "+getHeadingDegrees();
		return res;
	}
	public String toJedisString()
	{
		return super.toJedisString()+";"+getAirspeed()+";"+getHeadingDegrees();
	}

	
	
	
	
}

