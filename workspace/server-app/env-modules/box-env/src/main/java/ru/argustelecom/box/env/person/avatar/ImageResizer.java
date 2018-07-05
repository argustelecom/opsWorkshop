package ru.argustelecom.box.env.person.avatar;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;

@RequestScoped
public class ImageResizer implements Serializable {

	private static final long serialVersionUID = -8672140684577914965L;

	public InputStream resize(InputStream imageInputStream, String formatName, int scaledWidth, int scaledHeight)
			throws IOException {
		BufferedImage originalImage = ImageIO.read(imageInputStream);
		if (originalImage.getWidth() == scaledWidth && originalImage.getHeight() == scaledHeight) {
			return imageInputStream;
		}

		BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.dispose();

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ImageIO.write(scaledImage, formatName, os);
			return new ByteArrayInputStream(os.toByteArray());
		}
	}
}
