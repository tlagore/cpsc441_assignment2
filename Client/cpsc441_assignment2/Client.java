package cpsc441_assignment2;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;

public class Client {
	public static void main(String[] args)
	{
		try{
			Socket socket = new Socket("localhost", 2255);
			InputStream inputStream;
			PrintWriter outputStream;
			int amountRead;
			byte[] input = new byte[10*1024];
			byte[] output;
			
			try{
				outputStream = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
				inputStream = socket.getInputStream();
				
				outputStream.println("GET path HTTP/1.0");
				outputStream.println("butts butts butts");
				outputStream.flush();
				
				outputStream.close();
				inputStream.close();
				socket.close();
				System.out.println();
				Thread.sleep(2000);
			}catch (Exception ex)
			{
				System.out.println("Explosion... " + ex.getMessage());
			}
		}catch(Exception ex)
		{
			System.out.println("Explosion..." + ex.getMessage());
		}
		
	}
}
