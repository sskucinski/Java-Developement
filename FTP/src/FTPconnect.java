import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class FTPconnect {
	public Socket sock = null;
	public Socket sock2 = null;
	static PrintWriter output = null;
	static BufferedReader input = null;
	static BufferedReader input2 = null;
	private BufferedInputStream retPlik;
	String buff;
	String msg = "";
	String serv_msg = "";
	public int port;
	public String ip;
	ArrayList <String> listOut = new ArrayList <String>();
	boolean asci = true;
	

	
	
	public void connect (String address) {
		try{
			sock = new Socket(address, 21);
			output = new PrintWriter(sock.getOutputStream(),true);
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			buff = input.readLine();
			System.out.println("Server responce: " + buff);
			
		} catch (UnknownHostException e){
			System.out.println("Unknown Host address:" + address);
			System.exit(1);
		} catch (IOException e){
			System.out.println("IOException: There are problems with connection!");
			System.exit(1);
		}
	}
	protected String odpowiedz(boolean odp) throws IOException{
		if(!odp){
			buff = input.readLine();
			if(buff.charAt(3) == '-'){
				buff += "\n" + odpowiedz(odp);
			}
			if(buff.startsWith("227")){
				
				int poczatek = buff.indexOf('(');
				int koniec = buff.indexOf(')', poczatek+1);
				if (koniec > 0){
					String dane = buff.substring(poczatek + 1, koniec);
					StringTokenizer dzielnik = new StringTokenizer(dane, ",");
					ip = dzielnik.nextToken() + "." + dzielnik.nextToken() + "." + dzielnik.nextToken() + "." + dzielnik.nextToken();
					port = Integer.parseInt(dzielnik.nextToken()) * 256 + Integer.parseInt(dzielnik.nextToken());
				}
				
				
			}
		}
		
		return buff;
	}
	
	public String sendCommand (String identity, int variable){
		
		try {
			output.println(identity);
			//buff = input.readLine();
			if (variable == 1){
				odpowiedz(true);

				
			}
			odpowiedz(false);
		} catch (IOException e) {
			System.out.println("IOException: There are problems with sending command!");
			e.printStackTrace();
		}
		
		return buff;
	}
	
	public void reRetr(String command){
		
		try{
						
			sock2 = new Socket(ip, port);	
			retPlik = new BufferedInputStream(sock2.getInputStream());
			
			output.println(command);
			
			serv_msg = input.readLine();
			System.out.println("Server responce: " + serv_msg);
			if (serv_msg.charAt(3) == '-')
				while ((serv_msg = input.readLine()) != null){
					//System.out.println("Server responce: " + serv_msg);
				}
			
			
			if (asci){	
			byte[] buffer = new byte[1];
			StringBuilder strbld = new StringBuilder();
			while (retPlik.read(buffer) != -1) {
				strbld.append(new String(buffer));
			}
			retPlik.close();
			
			msg = strbld.toString();
			System.out.println("Server responce: " + input.readLine());
			}
			
			
			
						
			
			
			
		} catch(IOException e){
			System.out.println("IOException: There are problems with reciving message!");
		}
		
	}
	
	public void reList(String command){
		
		try{
			sock2 = new Socket(ip, port);
			input2 = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
			listOut.clear();
			output.println(command);
			msg += input.readLine() + "\n";
			while ((buff = input2.readLine()) != null){
			msg += buff + "\n";
			
			listOut.add(buff);
			
			}
			msg += input.readLine() + "\n";
						
			System.out.println("Server responce: \n" + msg);
			
			
		} catch(IOException e){
			System.out.println("IOException: There are problems with reciving message!");
		}
		
	}
	
	public void getPatch(ArrayList <String> folder){
		for (int i=0; i<listOut.size(); i++){
			String dziel;
			dziel = listOut.get(i);

			while(dziel.contains("  ")){
				dziel = dziel.replace("  ", " ");
			}

			String[] podzielone;
			podzielone = dziel.split(" ");
			
			if(podzielone[0].startsWith("d")){
				if(!podzielone[0].substring(4, 6).contains("--")){
					
				folder.add(podzielone[8]);
				}
			}else if(podzielone[0].startsWith("l")){
				System.out.println("Link skipped!");
			}
			
		}
		
	}
	
	public void getTopology(ArrayList <String> folder, ArrayList <String> plik){
		for (int i=0; i<listOut.size(); i++){
			String dziel;
			dziel = listOut.get(i);

			while(dziel.contains("  ")){
				dziel = dziel.replace("  ", " ");
			}

			String[] podzielone;
			podzielone = dziel.split(" ");
			
			if(podzielone[0].startsWith("d")){
				if(!podzielone[0].substring(4, 6).contains("--")){
					
				folder.add(podzielone[8]);
				}
			}else if(podzielone[0].startsWith("l")){
				System.out.println("Link skipped!");
			}else{
				plik.add(podzielone[8]);
			}
			
			//System.out.println("PLIKI: \n" + dziel);
		}
		
//		for (int i=0; i<folder.size(); i++){
//			System.out.println("FOLDER: " + folder.get(i));
//		}
//		for (int i=0; i<plik.size(); i++){
//			System.out.println("PLIK: " + plik.get(i));
//		}
	}
	
	public void saveDir(String name){
		if (name.contains("/")){
			new File(name).mkdirs();
			//System.out.println("Katalogi utworzone!");
		}else{
			new File(name).mkdir();
			//System.out.println("Katalog utworzony!");
		}
	}
	
	public void saveFile(String name, String dir){
		
		String extension = name.lastIndexOf(".") > 0 ? name.substring(name.lastIndexOf(".")) : "";
		
		if (ASCII(extension)){
			sendCommand("TYPE A\r\n", 1);
			if (buff.startsWith("200")) {
				sendCommand("PASV\r\n", 1);
				asci = true;
				reRetr("RETR " + name +"\r\n");
				if(serv_msg.startsWith("150")){
					
					try {
						FileWriter output = new FileWriter(name);
						output.write(msg);
						output.flush();
						output.close();
						
						File plik = new File(name);
						File folder = new File(dir);
						
						plik.renameTo(new File(folder, plik.getName()));
						
					} catch (IOException e) {
						System.out.println("Nie mo¿na zapisaæ pliku...");
					}
					
				} else {
					System.out.println("Error 500: Nie mozna odczytac pliku! =" + name);
				}
			}
		} else {
			sendCommand("TYPE I\r\n", 1);
			if (buff.startsWith("200")) {
				sendCommand("PASV\r\n", 1);
				asci = false;
				reRetr("RETR " + name +"\r\n");
				if(serv_msg.startsWith("150")){
					
					
					try {

						FileOutputStream outPlik = new FileOutputStream(name);
						
						
						byte[] buffer = new byte[1];
						while (retPlik.read(buffer) != -1) {
							outPlik.write(buffer);
							outPlik.flush();
						}
						System.out.println("Server responce: " + input.readLine());
						retPlik.close();
						outPlik.close();
						
						File plik = new File(name);
						File folder = new File(dir);
						
						plik.renameTo(new File(folder, plik.getName()));
						
					} catch (IOException e) {
						System.out.println("Nie mo¿na zapisaæ pliku...");
					}
					
				} else {
					System.out.println("File: \"" + name + "\" skipped.");
				}
				
			}
		}
				
	}
	
	private static String[] extensions = {
	    ".ajx", ".am", ".asa", ".asc", ".asp", ".aspx",
	    ".awk", ".bat", ".c", ".cdf", ".cf", ".cfg",
	    ".cfm", ".cgi", ".cnf", ".conf", ".cpp", ".css",
	    ".csv", ".ctl", ".dat", ".dhtml", ".diz", ".file",
	    ".forward", ".grp", ".h", ".hpp", ".hqx", ".hta",
	    ".htaccess", ".htc", ".htm", ".html", ".htpasswd",
	    ".htt", ".htx", ".in", ".inc", ".info", ".ini",
	    ".ink", ".java", ".js", ".jsp", ".log", ".logfile",
	    ".m3u", ".m4", ".m4a", ".mak", ".map", ".model",
	    ".msg", ".new", ".nfo", ".nsi", ".info", ".old", ".pas",
	    ".patch", ".perl", ".php", ".php2", ".php3", ".php4",
	    ".php5", ".php6", ".phtml", ".pix", ".pl", ".pm",
	    ".po", ".pwd", ".py", ".qmail", ".rb", ".rbl", ".rbw",
	    ".readme", ".reg", ".rss", ".rtf", ".ruby", ".session",
	    ".setup", ".sh", ".shtm", ".shtml", ".sql", ".ssh",
	    ".stm", ".style", ".svg", ".tcl", ".text", ".threads",
	    ".tmpl", ".tpl", ".txt", ".ubb", ".vbs", ".xhtml",
	    ".xml", ".xrc", ".xsl"
	};
	
	public static boolean ASCII(String extension) {
		boolean result = false;
		for (String asciiExt : extensions) {
			if (extension.equalsIgnoreCase(asciiExt)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	
}