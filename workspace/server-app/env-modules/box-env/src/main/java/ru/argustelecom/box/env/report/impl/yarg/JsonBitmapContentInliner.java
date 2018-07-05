package ru.argustelecom.box.env.report.impl.yarg;

import java.util.Base64;

import com.haulmont.yarg.formatters.impl.inline.BitmapContentInliner;

import net.minidev.json.JSONArray;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.system.inf.exception.SystemException;

public class JsonBitmapContentInliner extends BitmapContentInliner {

	@Override
	protected byte[] getContent(Object paramValue) {
		if (paramValue instanceof String) {
			return extractContentFromBase64String((String) paramValue);
		}
		if (paramValue instanceof JSONArray) {
			return extractContentFromJsonArray((JSONArray) paramValue);
		}
		return super.getContent(paramValue);
	}

	private byte[] extractContentFromBase64String(String base64String) {
		if (ReportDataImage.isRFC2397Uri(base64String)) {
			return ReportDataImage.getContentFromRFC2397Uri(base64String);
		}
		return Base64.getDecoder().decode(base64String);
	}

	private byte[] extractContentFromJsonArray(JSONArray jsonArray) {
		byte[] array = new byte[jsonArray.size()];
		int index = 0;

		for (Object item : jsonArray) {
			if (item == null) {
				array[index++] = 0;
			} else if (item instanceof Byte) {
				array[index++] = ((Byte) item).byteValue();
			} else if (item instanceof Integer) {
				array[index++] = ((Integer) item).byteValue();
			} else if (item instanceof Long) {
				array[index++] = ((Long) item).byteValue();
			} else {
				throw new SystemException("Unexpected element class of JsonArray: " + item.getClass());
			}
		}

		return array;
	}

}
