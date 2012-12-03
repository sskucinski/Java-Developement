import java.util.ArrayList;


public class Main {
	static ArrayList <String> subpatch = new ArrayList <String>();
	static ArrayList <String> folder = new ArrayList <String>();
	static ArrayList <String> plik = new ArrayList <String>();
	static ArrayList <String> patch = new ArrayList <String>();
	static String locDir;
	static int n;
	static int glebokosc = 0;
	static int wspomagacz = 0;
	static int addWspom = 0;
	static String fol;
	/**
	 * @param args
	 * FTP HOST: ftp.man.szczecin.pl
	 */
	public static void main(String[] args) {
		
		
		if (args.length == 2 || args.length == 3) {
			
			FTPconnect con = new FTPconnect();
			String[] daneFTP = pobierzDane(args);
			
			fol = daneFTP[2];
			locDir = daneFTP[3];
			n = Integer.valueOf(daneFTP[4].replace("-", ""));
			
			System.out.println("n jest rowne: " + n);
		
		con.connect(daneFTP[1]);
		con.sendCommand("USER "+daneFTP[0]+"\r\n", 1);
		con.sendCommand("PASS \r\n", 1);
		con.sendCommand("CWD "+fol+"\r\n", 1);
		
		if (n == 0){
			con.sendCommand("PASV\r\n", 1);	
			con.reList("LIST\r\n");
			con.getTopology(folder, plik);
			if (folder.size() > 0){	
				for (int i=0; i<folder.size(); i++){
					con.saveDir(locDir + "/"+ fol + "/" + folder.get(i));
				}
			}
			if (plik.size() > 0){
				for (int i=0; i<plik.size(); i++){
					con.saveFile(plik.get(i), locDir+ "/" + fol);
				}
			}
			
			
		// PRZECHODZENIE W GLAB	
		} else {
				
			con.sendCommand("PASV\r\n", 1);	
			con.reList("LIST\r\n");
			con.getPatch(subpatch);
			
			for(int i=0; i<subpatch.size(); i++){
				patch.add(subpatch.get(i));
				
			}
			if (n > 0){
				// Tu byl clear subpatch...
				folder.clear();
				plik.clear();
				con.sendCommand("PASV\r\n", 1);	
				con.reList("LIST\r\n");
				con.getTopology(folder, plik);
				if (folder.size() > 0){	
					for (int j=0; j<folder.size(); j++){
						con.saveDir(locDir+ "/" + fol + "/" + folder.get(j));
					}
				}
				if (plik.size() > 0){
					for (int k=0; k<plik.size(); k++){
						con.saveFile(plik.get(k), locDir+ "/" +  fol );
					}
				}
				// PETLA WHILE
				while(glebokosc < n){
					subpatch.clear();
					
					while (wspomagacz < patch.size()){
						int x = wspomagacz;
						con.sendCommand("CWD "+patch.get(x)+"\r\n", 1);
						folder.clear();
						plik.clear();
						con.sendCommand("PASV\r\n", 1);	
						con.reList("LIST\r\n");
						con.getTopology(folder, plik);
						if (folder.size() > 0){	
							for (int j=0; j<folder.size(); j++){
								con.saveDir(locDir+ "/" +  fol + "/" + patch.get(x)+ "/" + folder.get(j));
								subpatch.add(patch.get(x)+ "/" + folder.get(j));
								addWspom++;
								
							}
							
						}
						if (plik.size() > 0){
							for (int k=0; k<plik.size(); k++){
								con.saveFile(plik.get(k), locDir+ "/" +  fol + "/" + patch.get(x));
							}
						}
						for(int cofka=0; cofka<=glebokosc; cofka++){
							con.sendCommand("CDUP\r\n", 1);
						}
						wspomagacz++;
					
					}
						for(int i=0; i<subpatch.size(); i++){
							patch.add(subpatch.get(i));
						}
						
						glebokosc++;
				}
					System.out.println("Zakonczono sukcessem!");
//					for(int i=0; i<patch.size(); i++){
//						System.out.println(patch.get(i));
//					}
			}
			
		
				
		} 
		} else {
			System.out.println("Zle wywolales komende programu");
			System.out.println("Poprawny format: java Main user@server_name/dir/dir localdir [-d]");
			System.exit(1);
		}
	
	}
	
	public static String[] pobierzDane(String[] args) {
		String[] daneFTP = new String[5];
		
		int poczatek, koniec;
		poczatek = args[0].lastIndexOf("@"); koniec = args[0].indexOf("/");
		if (koniec < 0)
			koniec = args[0].length();
		daneFTP[0] = args[0].substring(0, poczatek);
		daneFTP[1] = args[0].substring(poczatek+1, koniec);
		daneFTP[2] = "";
		if (koniec != args[0].length()) {
			daneFTP[2] = args[0].substring(koniec+1);
		}
		daneFTP[3] = args[1];
		if (args.length == 3)
			daneFTP[4] = args[2];
		else
			daneFTP[4] = "-1";
		
		return daneFTP;
	}

}
