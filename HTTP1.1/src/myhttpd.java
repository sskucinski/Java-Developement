import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Date;

public class myhttpd implements Runnable {
	
	static File ROOT_FOLDER = new File("");
	static String FILE = "";
	static int PORT;
	static String IP = "";
	static int watek;
	ArrayList <String> filetest = new ArrayList <String>();
	
	Socket connect;
	
	
	public myhttpd(Socket connect)
	  {
	    this.connect = connect;
	  }
	
	
	
	public static void main(String[] args) {
		if (args.length == 0){	
			String instrukcja = "Uzycie: java myhttpd [PORT] [FOLDER]";
	        System.out.println(instrukcja);
	        return;
		} else if(args.length > 2){
			String instrukcja = "Uzycie: java myhttpd [PORT] [FOLDER]";
	        System.out.println(instrukcja);
	        return;
		}
		
		PORT = Integer.valueOf(args[0]);
		ROOT_FOLDER = new File(args[1]);
		
		try
	    {
		  
	      ServerSocket serverConnect = new ServerSocket(PORT);
	      System.out.println("\nNasluchiwanie na porcie: " + PORT);
	      
	      
	      
	      while (true) // Nasluch puki uzytkownik nie przerwie (CTRL+C)
	      {
	        myhttpd server = new myhttpd(serverConnect.accept());
	        System.out.println("Nowy klient poprosil o polaczenie (" + new Date() + ")");
	        
	        
	        
	        //Inicjalizacja watkow
	        Thread watki = new Thread(server);
	        watki.start();
	        System.out.println("Rozpoczecie watku dla nowego klienta! \nID Watku: " + watki.getId());
	        
	        InetAddress clientIP = InetAddress.getLocalHost();
	        System.out.println("ip: "+clientIP.getHostAddress());
	        IP = clientIP.getHostAddress();
	        watek = (int) watki.getId();
	      }
	    }
	    catch (IOException e)
	    {
	      System.err.println("Server error: " + e);
	    }

	}
	
	
	public void run(){
		BufferedReader input = null;
	    PrintWriter output = null;
	    BufferedOutputStream buffer = null;
	    String zadaniePliku = null;
	    
	    try{
	    	//Pobieramy zadanie od klienta
	    	input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
	    	//Odpowiedz do klienta
	    	output = new PrintWriter(connect.getOutputStream());
	    	//Nasz buffer
	    	buffer = new BufferedOutputStream(connect.getOutputStream());
	    	
	    	String wiadomosc = input.readLine();
	    		    	
	    	StringTokenizer st = new StringTokenizer(wiadomosc);
	    	String metoda = st.nextToken().toUpperCase();
	    	zadaniePliku = st.nextToken().toLowerCase();
	    	
	    	//Jezeli metoda bedzie roznic sie od GET lub HEAD wysylamy wiadomosc zwrotna
	    	if(!metoda.equals("GET") && !metoda.equals("HEAD")){
	    		System.out.println("Niepoprawne zadanie wyslane do serwera! - " + metoda);
	    		
	    		//Odsylamy do klienta informacje o problemie
	    		output.println("HTTP/1.1 501 wrong request");
	    		output.println("Server: Java HTTPd Server 0.01");
	            output.println("Date: " + new Date());
	            output.println("Content-Type: text/html");
	            output.println();
	            output.println("<HTML>");
	            output.println("<HEAD><TITLE>501 ERROR</TITLE>" + "</HEAD>");
	            output.println("<BODY>");
	            output.println("<H2>501 WRONG METHOD REQUEST: " + metoda + "</H2>");
	            output.println("</BODY></HTML>");
	            output.flush();
	            
	            return;
	    		
	    	}
	    	
	    	//System.out.println("IP: "+ connect.getRemoteSocketAddress());

	    	if(zadaniePliku.endsWith("/") && zadaniePliku.startsWith("/")){
	    		zadaniePliku = zadaniePliku.replace("/", "");
	    		File dir = new File(ROOT_FOLDER+"/"+zadaniePliku);
	    		if(!dir.exists()){
	    			dirNotFound(output, zadaniePliku);
	    			return;
	    		}else{
	    			SprawdzPliki(output);
	    			String direc = "";
	    			direc = zadaniePliku;
	    			
	    			zadaniePliku = "/"+direc+"/"+FILE;
	    		}
	    	}
	    	
	    	else if(zadaniePliku.endsWith("/")){
	    		SprawdzPliki(output);
	    		zadaniePliku = FILE;
	    	}
	    	
	    	
	    	
	    	File kronika = new File("kronika.txt");
	    	if(!kronika.exists()){
	    		kronika.createNewFile();
	    	}else{
	    		FileReader fr = new FileReader("kronika.txt");
	    		BufferedReader br = new BufferedReader(fr);
	    		String s;
	    		String old = "";
	    		
	    		while((s = br.readLine()) != null)
	    			{
	    			System.out.println(s);
	    			old = old + s + "\n";
	    			}
	    		fr.close();
	    		
	    		FileWriter fw = new FileWriter("kronika.txt");
	    		fw.write(old + "Data: ("+ new Date() + "), IP Klienta: " + IP +", URL: " + zadaniePliku + ", W¹tek: " + watek + "\n");
	    		fw.close();
	    	}
	    	
	    	
	    	
	    	
	    	//Tworzymy obiekt pliku
	        File file = new File(ROOT_FOLDER, zadaniePliku);
	        int fileLength = (int)file.length();

	        String content = getContentType(zadaniePliku);

	        if (metoda.equals("GET")) {
	          FileInputStream fileIn = null;
	          
	          byte[] fileData = new byte[fileLength];

	          try {
	            
	            fileIn = new FileInputStream(file);
	            fileIn.read(fileData);
	          }
	          finally {
	            close(fileIn);
	          }

	          //send HTTP headers
	          output.println("HTTP/1.1 200 OK");
	          output.println("Server: Java HTTP Server 1.0");
	          output.println("Date: " + new Date());
	          output.println("Content-type: " + content);
	          output.println("Content-length: " + file.length());
	          output.println(); 
	          output.flush();
	          
	          buffer.write(fileData,0,fileLength); //write file
	          buffer.flush(); //flush binary output stream buffer
	        }

	        
	          System.out.println("File " + zadaniePliku + " of type " + content + " returned.");
	        
	      }
	      catch (FileNotFoundException fnfe)
	      {
	        //inform client file doesn't exist
	        fileNotFound(output, zadaniePliku);
	      }
	      catch (IOException ioe)
	      {
	        System.err.println("Server Error: " + ioe);
	      }
	      finally
	      {
	        close(input); //close character input stream
	        close(output); //close character output stream
	        close(buffer); //close binary output stream
	        close(connect); //close socket connection
	        
	        
	          System.out.println("Connection closed.\n");
	        
	      }
	    	
	}
	
	private void dirNotFound(PrintWriter out, String file)
	{
		//send file not found HTTP headers
		out.println("HTTP/1.1 400 Directory Not Found");
		out.println("Server: Java HTTP Server 1.0");
		out.println("Date: " + new Date());
		out.println("Content-Type: text/html");
		out.println();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>Directory Not Found</TITLE>" +
				"</HEAD>");
		out.println("<BODY>");
		out.println("<H2>400 Directory Not Found: " + file + "</H2>");
		out.println("</BODY>");
		out.println("</HTML>");
		out.flush();

  
		System.out.println("400 Directory Not Found: " + file);
  
	}
	
	private void fileNotFound(PrintWriter out, String file)
	{
		//send file not found HTTP headers
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server 1.0");
		out.println("Date: " + new Date());
		out.println("Content-Type: text/html");
		out.println();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>File Not Found</TITLE>" +
				"</HEAD>");
		out.println("<BODY>");
		out.println("<H2>404 File Not Found: " + file + "</H2>");
		out.println("</BODY>");
		out.println("</HTML>");
		out.flush();

  
		System.out.println("404 File Not Found: " + file);
  
	}

	public void SprawdzPliki(PrintWriter output){
		//Sprawdzamy czy plik index.html, index.htm, index.php istnieje
		
    	filetest.clear();		
    	
    	filetest.add("index.php");
    	filetest.add("index.html");
    	filetest.add("index.htm");
    	
    	String finFil = "";
    	
    	
    	
    	
    	for (int i=0; i<filetest.size(); i++){
    		finFil = filetest.get(i).toString();
    		File chFile = new File(ROOT_FOLDER+"/"+finFil);
    		if (chFile.exists()){
    			    		
    			FILE = finFil;
    			return;
    		}
    	}
    	
    	if(FILE == ""){
    		fileNotFound(output, "index.php, index.html, index.htm");
    	}
	}
	
	
	 private String getContentType(String fileRequested)
	  {
	    if (fileRequested.endsWith(".htm") ||
	      fileRequested.endsWith(".html"))
	    {
	      return "text/html";
	    }
	    else if (fileRequested.endsWith(".gif"))
	    {
	      return "image/gif";
	    }
	    else if (fileRequested.endsWith(".jpg") ||
	      fileRequested.endsWith(".jpeg"))
	    {
	      return "image/jpeg";
	    }
	    else if (fileRequested.endsWith(".class") ||
	      fileRequested.endsWith(".jar"))
	    {
	      return "applicaton/octet-stream";
	    }
	    else
	    {
	      return "text/plain";
	    }
	  }
	
	
	public void close(Object stream)
	  {
	    if (stream == null)
	      return;

	    try
	    {
	      if (stream instanceof Reader)
	      {
	        ((Reader)stream).close();
	      }
	      else if (stream instanceof Writer)
	      {
	        ((Writer)stream).close();
	      }
	      else if (stream instanceof InputStream)
	      {
	        ((InputStream)stream).close();
	      }
	      else if (stream instanceof OutputStream)
	      {
	        ((OutputStream)stream).close();
	      }
	      else if (stream instanceof Socket)
	      {
	        ((Socket)stream).close();
	      }
	      else
	      {
	        System.err.println("Unable to close object: " + stream);
	      }
	    }
	    catch (Exception e)
	    {
	      System.err.println("Error closing stream: " + e);
	    }
	  }
	
	
	

}
