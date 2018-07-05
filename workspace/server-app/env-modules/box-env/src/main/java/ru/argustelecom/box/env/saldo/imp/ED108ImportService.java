package ru.argustelecom.box.env.saldo.imp;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.LineReader;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.saldo.imp.model.ClassProperty;
import ru.argustelecom.box.env.saldo.imp.model.ED108Item;
import ru.argustelecom.box.env.saldo.imp.model.ED108Register;
import ru.argustelecom.box.env.saldo.imp.model.Register;
import ru.argustelecom.box.env.saldo.imp.model.RegisterContext;
import ru.argustelecom.box.env.saldo.imp.model.RegisterException;
import ru.argustelecom.box.env.saldo.imp.model.RegisterItem;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.chrono.ChronoUtils;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;

@Named
@ApplicationService
public class ED108ImportService extends RegisterImportService {

	private static final long serialVersionUID = 7840816549085043364L;

	private static final String DELIMITER = ";";
	private static final int ITEM_TOKENS_COUNT = 12;
	private static final int PROPERTY_TOKENS_COUNT = 6;

	private static final Map<Class, ClassProperty> inspectMap = new HashMap<>();

	static {
		inspectMap.put(ED108Register.class, new ClassProperty(ED108Register.class));
		inspectMap.put(ED108Item.class, new ClassProperty(ED108Item.class));
	}

	@Override
	public void process(RegisterContext context, InputStream inputStream) throws RegisterException, IOException {
		context.getRegister().setCharset(detectEncoding(inputStream));

		processRegister(context, inputStream);
		fillDataFromPropertyIntoItems(context);
		fillItemsPaymentDocDate(context);

		context.getItems().forEach(RegisterItem::checkRawData);
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
			paymentDocSource = messages.ed108registerImport(register.getNumber());
		}
		return paymentDocSource;
	}

	private LineReader createLineReader(InputStream inputStream, String encoding) throws RegisterException {
		try {
			return new LineReader(new InputStreamReader(inputStream, encoding));
		} catch (IOException ex) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			throw new RegisterException(messages.notKoi8REncoding());
		}
	}

	private void processRegister(RegisterContext context, InputStream inputStream)
			throws IOException, RegisterException {
		LineReader reader = createLineReader(inputStream, context.getRegister().getCharset());
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (Strings.isNullOrEmpty(currentLine))
				continue;

			if (currentLine.startsWith("=") && !hasItems(context)) {
				processPropertyLine(context, currentLine);
				break;
			}

			processItemLine(context, currentLine);
		}

		if (currentLine == null) {
			OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			throw new RegisterException(messages.attemptToDownloadEmptyFile());
		}

	}

	private void processPropertyLine(RegisterContext context, String propertyLine) throws RegisterException {
		if (isEmpty(propertyLine.trim()))
			return;

		fillObjectFieldsFromLineTokens(context.getRegister(), propertyLine.trim().substring(1), DELIMITER,
				PROPERTY_TOKENS_COUNT);
	}

	private void processItemLine(RegisterContext context, String itemLine) throws RegisterException {
		if (isEmpty(itemLine.trim()))
			return;

		ED108Item item = new ED108Item(itemLine);
		fillObjectFieldsFromLineTokens(item, itemLine, DELIMITER, ITEM_TOKENS_COUNT);
		context.getItems().add(item);
	}

	private void fillDataFromPropertyIntoItems(RegisterContext context) {
		context.getItems().forEach(item -> item.setPaymentDocNumber(context.getRegister().getNumber()));
	}

	private void fillItemsPaymentDocDate(RegisterContext context) {
		context.getItems().forEach(item -> {
			ED108Item ed108Item = (ED108Item) item;
			if (ed108Item.getPaymentDate() != null && ed108Item.getPaymentTime() != null) {
				LocalDateTime localDateTime = LocalDateTime.of(ed108Item.getPaymentDate(), ed108Item.getPaymentTime());
				item.setPaymentDocDate(ChronoUtils.fromLocalDateTime(localDateTime));
			}
		});
	}

	private static final List<String> possibleEncodings = Lists.newArrayList("UTF-8", "windows-1251", "KOI8-R");

	private String detectEncoding(InputStream inputStream) throws IOException {
		CharsetDetector detector = new CharsetDetector();
		detector.setText(inputStream);
		String encoding = detector.detect().getName();

		if (possibleEncodings.contains(encoding)) {
			return encoding;
		} else {
			CharsetMatch[] allMatches = detector.detectAll();
			for (CharsetMatch charsetMatch : allMatches) {
				if (possibleEncodings.contains(charsetMatch.getName())) {
					return charsetMatch.getName();
				}
			}
		}
		return StringUtils.EMPTY;
	}

	private boolean hasItems(RegisterContext context) throws RegisterException {
		if (context.getItems() == null || context.getItems().isEmpty()) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			throw new RegisterException(messages.registerImportPaymentDocumentNotFound());
		}
		return false;
	}

}