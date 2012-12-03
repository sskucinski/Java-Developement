
public class Decode {
	static private byte[] kod = new byte[256];
	   static {
	      for (int i=0; i<256; i++) kod[i] = -1;
	      for (int i = 'A'; i <= 'Z'; i++) kod[i] = (byte)(     i - 'A');
	      for (int i = 'a'; i <= 'z'; i++) kod[i] = (byte)(26 + i - 'a');
	      for (int i = '0'; i <= '9'; i++) kod[i] = (byte)(52 + i - '0');
	      kod['+'] = 62;
	      kod['/'] = 63;
	   }
	   
// Dekodowanie
static public byte[] decode(char[] data)
{


int size = data.length;
for( int ix=0; ix<data.length; ix++ )
{
  if( (data[ix] > 255) || kod[ data[ix] ] < 0 )
     --size;  
}


int dlugosc = (size / 4) * 3;
if ((size % 4) == 3) dlugosc += 2;
if ((size % 4) == 2) dlugosc += 1;

byte[] out = new byte[dlugosc];



int przesuniecie = 0;  
int zbior = 0;  
int index = 0;


for (int ix=0; ix<data.length; ix++)
{
  int value = (data[ix]>255)? -1: kod[ data[ix] ];

  if ( value >= 0 )           
  {
 	zbior <<= 6;            
     przesuniecie += 6;             
     zbior |= value;         
     if ( przesuniecie >= 8 )       
     {
     	przesuniecie -= 8;         
        out[index++] =      
           (byte) ((zbior >> przesuniecie) & 0xff);
     }
  }
 
}


return out;
}

public static byte[] decode(String data) {
return decode(data.toCharArray());
}

}
