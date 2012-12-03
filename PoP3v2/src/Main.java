import java.util.Scanner;

public class Main {
	
	public static void main(String[] args){
	
		MailConnect m_connect = new MailConnect();
		MailUnread m_unread = new MailUnread();
		// Koñcowe
		if(args.length == 0){
			System.out.println("You have to pass server address and your login...");
			System.exit(1);
		}
		m_connect.connect(args[0]);
		m_connect.sendCommand("USER " + args[1] + "\r\n", 1);
		Scanner in = new Scanner(System.in);
		System.out.println("Password: ");
		String password = in.nextLine();
		m_connect.sendCommand("PASS "+password+"\r\n", 1);
		m_unread.checkMails();
		
		
		
		m_connect.sendCommand("quit\r\n", 1);
		
		
	}

}
