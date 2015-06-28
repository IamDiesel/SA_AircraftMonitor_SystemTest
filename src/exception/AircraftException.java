package exception;

import redis.clients.jedis.exceptions.JedisDataException;
import domain.Aircraft;


public class AircraftException extends JedisDataException{
	public AircraftException(int errNo, String errText, int aircraftID, String dataFlow, double velocity, double veloAngle, double latitude, double longitude, String flightNo)
	{
		super("AircraftException No"+ errNo +": " + errText + "\n" + "Aircraft Content: --Begin--\n" +
				"velocity: " +velocity+
				", veloAngle: " +veloAngle+
				", aircraftID: " +aircraftID+
				", latitude: " +latitude+
				", longitude: " +longitude+
				",LPDF: "+ dataFlow +"\n--End Msg Content--");
	
	}
	
	
}
