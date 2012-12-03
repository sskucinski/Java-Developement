import java.io.File;


public class Filtr_rozszerzen extends javax.swing.filechooser.FileFilter
{
	
	protected boolean isImageFile(String ext)
	{
		return (ext.equals("bmp"));
	}
	
	
	public boolean accept(File f)
	{
	    if (f.isDirectory())
	    {
			return true;
	    }

	    String extension = getExtension(f);
		if (extension.equals("bmp"))
		{
			return true;
		}
		return false;
	}
	
	
	public String getDescription()
	{
		return "BitMapa   *bmp / *BMP";
	}
	
	
	protected static String getExtension(File f)
	{
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) 
		  return s.substring(i+1).toLowerCase();
		return "";
	}	
}