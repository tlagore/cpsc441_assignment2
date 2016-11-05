package cpsc441_assignment2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * WebServer takes in a port number and creates a ServerSocket on that port. It then waits in a loop
 * for HTTP requests. When it receives a socket connection, it creates a WebWorker thread to handle
 * the command and goes back to listening.
 * <p>
 * shutdown() can be called to terminate the WebServer loop.
 * 
 * @author Tyrone Lagore
 */
public class WebServer extends Thread {
	private boolean _Shutdown;
	private ServerSocket _ServerSocket;
	private ExecutorService _ExecutorService;
	
	public WebServer(int port){
		_Shutdown = false;
		_ExecutorService = Executors.newFixedThreadPool(10);
		try {
			_ServerSocket = new ServerSocket(port);
		}catch(IOException ex)
		{
			System.out.println("Error instantiating server socket for port " + port + ". " + ex.getMessage());
		}
	}
	
	/**
	 * run runs a loop waiting for socket connections. When it acquires a connection, it creates a WebWorker
	 * to handle 
	 */
	public void run()
	{
		try{
			while(!_Shutdown)
			{
				Socket socket = _ServerSocket.accept();
				System.out.println("Acquired connection.");
				_ExecutorService.execute(new WebWorker(socket));
			}
		}catch(IOException ex)
		{
			System.out.println("Server: " + ex.getMessage());
		}
		System.out.println("Server: Server has shut down. Remaining threads will shutdown shortly.");
	}
	
	public void shutdown(){
		System.out.println("Server: Received shutdown request, shutting down...");
		try{
			_ExecutorService.shutdownNow();
			_ServerSocket.close();
		}catch(IOException ex)
		{
			System.out.println("Server: Error closing socket: " + ex.getMessage());
		}
		
		_Shutdown = true;
	}
}
