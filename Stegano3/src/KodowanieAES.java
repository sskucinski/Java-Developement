
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class KodowanieAES {
	static String kluczyk;

	public static String encodeMessage(final String message, final String key, final String msgKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		String password = new String(DigestUtils.sha512Hex(key.getBytes()))
				.substring(0, 16); // cannot generate key bigger than 128bits so
									// trim sha512 has to 16 bytes

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(password.getBytes(),
				"AES"));
		String test = generujWiadomosc(message, msgKey);
		byte[] encVal = cipher.doFinal(test.getBytes());

		String encryptedValue = new String(Base64.encodeBase64(encVal));

		return encryptedValue;
	}

	public static String decodeMessage(final String message, final String key, final String msgKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		String password = new String(DigestUtils.sha512Hex(key.getBytes()))
				.substring(0, 16); // cannot generate key bigger than 128bits so
									// trim sha512 has to 16 bytes

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(password.getBytes(),
				"AES"));

		byte[] decordedValue = Base64.decodeBase64(message.getBytes());

		byte[] decValue = cipher.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		String testOdkoduj = odkodujWiadomosc(decryptedValue, msgKey);

		return testOdkoduj;
	}
	
	public static String generujWiadomosc(final String wiadomosc, String klucz){
		kluczyk = new String(Base64.encodeBase64(klucz.getBytes()));
		String zmienionaWiadomosc = new String(Base64.encodeBase64(wiadomosc.getBytes()));
		String test = "";
		if(kluczyk.length() < zmienionaWiadomosc.length()){
			int lng = 0;
			lng = zmienionaWiadomosc.length() - kluczyk.length(); 
			for(int j=0; j<lng; j++){
				kluczyk+="0";
			}
		}
		
		
		for (int i=0; i<zmienionaWiadomosc.length(); i++){
			
				test += zmienionaWiadomosc.substring(i, i+1) + kluczyk.substring(i, i+1);
			
		}	
		System.out.println("Klucz: " + kluczyk);
		System.out.println("MSG: " + zmienionaWiadomosc);
		System.out.println("TEST: " + test);
		
		return test;
	}
	
	public static String odkodujWiadomosc (final String wiadomosc, String klucz){
		kluczyk = new String(Base64.encodeBase64(klucz.getBytes()));
		String zmienionaWiadomosc = wiadomosc;
		String test = "";
		
		
		if(kluczyk.length() < (zmienionaWiadomosc.length()/2)){
			int lng = 0;
			lng = (zmienionaWiadomosc.length()/2) - kluczyk.length(); 
			for(int j=0; j<lng; j++){
				kluczyk+="0";
			}
		}
		
		int j=1;
		for (int i=1; i<zmienionaWiadomosc.length(); i+=2){
			if(zmienionaWiadomosc.substring(i, i+1).equals(kluczyk.substring(j-1, j))){
				System.out.println("Loop if MSG: " + zmienionaWiadomosc.substring(i, i+1));
				System.out.println("Loop if Klucz: " + kluczyk.substring(j-1, j));
				
				test += zmienionaWiadomosc.substring(i-1, i);
				//zmienionaWiadomosc.substring(i, i+1).replace(zmienionaWiadomosc.substring(i, i+1), "");
			}
			j+=1;
		}
		System.out.println("Klucz: " + kluczyk);
		System.out.println("MSG: " + test);
		
		byte[] decordedValue = Base64.decodeBase64(test.getBytes());

		String decryptedValue = new String(decordedValue);
		
		return decryptedValue;
	}

	public static String generujKlucz(final String pass, int length) {

		String scatteringKey = new String(Base64.encodeBase64(DigestUtils
				.sha512(DigestUtils.sha512(pass.getBytes()))));

		int appender = '0';
		while (scatteringKey.length() < length) {
			scatteringKey += new String(
					Base64.encodeBase64(DigestUtils.sha512(scatteringKey
							+ Character.valueOf((char) appender))));
			if (++appender > '}') {
				appender = '0';
			}
		}

		return scatteringKey.substring(0, length);
	}

}
