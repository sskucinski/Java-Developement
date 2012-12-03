import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class MailConnect {
	static Socket sock = null;
	static PrintWriter output = null;
	static BufferedReader input = null;
	String buff;
	
	public void connect (String address) {
		try{
			sock = new Socket(address, 110);
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
	
	public String sendCommand (String identity, int variable){
		output.println(identity);
		try {
			buff = input.readLine();
			if (variable == 1){
				System.out.println("Server responce: " + buff);
				if (buff.startsWith("-ERR")){
					System.out.println("You have to pass wrong PASSWORD or USERNAME!");
					System.exit(1);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException: There are problems with authorization!");
			e.printStackTrace();
		}
		
		return buff;
	}
	
	public String reciveMessage(String command){
		output.println(command);
		String message = "";
		try{
			while((buff = input.readLine()) != null){
				if (buff.contains("+OK")){
					continue;
				}
				if(buff.length() > 1 && buff.startsWith(".")){
					buff = buff.replaceFirst(".", "");
					message += buff + "\n"; 
				} else if (buff.length() == 1 && buff.startsWith(".")){
					break;
				} else {
					message += buff + "\n";
				}
			}
		} catch(IOException e){
			System.out.println("IOException: There are problems with reciving message!");
		}
		
		return message;
	}
}
