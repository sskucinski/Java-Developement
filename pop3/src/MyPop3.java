import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;


public class MyPop3 {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		
		
	    String host = "HOST";
	    String username = "USER";
	    String password = "PASS";
	    String provider = "pop3";

	    Session session = Session.getDefaultInstance(props, null);
	    Store store = session.getStore(provider);
	    store.connect(host, username, password);

	    Folder inbox = store.getFolder("INBOX");
	    if (inbox == null) {
	      System.out.println("No INBOX");
	      System.exit(1);
	    }
	    inbox.open(Folder.READ_ONLY);
	    
	    Message[] messages = inbox.getMessages();
	    for (int i = 0; i < messages.length; i++) {
	      System.out.println("Message " + (i + 1));
	      //messages[i].writeTo(System.out);
	      String test = messages[i].getDescription();
	      if (test == null){
	    	  System.out.println("dupa");
	      }
	    }
	    inbox.close(false);
	    store.close();

	}

}
