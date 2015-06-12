package translator;

import exception.SixBitASCIIException;





public final class SixBitASCII {
public static String bin2ASCII(String binString)
{
	String result = "";
	if(binString.length() % 6 == 0)
	{
		for(int i = 0; i < binString.length(); i=i+6)
		{
			if(binString.substring(i,i+1).equals("0"))
				result =  result + (char) (Integer.parseInt(binString.substring(i,i+6),2)+64);
			else
				result =  result + (char) (Integer.parseInt(binString.substring(i,i+6),2));
		}
		
		
	}
	else
		throw new SixBitASCIIException(400, "Binary String size mismatch. String length % 6 should be 0 but is: " + String.valueOf(binString.length() %6), binString);
	
	return result;	
}
/*public static void main(String[] args)
{
	try {
		System.out.println(bin2ASCII("000100110000000001001110001001000101001100"));
	} catch (SixBitASCIIException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}*/
}
