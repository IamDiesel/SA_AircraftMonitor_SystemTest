package server;

import java.io.IOException;
import java.io.OutputStream;
//import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;

public final class WebServer {

   public static final class ActiveKML implements HttpHandler 
   {
        public void handle(HttpExchange t) throws IOException
        {
            String response = aircraft_active_kml();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


  public static final class MapBasic implements HttpHandler
  {	 
	  public void handle ( HttpExchange t ) throws IOException
	  {
		  try
		  {
		   System.err.println ( "map.basic acessed" ); // send basicMap.html
		   String response = new String ( Files.readAllBytes (Paths.get("basicMap.html") ) );
		   t.sendResponseHeaders ( 200, response.length() );
		   OutputStream os = t.getResponseBody();
		   os.write ( response.getBytes() );
		   os.close ();
		  }
		  catch(IOException e)
		  {
			  JOptionPane.showMessageDialog(new JFrame(), "Error while Accessing map.basic at Webserver."+e.getMessage()); 
		  }
	  }

    }

    private final static Jedis jed = new Jedis ("localhost");
    private static String aircraft_active_kml () {
    //boolean print = false;	
StringBuilder kml = new StringBuilder (256);
// creates a KML string from active.aircraft.* in redis
// basicMap.html expects "http://localhost:3000/aircraft_active_kml
// set preamble for the kml string
kml.append ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"     );
kml.append ( "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" );
kml.append ( "<Document>\n" );
Set<String> keys =   jed.keys ("*"); //( "aircraft.active.*" );
for ( String k : keys ) 
{
   // get the active positions from redis
   String position = jed.get ( k );
   //System.out.println(position);
   // 1385651813.7752786,3956954,9.536544799804688,48.21986389160156,18850
   // ts                 icao    lon               lat               alt
   String [] entry = position.split (",");
  // String lon = 
   //System.err.println ( entry[5] + "," + entry[4] + " size:"+entry.length);
   if (!entry[5].equals("-1.0") && !entry[4].equals("-1.0"))
   {
   kml.append ( "<Placemark>\n<name>" + entry[1] + "</name>\n" +
		   		"<Point>\n<coordinates>" + entry[5] + "," + entry[4] +
		   		",0</coordinates>\n</Point>\n</Placemark>\n" );
   }
}
	kml.append ( "</Document>\n" );
	kml.append ( "</kml>\n\n" );
	return kml.toString();
}

}