package cpsc441_assignment2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class HttpRequest {
	private final String _Directory = System.getProperty("user.dir");
	
	private Path _RequestedFilePath;
	
	private int _Code;
	private String _Method;
	private String _Path;
	private String _Host;
	
	public HttpRequest(String request)
	{
		_RequestedFilePath = null;
		
		String[] lines = request.split(System.getProperty("line.separator"));
		String[] firstLine = lines[0].split(" ");
		
		firstLine[0].replace("HTTP/1.1", "").replace("HTTP/1.0", "");
		if (firstLine[0].toUpperCase().compareTo("GET") != 0)
		{
			_Code = 400;
		}else
		{
			_Method = "GET";
			_Path = firstLine.length > 1 ? firstLine[1].replace(" ", "") : "";

			Path path = Paths.get(_Directory + _Path);
			if(Files.exists(path))
			{
				_Code = 200;
				if(_Path.compareTo("/") != 0)
					_RequestedFilePath = path;
			}else
				_Code = 404;
			
			for (int i = 1; i < lines.length; i ++)
			{
				if(lines[i].contains("Host"))
					_Host = lines[i].replace("Host: ", "");
			}
		}
	}

	public int getCode() {
		return _Code;
	}

	public String getMethod() {
		return _Method;
	}

	public String getPath() {
		return _Path;
	}

	public String getHost() {
		return _Host;
	}
	
	public String getResponseHeader(){
		String codeString = getCodeString();
		String header = "HTTP/1.0 " + _Code + " " + codeString + "\r\nConnection: close\r\n";
		
		if(_Code == 200 && _Path.compareTo("/") != 0){
			File file = new File(_RequestedFilePath.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			header += "Last-Modified: " + dateFormat.format(file.lastModified()) + "\r\n";
		}
		
		return header + "\r\n";
	}
	
	private String getCodeString(){
		String codeString = "";
		switch(_Code)
		{
			case 200:
				codeString = "OK";
				break;
			case 400:
				codeString = "Bad Request";
				break;
			case 404:
				codeString = "Not Found";
				break;
			//should not get to default
			default:
				_Code = 500;
				codeString = "Internal Server Error";
				break;
		}
		
		return codeString;
	}
	
	public Path getRequestedFilePath()
	{
		return _RequestedFilePath;
	}
}
