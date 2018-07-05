package ru.argustelecom.box.env.barcode;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import static com.google.zxing.EncodeHintType.CHARACTER_SET;
import static com.google.zxing.EncodeHintType.ERROR_CORRECTION;
import static com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QrCodePreference extends BarcodePreference {

	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String DEFAULT_IMAGE_TYPE = "png";
	private static final ErrorCorrectionLevel DEFAULT_CORRECTION_LEVEL = M;

	public QrCodePreference(int width, int height) {
		setWidth(width);
		setHeight(height);

		initDefaultParams();
		fillHints();
	}

	public QrCodePreference(int width, int height, String imageType, ErrorCorrectionLevel correctionLevel,
			String charset) {
		super(width, height, imageType, QR_CODE, correctionLevel, charset);
	}

	@Override
	protected void fillHints() {
		getEncodeHints().put(ERROR_CORRECTION, getCorrectionLevel());
		getEncodeHints().put(CHARACTER_SET, DEFAULT_CHARSET);
	}

	private void initDefaultParams() {
		setImageType(DEFAULT_IMAGE_TYPE);
		setCharset(DEFAULT_CHARSET);
		setBarcodeFormat(QR_CODE);
		setCorrectionLevel(DEFAULT_CORRECTION_LEVEL);
	}

}