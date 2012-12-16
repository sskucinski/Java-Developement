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
		
		byte[] bytes = imageToByteArray(image);

		byte[] head = Arrays.copyOfRange(bytes, 0, 54);
		byte[] rgbPixels = Arrays.copyOfRange(bytes, 54, bytes.length);


		// encrypt message with aes
		String encryptedMessage = KodowanieAES.encodeMessage(message, key, msgKey);

		byte[] encryptedMessageBytes = encryptedMessage.getBytes();
		
		byte[] encodedWithHamming = Hamming.convertToHamming(encryptedMessageBytes);
		
		byte[] messageBytes = getByteArrayToHideInImage(encodedWithHamming);

		int arrPos = 0;
		
		// encode message length at the beginning of bitmap
		for (int i = 0; i < 4; i++) {
			for (int bitPos = 7; bitPos >= 0; bitPos--) {
				byte bit = (byte) ((messageBytes[i] >> bitPos) & 0x1);
				rgbPixels[arrPos] = (byte) ((rgbPixels[arrPos] & 254) | bit);
				arrPos++;
			}
		}

		String sc = KodowanieAES.generujKlucz(key, messageBytes.length - 4);

		byte[] scatteringKey = sc.getBytes();

		StringBuilder sb = new StringBuilder("Writing pixels: ");

		int pixelShift = rgbPixels.length / (messageBytes.length * 3) - 1;

		int scatterPos = 0;
		for (int i = 4; i < messageBytes.length; i++) {
			arrPos += scatteringKey[scatterPos++] + pixelShift;
			for (int bitPos = 7; bitPos >= 0; bitPos--) {
				byte bit = (byte) ((messageBytes[i] >> bitPos) & 0x1);
				sb.append(arrPos).append(" ");
				rgbPixels[arrPos] = (byte) ((rgbPixels[arrPos] & 254) | bit);
				arrPos++;
			}
		}

		byte[] modifiedImageBytes = new byte[head.length + rgbPixels.length];
		System.arraycopy(head, 0, modifiedImageBytes, 0, head.length);
		System.arraycopy(rgbPixels, 0, modifiedImageBytes, head.length,
				rgbPixels.length);
		
		File f = new File(sciezka);
		FileOutputStream fos = new FileOutputStream(f);

		fos.write(modifiedImageBytes);
		fos.flush();
		fos.close();

		return modifiedImageBytes;
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

		StringBuilder sb = new StringBuilder("Reading pixels: ");

		int pixelShift = rgbPixels.length / ((length + 4) * 3) - 1;

		int arrPos = 32;
		for (int p = 0; p < length * 8; p++) {
			int pos = p / 8;
			arrPos += (p % 8 == 0 ? scatteringKey[pos] + pixelShift : 0);
			sb.append(arrPos).append(" ");
			byte bit = (byte) ((rgbPixels[arrPos] >> 0) & 0x1);
			retrievedMsg[pos] = (byte) ((retrievedMsg[pos] << 1) | bit);
			arrPos++;
		}

		byte[] decodedFromHamming = Hamming.convertFromHamming(retrievedMsg);

		String decodeMessage = KodowanieAES.decodeMessage(new String(
				decodedFromHamming), key, msgKey);

		return decodeMessage;

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
	
	public static byte[] getByteArrayToHideInImage(byte[] message) {

		byte[] messsageLengthArray = intToBytes(message.length);
		byte[] messageBytes = new byte[messsageLengthArray.length
				+ message.length];

		System.arraycopy(messsageLengthArray, 0, messageBytes, 0,
				messsageLengthArray.length);
		System.arraycopy(message, 0, messageBytes, messsageLengthArray.length,
				message.length);

		return messageBytes;

	}

}
