package cpsc441_assignment2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * WebWorker is a threaded class that receives a socket and awaits a command.
 * 
 * It parses the command then returns the appropriate header response and file information if necessary.
 * 
 * WebWorker can only handle simple GET requests and cannot handle any conditional information
 * 
 * @author Tyrone
 */
public class WebWorker extends Thread{
	private Socket _Socket;
	private final int MAX_IN_SIZE = 10*1024;
	
	public WebWorker(Socket socket)
	{
		_Socket = socket;
	}
	
	/**
	 * Attempts to retrieve a command from the sockets input stream. It then parses the retrieved command
	 * and returns a header specifying the HTTP code as well as requested file if the command is in good form. 
	 */
	public void run()
	{
		PrintWriter outputStream;
		byte[] input = new byte[MAX_IN_SIZE];
		String command = "";
		String response = "";
		
		System.out.println("Thread " + this.getId() + " handling a request.");
		try{
			outputStream = new PrintWriter(new DataOutputStream(_Socket.getOutputStream()));
			
			_Socket.getInputStream().read(input);
			
			//get string version of response 
			command = extractStringFromByte(input);
			System.out.println("Received command:");
			System.out.println(command);
			
			HttpRequest request = new HttpRequest(command);
			response = request.getResponseHeader();
			
			
			if (request.getCode() == 200)
			{
				//if requestedFilePath is null, then home directory was requested
				if (request.getRequestedFilePath() == null)
				{
					response += "Base path requested, would be index.html.";
					outputStream.print(response);
				}
				else
				{
					write200Response(response, request.getRequestedFilePath());
				}
			}else
				outputStream.print(response);
				
			
			System.out.println("Response header:");
			System.out.print(request.getResponseHeader());
			
			outputStream.close();
			_Socket.close();
			System.out.println();
		}catch(IOException ex)
		{
			System.out.println("Error in thread " + this.getId() + ": " + ex.getMessage());
		}		
		System.out.println("Thread " + this.getId() + " exiting.");
	}
	
	/**
	 * write200Response takes in a headerResponse to an http request along with the requested file and writes the contents
	 * of the header and the file to the sockets output stream held.
	 * 
	 * The header response is assumed to be well formed.
	 * 
	 * @param headerResponse the properly formed header response of a GET request
	 * @param requestedFilePath the path of the file to be written to the sockets outputstream
	 */
	private void write200Response(String headerResponse, Path requestedFilePath)
	{
		try{
			byte[] response = headerResponse.getBytes();
			byte[] file = Files.readAllBytes(requestedFilePath);
			byte[] output = mergeByteArrays(response, file);

			_Socket.getOutputStream().write(output);
			//System.out.println(output.toString());
		}catch(IOException ex)
		{
			System.out.println("Error in thread " + this.getId() + ": " + ex.getMessage());
		}
	}
	
	/**
	 * mergeByteArrays takes in two byte arrays and returns an array that holds the contents of arrA + arrB
	 * 
	 * @param arrA The first array to be joined, the contents of this array will appear at the beginning of the returned array
	 * @param arrB The second array to be joined, the contents of this array will appear at the end of the returned array
	 * @return A byte containing a combination of arrA and ArrB
	 */
	private byte[] mergeByteArrays(byte[] arrA, byte[] arrB)
	{
		byte[] arrC = new byte[arrA.length + arrB.length];
	
		System.arraycopy(arrA, 0, arrC, 0, arrA.length);
		System.arraycopy(arrB, 0, arrC, arrA.length, arrB.length);
		
		return arrC;
	}
	
	/**
	 * extractStringFromByte extracts the contents of a byte array into a string. The command is assumed to be delimited by 
	 * new line characters.
	 * 
	 * The characters of the array are *not* checked to ensure that they are String compatible characters.
	 * 
	 * @param data the byte array holding the command
	 * @return a String representation of the byte array
	 */
	private String extractStringFromByte(byte[] data)
	{
		String command = "";
		String line;
		InputStream inputStream = new ByteArrayInputStream(data);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		try
		{
			line = reader.readLine();
			while (line != null && !line.isEmpty())
			{
				command += line + "\r\n";
				line  = reader.readLine();
			}
		}catch(IOException ex)
		{
			System.out.println("Error: " + ex.getMessage());
		}
		
		return command;
	}
}
