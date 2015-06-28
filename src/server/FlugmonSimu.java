package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FlugmonSimu implements HttpHandler{
	private BufferedReader br;
	private Scanner scanner;
	public FlugmonSimu(String inputDataPath) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(inputDataPath));
		this.scanner = new Scanner(new File(inputDataPath));
		scanner.useDelimiter("\r\n");
	}
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		
		LinkedList<String> messages = new LinkedList<String>();
		String message = "";
		String completeMessage = "";
		int dataLength = 0;
		while(scanner.hasNext())
		{
			messages.addLast(scanner.next());
			dataLength += messages.getLast().length();
			completeMessage +=messages.getLast();
		}
		//System.out.println("Simu:msgLength:"+dataLength+" CompMessage:"+completeMessage);
		
		//message = br.readLine();
		OutputStream os = null;
	    httpExchange.getResponseHeaders().add( "Content-type", "text/html" );
	    httpExchange.sendResponseHeaders( 200, dataLength);
	    os = httpExchange.getResponseBody();
	    os.write(completeMessage.getBytes(),0,dataLength);

		//System.out.println("Simu:Message:"+message+"messageLength"+message.length());

	/*	for(int i = 0; i < messages.size(); i++)
		    {
			os.write(messages.get(i).getBytes());
		    }*/
		   
		    //os.write("\r\n".getBytes());
		os.close();   
		os.flush();
		
		//httpExchange.sendResponseHeaders( 200, 0);
		//os.write("".getBytes());
		httpExchange.close();
		 
		
	}
	

}
