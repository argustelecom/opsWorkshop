package ru.argustelecom.box.env.barcode;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BarcodePreference {

	private int width;
	private int height;
	private String imageType;
	private BarcodeFormat barcodeFormat;
	private ErrorCorrectionLevel correctionLevel;
	private String charset;

	private Map<EncodeHintType, Object> encodeHints = new HashMap<>();

	public BarcodePreference(int width, int height, String imageType, BarcodeFormat barcodeFormat,
			ErrorCorrectionLevel correctionLevel, String charset) {
		checkNotNull(imageType);
		checkNotNull(barcodeFormat);
		checkNotNull(correctionLevel);
		checkNotNull(charset);

		this.width = width;
		this.height = height;
		this.imageType = imageType;
		this.barcodeFormat = barcodeFormat;
		this.correctionLevel = correctionLevel;
		this.charset = charset;

		fillHints();
	}

	protected abstract void fillHints();

}