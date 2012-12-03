import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class MailUnread {
	MailConnect m_connect = new MailConnect();
	ArrayList<String> MailBase = new ArrayList<String>();
	ArrayList<Integer> UnreadMails = new ArrayList<Integer>();
	String buff;
	String[] counter;
	
	private void readFile (String fileName){
		try{
			File mailFile = new File(fileName);
			Scanner scan = new Scanner(mailFile);
			while(scan.hasNext()){
				buff = scan.nextLine();
				MailBase.add(buff);
			}
		} catch (IOException e){
			System.out.println("IOException: There are problems with reading file");
		}
		
	}
	
	private void createFile(String fileName) throws IOException{
		File mailFile = new File(fileName);
		if (mailFile.exists() == false){
			mailFile.createNewFile();
		} else {
			System.out.println("File " + fileName + " exist!");
			System.out.println("Reading file data!");
			readFile(fileName);
		}
	}
	
	public int mailCounter(){
		int mailCount = 0;
		counter = m_connect.sendCommand("STAT\r\n", 1).split(" ");
		mailCount = Integer.valueOf(counter[1]);
		
		return mailCount;
	}

	public void checkMails(){
		try {
			MailSave m_save = new MailSave();
			createFile("MailIDBase.txt");
			int mailCount = mailCounter();
			PrintWriter save = new PrintWriter(new BufferedWriter(new FileWriter("MailIDBase.txt")));
			for (int i = 1; i<=mailCount; i++){
				counter = m_connect.sendCommand("UIDL " + i + "\r\n", 0).split(" ");
				if(MailBase.contains(counter[2])){
					System.out.println("Mail ID: " + counter[1] + " has been already downloaded!");
				}else{
					UnreadMails.add(Integer.valueOf(counter[1]));
					save.println(counter[2]);
				}
				
			}
			save.close();
			System.out.println("You have " + UnreadMails.size() + " unread mails!");
			m_save.getMail(UnreadMails);
		} catch (IOException e) {
			System.out.println("IOException: There are problems with checking mails");
		}
		
	}
}
