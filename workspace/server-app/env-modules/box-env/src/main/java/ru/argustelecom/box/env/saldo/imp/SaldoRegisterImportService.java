package ru.argustelecom.box.env.saldo.imp;

import static ru.argustelecom.box.env.saldo.imp.RegisterImportUtils.get;
import static ru.argustelecom.box.env.saldo.imp.model.SaldoRegister.DEFAULT_CHARSET;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.LineReader;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.saldo.imp.model.ClassProperty;
import ru.argustelecom.box.env.saldo.imp.model.FieldProperty;
import ru.argustelecom.box.env.saldo.imp.model.Register;
import ru.argustelecom.box.env.saldo.imp.model.RegisterContext;
import ru.argustelecom.box.env.saldo.imp.model.RegisterException;
import ru.argustelecom.box.env.saldo.imp.model.SaldoRegister;
import ru.argustelecom.box.env.saldo.imp.model.SaldoRegisterItem;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

@Named
@ApplicationService
public class SaldoRegisterImportService extends RegisterImportService {

	private static final long serialVersionUID = -585815721455530151L;

	private static final String DELIMITER = ";";
	private static final int ITEM_TOKENS_COUNT = 10;

	private static final Map<Class, ClassProperty> inspectMap = new HashMap<>();

	static {
		inspectMap.put(SaldoRegister.class, new ClassProperty(SaldoRegister.class));
		inspectMap.put(SaldoRegisterItem.class, new ClassProperty(SaldoRegisterItem.class));
	}

	@Override
	public void process(RegisterContext context, InputStream inputStream) throws RegisterException, IOException {
		LineReader reader = createLineReader(inputStream);

		processHeader(context, reader);
		processBody(context, reader);

		checkRequiredData(context.getItems());
		context.getRegister().fillContainers(context.getItems());
		context.getItems().clear();
	}

	@Override
	protected Map<Class, ClassProperty> getInspectMap() {
		return inspectMap;
	}

	private String paymentDocSource;

	@Override
	protected String getPaymentDocSource(Register register) {
		if (paymentDocSource == null) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			paymentDocSource = messages.saldoRegisterImport(register.getNumber());
		}
		return paymentDocSource;
	}

	private LineReader createLineReader(InputStream inputStream) throws RegisterException {
		try {
			return new LineReader(new InputStreamReader(inputStream, DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException uee) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			throw new RegisterException(messages.notWin1251Encoding());
		}
	}

	private void processHeader(RegisterContext context, LineReader reader) throws RegisterException, IOException {
		String currentLine;
		int propertyIndex = 0;
		OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);

		while ((currentLine = reader.readLine()) != null && (currentLine.startsWith("#") || currentLine.isEmpty())) {
			if (currentLine.isEmpty()) {
				throw new RegisterException(messages.attemptToDownloadEmptyFile());
			}

			propertyIndex++;
			processPropertyLine(context, currentLine, propertyIndex);
		}

		if (currentLine == null)
			throw new RegisterException(messages.attemptToDownloadEmptyFile());

		checkHeader(context.getRegister());
		processItemLine(context, currentLine);
	}

	private void checkHeader(Register register) throws RegisterException {
		String checkHeaderMsg = register.checkParams();
		if (!checkHeaderMsg.isEmpty()) {
			throw new RegisterException(checkHeaderMsg);
		}
	}

	private void processBody(RegisterContext context, LineReader reader) throws RegisterException, IOException {
		String currentLine;

		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.isEmpty() && reader.readLine() != null) {
				SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
				throw new RegisterException(messages.registerImportFileHasEmptyLines());
			}

			processItemLine(context, currentLine);
		}
		hasItems(context);
	}

	private void hasItems(RegisterContext context) throws RegisterException {
		if (context.getItems().isEmpty()) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			throw new RegisterException(messages.registerImportPaymentDocumentNotFound());
		}
	}

	private void processPropertyLine(RegisterContext context, String property, int index) {
		String propertyValue = property.substring(1).trim();

		FieldProperty fieldProperty = inspectMap.get(SaldoRegister.class).find(index);
		if (fieldProperty != null)
			ReflectionUtils.setFieldValue(SaldoRegister.class, fieldProperty.getName(), context.getRegister(),
					get(fieldProperty, propertyValue));
	}

	private void processItemLine(RegisterContext context, String itemLine) throws RegisterException {
		if (StringUtils.isEmpty(itemLine.trim()))
			return;

		SaldoRegisterItem saldoItem = new SaldoRegisterItem(itemLine.trim());
		fillObjectFieldsFromLineTokens(saldoItem, itemLine, DELIMITER, ITEM_TOKENS_COUNT);
		saldoItem.checkRawData();
		context.getItems().add(saldoItem);
	}

}