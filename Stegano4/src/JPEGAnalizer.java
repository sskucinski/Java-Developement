import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class JPEGAnalizer {

	private BufferedImage BMP = null;
	private BufferedImage convBMP = null;

	private List<Point> pixels = new ArrayList<Point>();

	public JPEGAnalizer(File f) throws IOException {
		BMP = ImageIO.read(f);

		BufferedImage jpeg = doJPEG(BMP);
		convBMP = convertToBMP(jpeg);

		sprawdzRoznice();
		oznacz();

	}

	public JPEGAnalizer(String path) throws IOException {
		this(new File(path));
	}

	public JPEGAnalizer(InputStream is) throws IOException {
		BMP = ImageIO.read(is);

		BufferedImage jpeg = doJPEG(BMP);
		convBMP = convertToBMP(jpeg);

		sprawdzRoznice();

	}

	public int getPixelsCount() {
		return this.pixels.size();
	}

	public BufferedImage getBMPImg() {
		return convBMP;
	}

	private BufferedImage doJPEG(BufferedImage bmp) throws IOException {

		File jpg = File.createTempFile("bmp2jpg", ".jpg", new File("."));
		if (!ImageIO.write(bmp, "jpg", jpg)) {
			throw new IOException("Problem with converting bmp to jpg");
		}

		return ImageIO.read(jpg);
	}

	private BufferedImage convertToBMP(final BufferedImage jpg)
			throws IOException {

		File bmp = File.createTempFile("jpg2bmp", ".bmp", new File("."));
		if (!ImageIO.write(jpg, "bmp", bmp)) {
			throw new IOException("Nie mo¿na zamieniæ do JPEG");
		}

		return ImageIO.read(bmp);
	}

	private void sprawdzRoznice() {

		int width = BMP.getWidth();
		int height = BMP.getHeight();

		int w = convBMP.getWidth();
		int h = convBMP.getHeight();

		System.out.println("Oryginalny BMP: " + width + ", " + height);
		System.out.println("Zmieniony BMP: " + w + ", " + h);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color orig = new Color(BMP.getRGB(x, y));
				Color conv = new Color(convBMP.getRGB(x, y));

				if (!orig.equals(conv)) {
					pixels.add(new Point(x, y));
				}

			}
		}

	}

	private void oznacz() {

		for (Point p : pixels) {
			convBMP.setRGB(p.x, p.y, Color.RED.getRGB());
		}

	}

}