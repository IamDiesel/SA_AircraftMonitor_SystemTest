package exception;

import redis.clients.jedis.exceptions.JedisDataException;
import domain.Aircraft;


public class AircraftException extends JedisDataException{
	private Aircraft aircraft;
	public AircraftException(int errNo, String errText, Aircraft aircraft)
	{
		super("AircraftException No"+ errNo +": " + errText + "\n" + "Aircraft Content: --Begin--\n" + aircraft.toString() +"\n--End Msg Content--");
		this.aircraft = aircraft;
	}
	public Aircraft getAircraft()
	{
		return aircraft;
	}
		
	
	
}
