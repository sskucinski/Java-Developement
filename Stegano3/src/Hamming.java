
public class Hamming {
	
	public final static byte[] convertToHamming(byte[] message) {

		byte[] hamm = encodeByteArrayWithHamming(message);
		
		byte[] converted = new byte[hamm.length / 8];

		for (int i = 0; i < hamm.length; i++) {
			int p = i / 8;
			converted[p] = (byte) (converted[p] << 1 | hamm[i]);
		}

		return converted;

	}

	public final static byte[] convertFromHamming(byte[] hammingEncodedMessage) {

		byte[] hamming = getHammingFromArray(hammingEncodedMessage);
		byte[] bits = new byte[hamming.length / 5];
		
		for(int i = 0; i < hamming.length; i+=5) {
			byte [] t = new byte[5];
			System.arraycopy(hamming, i, t, 0, t.length);
			bits[i/5] = hammingToBit(t);
		}
		
		byte[] before = new byte[bits.length/8];
		for(int i = 0; i < bits.length; i++) {
			int pos = i/8;
			before[pos] =  (byte)(before[pos] << 1 | bits[i]);
		}
		
		return before;
	}

	public final static byte hammingToBit(byte[] hamm) {

		if (hamm.length != 5)
			throw new IllegalArgumentException("Byte array must be of length 5");

		byte s = 0;
		for (int i = 0; i < hamm.length; i++) {
			s += hamm[i];
		}

		if (s >= 3) {
			return 1;
		} else {
			return 0;
		}
	}

	public final static byte[] bitToHamming(byte b) {

		if (b == 1)
			return new byte[] { 1, 1, 1, 1, 1 };
		else if (b == 0)
			return new byte[] { 0, 0, 0, 0, 0 };
		else
			throw new IllegalArgumentException("Byte value is not '0' or '1': "
					+ b);
	}

	private static byte[] encodeByteArrayWithHamming(byte[] message) {
		byte[] hamm = new byte[message.length * 8 * 5];

		int pos = 0;
		final int length = 5;

		for (int i = 0; i < message.length; i++) {
			for (int bitPos = 7; bitPos >= 0; bitPos--) {
				byte bit = (byte) ((message[i] >> bitPos) & 0x1);
				byte[] converted = bitToHamming(bit);
				System.arraycopy(converted, 0, hamm, pos, length);
				pos += 5;
			}
		}
		return hamm;
	}

	private static byte[] getHammingFromArray(byte[] hammingEncodedMessage) {

		byte[] h = new byte[hammingEncodedMessage.length * 8];
		int hPos = 0;
		for (int i = 0; i < hammingEncodedMessage.length; i++) {
			for (int bitPos = 7; bitPos >= 0; bitPos--) {

				byte bit = (byte) ((hammingEncodedMessage[i] >> bitPos) & 0x1);
				h[hPos] = bit;
				hPos++;

			}
		}

		return h;
	}

}
