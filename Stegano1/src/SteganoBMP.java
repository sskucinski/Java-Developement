import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class SteganoBMP
{
	
	
	public SteganoBMP()
	{
		
	}
	
	
	public static boolean encode(String sciezka, String nazwaOryg, String rozszerzenie, String nazwaNowego, String text)
	{
		// Ukrywanie znaków
		String cala_nazwa = rysuj_sciezke(sciezka,nazwaOryg,rozszerzenie);
		BufferedImage buffIMG = getImage(cala_nazwa);
		
		BufferedImage image = duplikuj(buffIMG);
		image = ukryj_text(image,text);
		
		return(setImage(image,new File(rysuj_sciezke(sciezka,nazwaNowego,"bmp")),"bmp"));
	}
	
	
	public static String decode(String sciezka, String nazwa)
	{
		byte[] decode;
		try
		{
			// Wykrywanie znaków
			BufferedImage image  = duplikuj(getImage(rysuj_sciezke(sciezka,nazwa,"bmp")));
			decode = decode_text(pobierzBity(image));
			return(new String(decode));
		}
		catch(Exception e)
		{
			System.out.println("Nie mo¿na odczytaæ ukrytych znaków, lub ich nie ma...");
			return "";
		}
	}
	
	
	private static String rysuj_sciezke(String sciezka, String nazwa, String rozszerzenie)
	{
		// rozszerzenie w razie przysz³ych zmian...
		return sciezka + "/" + nazwa + "." + rozszerzenie;
	}
	
	
	private static BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			System.out.println("Problem z obrazkiem...");
		}
		return image;
	}
	
	
	private static boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete();
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Nie mo¿na zapisaæ obrazu...");
			return false;
		}
	}
	
	
	private static BufferedImage ukryj_text(BufferedImage image, String text)
	{
		
		byte img[]  = pobierzBity(image);
		byte msg[] = text.getBytes();
		byte len[]   = konwersja_bitow(msg.length);
		try
		{
			encode_text(img, len,  0); 
			encode_text(img, msg, 32);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
					"Nie mo¿na dodaæ do tego pliku tekstu!", "B³¹d!",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	
	private static BufferedImage duplikuj(BufferedImage image)
	{
		//Tworzymy nowy obraz z atrybutami starego
		BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D	graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose();
		return new_img;
	}
	
	
	private static byte[] pobierzBity(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	
	private static byte[] konwersja_bitow(int i)
	{
		
		byte byte3 = (byte)((i & 0xFF000000) >>> 24);
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16);
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 );
		byte byte0 = (byte)((i & 0x000000FF)	   );
		
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	
	
	private static byte[] encode_text(byte[] img, byte[] bity, int offset)
	{
		// Sprawdzamy czy d³ugoœæ danego napisu zmieœci nam sie w pliku...
		if(bity.length + offset > img.length)
		{
			System.out.println("Plik jest zbyt ma³y!");
		}
		
		for(int i=0; i<bity.length; ++i)
		{
			
			int dodaj = bity[i];
			for(int j=7; j>=0; --j, ++offset) 
			{
				
				int b = (dodaj >>> j) & 1;
				
				img[offset] = (byte)((img[offset] & 0xFE) | b );
			}
		}
		return img;
	}
	
	
	private static byte[] decode_text(byte[] image)
	{
		int length = 0;
		int offset  = 32;
		
		for(int i=0; i<32; ++i) 
		{
			length = (length << 1) | (image[i] & 1);
		}
		
		byte[] result = new byte[length];
		
		
		for(int b=0; b<result.length; ++b )
		{
			
			for(int i=0; i<8; ++i, ++offset)
			{
				
				result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
			}
		}
		return result;
	}
}
