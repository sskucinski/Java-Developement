import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MailSave {
	String[] message;
	String[] parting_mail;
	static String msg_txt;
	static String subject = "";
	static String hsubject = "";
	static String from = "";
	static String to = "";
	static String date = "";
	static String charset = "";
	static String bound = "";
	static String attachment_file_name = "";
	static char[] attachment_content = null;
	static boolean multi = false;
	static int var = 0;
	
	
	public void splitting(String mail) throws IOException{
		message = mail.split("\n");
		
		for (String line : message){
			
			if(line.contains("Subject:")){
				subject = line.replace("Subject: ", "");
				if (line.contains("=?UTF-8?Q?")){
					subject = subject.replace("=?UTF-8?Q?", "");
				}
				else if (line.contains("=?ISO-8859-2?Q?")){
					subject = subject.replace("=?ISO-8859-2?Q?", "");
					var = 1;
				}
				subject = subject.replace("_", " ");
				subject = subject.replace("?=", "");
				subject = getDecoded(subject);
				hsubject = subject;
				hsubject = hsubject.replace("?", "");
				
			}
			
			else if(line.contains("From:")){
				from = line.replace("From: ", "");
				if (line.contains("=?UTF-8?Q?")){
					from = from.replace("=?UTF-8?Q?", "");
				}
				else if (line.contains("=?ISO-8859-2?Q?")){
					from = from.replace("=?ISO-8859-2?Q?", "");
					var = 1;
				}
				from = from.replace("_", " ");
				from = from.replace("?=", "");
				from = getDecoded(from);
			}
			
			else if(line.contains("To:")){
				to = line.replace("To: ", "");
				if (line.contains("=?UTF-8?Q?")){
					to = to.replace("=?UTF-8?Q?", "");
				}
				else if (line.contains("=?ISO-8859-2?Q?")){
					to = to.replace("=?ISO-8859-2?Q?", "");
					var = 1;
				}
				to = to.replace("_", " ");
				to = to.replace("?=", "");
				to = getDecoded(to);
			}
			
			else if(line.contains("Date:")){
				date = line.replace("Date: ", "");				
			}
			
			else if(line.contains("Content-Type: text/plain")){
				if(line.contains("UTF-8") || line.contains("utf-8")){
					charset = "UTF-8";
				}
				else if(line.contains("ISO-8859-2") || line.contains("iso-8859-2")){
					charset = "ISO-8859-2";
				}
				
				
			}
			
			else if(line.startsWith("Content-Type: multipart") || line.contains("boundary=\"")){
				multi = true;
				if(line.contains("boundary=\"")){
					bound = line.substring(line.indexOf("boundary=\"") + "boundary=\"".length(), line.lastIndexOf("\""));
				}
			}
			
		}
		//System.out.println("Temat: " + subject + "\n" + "From: " + from + "\n" + "To: " + to + "\n" + "Date: " + date + "\n" + "Charset: " + charset);
		
		if (multi == true){
			parting_mail = mail.split("--" + bound);
			for (String spli : parting_mail){
				if(spli.contains("Content-Transfer-Encoding: quoted-printable")){
					
					getTxtMsgDecoded(spli);
					
				} else if (spli.contains("Content-Disposition: attachment") && spli.contains("Content-Transfer-Encoding: base64")){
					
					String [] aTable = spli.split("\n");
						for (String row : aTable){
							if (row.contains("filename=\"")){
								attachment_file_name = row.replace("filename=\"", "");
								if (row.contains("=?UTF-8?Q?")){
									attachment_file_name = attachment_file_name.replace("=?UTF-8?Q?", "");
								}
								else if (row.contains("=?ISO-8859-2?Q?")){
									attachment_file_name = attachment_file_name.replace("=?ISO-8859-2?Q?", "");
								}
								attachment_file_name = attachment_file_name.replace("?=\"", "");
								attachment_file_name = attachment_file_name.replace("\"", "");
								attachment_file_name = attachment_file_name.trim();
							}
						}
						String [] bTable = spli.split("\n");
						String attachment = "";
						boolean check = true;
						for (int i = 0; i < bTable.length; i++) {
							if (check == false) {
								if (bTable[i].length() == 0) {
									check = true;
								}
							} else {
								attachment += bTable[i] + "\n";
								if (bTable[i].length() == 0) {
									check = false;
								}
							}
						}
						
						
						
							attachment_content = attachment.toCharArray();
							//System.out.println("Nazwa pliku: " + attachment_file_name + "\nZawartosc: " + attachment_content);
							File file_wy = new File(attachment_file_name);
							byte[] odkodowane = Decode.decode(attachment_content);
							System.out.println("Saving attachment: " + attachment_file_name);
							Pliki.zapisBitow(file_wy, odkodowane);
							
							
				} 
				
			}
					
		} else {
			getTxtMsgDecoded(mail);
			
		}
	}
	
	private String getDecoded(String txt){
		if(var == 0){
			txt = txt.replace("=20", " ");
			txt = txt.replace("=C4=84", "¥");
			txt = txt.replace("=C4=86", "Æ");
			txt = txt.replace("=C4=98", "Ê");
			txt = txt.replace("=C5=81", "£");
			txt = txt.replace("=C5=83", "Ñ");
			txt = txt.replace("=C3=93", "Ó");
			txt = txt.replace("=C5=9A", "Œ");
			txt = txt.replace("=C5=B9", "");
			txt = txt.replace("=C5=BB", "¯");
			txt = txt.replace("=C4=85", "¹");
			txt = txt.replace("=C4=87", "æ");
			txt = txt.replace("=C4=99", "ê");
			txt = txt.replace("=C5=82", "³");
			txt = txt.replace("=C5=84", "ñ");
			txt = txt.replace("=C3=B3", "ó");
			txt = txt.replace("=C5=9B", "œ");
			txt = txt.replace("=C5=BA", "Ÿ");
			txt = txt.replace("=C5=BC", "¿");
		} else if (var == 1){
			txt = txt.replace("=20", " ");
			txt = txt.replace("=A1", "¥");
			txt = txt.replace("=C6", "Æ");
			txt = txt.replace("=CA", "Ê");
			txt = txt.replace("=A3", "£");
			txt = txt.replace("=D1", "Ñ");
			txt = txt.replace("=D3", "Ó");
			txt = txt.replace("=A6", "Œ");
			txt = txt.replace("=AC", "");
			txt = txt.replace("=AF", "¯");
			txt = txt.replace("=B1", "¹");
			txt = txt.replace("=E6", "æ");
			txt = txt.replace("=EA", "ê");
			txt = txt.replace("=B3", "³");
			txt = txt.replace("=F1", "ñ");
			txt = txt.replace("=F3", "ó");
			txt = txt.replace("=B6", "œ");
			txt = txt.replace("=BC", "Ÿ");
			txt = txt.replace("=BF", "¿");
			txt = txt.replace("=3D", "=");
			txt = txt.replace("=3F", "?");
		}
		return txt;
	}
	
	private String getTxtMsgDecoded(String spli){
		msg_txt = spli.substring(spli.indexOf("Content-Transfer-Encoding: quoted-printable") + "Content-Transfer-Encoding: quoted-printable".length());
		if(charset == "UTF-8"){
			msg_txt = msg_txt.replace("=20", " ");
			msg_txt = msg_txt.replace("=C4=84", "¥");
			msg_txt = msg_txt.replace("=C4=86", "Æ");
			msg_txt = msg_txt.replace("=C4=98", "Ê");
			msg_txt = msg_txt.replace("=C5=81", "£");
			msg_txt = msg_txt.replace("=C5=83", "Ñ");
			msg_txt = msg_txt.replace("=C3=93", "Ó");
			msg_txt = msg_txt.replace("=C5=9A", "Œ");
			msg_txt = msg_txt.replace("=C5=B9", "");
			msg_txt = msg_txt.replace("=C5=BB", "¯");
			msg_txt = msg_txt.replace("=C4=85", "¹");
			msg_txt = msg_txt.replace("=C4=87", "æ");
			msg_txt = msg_txt.replace("=C4=99", "ê");
			msg_txt = msg_txt.replace("=C5=82", "³");
			msg_txt = msg_txt.replace("=C5=84", "ñ");
			msg_txt = msg_txt.replace("=C3=B3", "ó");
			msg_txt = msg_txt.replace("=C5=9B", "œ");
			msg_txt = msg_txt.replace("=C5=BA", "Ÿ");
			msg_txt = msg_txt.replace("=C5=BC", "¿");
		} else if (charset == "ISO-8859-2"){
			msg_txt = msg_txt.replace("=20", " ");
			msg_txt = msg_txt.replace("=A1", "¥");
			msg_txt = msg_txt.replace("=C6", "Æ");
			msg_txt = msg_txt.replace("=CA", "Ê");
			msg_txt = msg_txt.replace("=A3", "£");
			msg_txt = msg_txt.replace("=D1", "Ñ");
			msg_txt = msg_txt.replace("=D3", "Ó");
			msg_txt = msg_txt.replace("=A6", "Œ");
			msg_txt = msg_txt.replace("=AC", "");
			msg_txt = msg_txt.replace("=AF", "¯");
			msg_txt = msg_txt.replace("=B1", "¹");
			msg_txt = msg_txt.replace("=E6", "æ");
			msg_txt = msg_txt.replace("=EA", "ê");
			msg_txt = msg_txt.replace("=B3", "³");
			msg_txt = msg_txt.replace("=F1", "ñ");
			msg_txt = msg_txt.replace("=F3", "ó");
			msg_txt = msg_txt.replace("=B6", "œ");
			msg_txt = msg_txt.replace("=BC", "Ÿ");
			msg_txt = msg_txt.replace("=BF", "¿");
			msg_txt = msg_txt.replace("=3D", "=");
			msg_txt = msg_txt.replace("=3F", "?");
		}
		
		return msg_txt;
	}
	
	
	public void saveMailToFile(String filename, String txt) throws IOException{
		File mailFile = new File(filename);
		if (mailFile.exists() == false){
			mailFile.createNewFile();
			PrintWriter save = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			save.println(txt);
			save.close();
		} else {
			System.out.println("File " + filename + " exist!");
			System.exit(1);
		}
		
	}
	
	public void getMail(ArrayList<Integer> unreadMails) throws IOException{
		MailConnect m_connect = new MailConnect();
		
		for (int mailID : unreadMails){
			String mail = m_connect.reciveMessage("RETR " + mailID + "\r\n");
			splitting(mail);
			System.out.println("Saving e-mail: " + hsubject + " to file...");
			saveMailToFile(hsubject+".txt", "Od: "+from+"\n"+"Do: "+to+"\n"+"Data: "+date+"\n"+"Temat: "+subject+"\n"+"\n"+msg_txt);
		}
		
	}
}
