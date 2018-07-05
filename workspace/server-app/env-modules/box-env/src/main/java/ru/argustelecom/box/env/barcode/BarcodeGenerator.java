package ru.argustelecom.box.env.barcode;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationService
public class BarcodeGenerator {

	public ByteArrayOutputStream generate(BarcodeDataFormatter formatter, BarcodePreference preference) {
		checkNotNull(formatter);
		checkNotNull(preference);

		//FIXME сделать decode (Apache Commons, Google Guava ???)
		String formattedData = formatter.getFormattedData();
		BitMatrix matrix;
		try {
			//@formatter:off
			matrix = new MultiFormatWriter().encode
					(
							formattedData,
							preference.getBarcodeFormat(),
							preference.getWidth(),
							preference.getHeight(),
							preference.getEncodeHints()
					);
			//@formatter:on
		} catch (WriterException we) {
			throw new SystemException("Could not generate the barcode", we);
		}

		ByteArrayOutputStream qrCodeOutputStream = new ByteArrayOutputStream();
		try {
			MatrixToImageWriter.writeToStream(matrix, preference.getImageType(), qrCodeOutputStream);
		} catch (IOException ex) {
			throw new SystemException("Could not write the barcode to stream", ex);
		}
		return qrCodeOutputStream;
	}

}