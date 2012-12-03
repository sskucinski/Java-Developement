import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class Decode_Encode_LSB {

	
	public static byte[] encode(BufferedImage image, String message, String key, String msgKey, String sciezka) throws Exception {
		
		byte[] IMGbytes = imageToByteArray(image);

		byte[] header = Arrays.copyOfRange(IMGbytes, 0, 54);
		byte[] pixels = Arrays.copyOfRange(IMGbytes, 54, IMGbytes.length);

		String encryptedMessage = KodowanieAES.encodeMessage(message, key, msgKey);

		byte[] messageBytes = stringToByteArrayWithLength(encryptedMessage);

		int arrPos = 0;
		// Rozpraszanie na pocz¹tku bitmapy
		for (int i = 0; i < 4; i++) {
			for (int bitPos = 7; bitPos >= 0; bitPos--) {
				byte bit = (byte) ((messageBytes[i] >> bitPos) & 0x1);
				pixels[arrPos] = (byte) ((pixels[arrPos] & 254) | bit);
				arrPos++;
			}
		}

		String klucz = KodowanieAES.generujKlucz(key, messageBytes.length - 4);
		
		byte[] scatteringKey = klucz.getBytes();
		assert scatteringKey.length == messageBytes.length - 4 : "Klucz nie jest tej samej d³ugoœci co wiadomoœæ";

		StringBuilder sb = new StringBuilder();
		int scatterPos = 0;
		for (int i = 4; i < messageBytes.length; i++) {
			arrPos += scatteringKey[scatterPos++];
			for (int bitPos = 7; bitPos >= 0; bitPos--) {
				byte bit = (byte) ((messageBytes[i] >> bitPos) & 0x1);
				sb.append(arrPos).append(" ");
				pixels[arrPos] = (byte) ((pixels[arrPos] & 254) | bit);
				arrPos++;
			}
		}

	

		byte[] modifiedImageBytes = new byte[header.length + pixels.length];
		System.arraycopy(header, 0, modifiedImageBytes, 0, header.length);
		System.arraycopy(pixels, 0, modifiedImageBytes, header.length,
				pixels.length);
		
		File f = new File(sciezka);
		FileOutputStream fos = new FileOutputStream(f);

		fos.write(modifiedImageBytes);
		fos.flush();
		fos.close();

		return modifiedImageBytes;
		
	}
	
	public static byte[] intToBytes(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public static int byteArrayToInt(byte[] array) {
		return ByteBuffer.wrap(array).getInt();
	}
	
	public static byte[] imageToByteArray(BufferedImage image)
			throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		ImageIO.write(image, "bmp", array);
		array.flush();

		byte[] bytes = array.toByteArray();
		array.close();
		return bytes;
	}
	
	public static byte[] stringToByteArrayWithLength(String s) {
		byte[] messsageLengthArray = intToBytes(s.length());
		byte[] message = s.getBytes();
		byte[] messageBytes = new byte[messsageLengthArray.length
				+ message.length];

		System.arraycopy(messsageLengthArray, 0, messageBytes, 0,
				messsageLengthArray.length);
		System.arraycopy(message, 0, messageBytes, messsageLengthArray.length,
				message.length);

		return messageBytes;
	}
	
	public static String decode(BufferedImage image, String key, String msgKey) throws Exception {

		byte[] bytes = imageToByteArray(image);
		byte[] rgbPixels = Arrays.copyOfRange(bytes, 54, bytes.length);

		byte[] lengthArray = new byte[4];
		for (int p = 0; p < 32; p++) {
			int pos = p / 8;
			byte bit = (byte) ((rgbPixels[p] >> 0) & 0x1);
			lengthArray[pos] = (byte) ((lengthArray[pos] << 1) | bit);
		}

		int length = ByteBuffer.wrap(lengthArray).getInt();
		
		byte[] retrievedMsg = new byte[length];

		String sc = KodowanieAES.generujKlucz(key, length);
		
		byte[] scatteringKey = sc.getBytes();

		assert length == scatteringKey.length : "Klucz nie jest tej samej d³ugoœci co wiadomoœæ";
		StringBuilder sb = new StringBuilder();
		int arrPos = 32;
		for (int p = 0; p < length * 8; p++) {
			int pos = p / 8;
			arrPos += (p % 8 == 0 ? scatteringKey[pos] : 0);
			sb.append(arrPos).append(" ");
			byte bit = (byte) ((rgbPixels[arrPos] >> 0) & 0x1);
			retrievedMsg[pos] = (byte) ((retrievedMsg[pos] << 1) | bit);
			arrPos++;
		}
				
		
		return KodowanieAES.decodeMessage(new String(retrievedMsg), key, msgKey);
	}

}
