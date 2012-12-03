
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Pliki {
	// Odczyt bitow z pliku przeznaczonych do kodowania
				
		public static void zapisBitow(File file_wy, byte[] data) {
		      try {
		         OutputStream fos = new FileOutputStream(file_wy);
		         OutputStream bos = new BufferedOutputStream(fos);
		         bos.write(data);
		         bos.close();
		      }
		      catch (Exception e) { e.printStackTrace(); }
		   }
		
}
