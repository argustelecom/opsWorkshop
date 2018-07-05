package ru.argustelecom.box.env.saldo.imp;

import static ru.argustelecom.box.env.saldo.imp.RegisterImportUtils.get;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.IMPOSSIBLE_DETERMINE_ACCOUNT;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.TRYING_TO_RE_IMPORT;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.saldo.imp.model.ClassProperty;
import ru.argustelecom.box.env.saldo.imp.model.FieldProperty;
import ru.argustelecom.box.env.saldo.imp.model.Register;
import ru.argustelecom.box.env.saldo.imp.model.RegisterContext;
import ru.argustelecom.box.env.saldo.imp.model.RegisterException;
import ru.argustelecom.box.env.saldo.imp.model.RegisterItem;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

public abstract class RegisterImportService implements Serializable {

	private static final long serialVersionUID = -398092218296333896L;

	@PersistenceContext
	protected EntityManager em;

	@Inject
	private TransactionRepository tr;

	public abstract void process(RegisterContext context, InputStream inputStream)
			throws RegisterException, IOException;

	protected abstract Map<Class, ClassProperty> getInspectMap();

	protected abstract String getPaymentDocSource(Register register);

	public void importing(RegisterContext context) {
		for (RegisterItem item : context.getRegister().getSuitableItems()) {
			PersonalAccount personalAccount = em.getReference(PersonalAccount.class, item.getAccountId());
			tr.createPaymentDocTransaction(personalAccount, item.getPaymentDocId(), item.getSum(),
					item.getPaymentDocNumber(), item.getPaymentDocDate(), getPaymentDocSource(context.getRegister()));
		}
	}

	protected void fillObjectFieldsFromLineTokens(Object object, String itemLine, String delimiter, int tokensCount)
			throws RegisterException {
		String[] tokens = itemLine.trim().split(delimiter);

		if (tokens.length != tokensCount) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			throw new RegisterException(messages.registerImportFileHasInvalidLines());
		}

		for (int partIndex = 0; partIndex < tokens.length; partIndex++) {
			FieldProperty fieldProperty = getInspectMap().get(object.getClass()).find(partIndex + 1);
			if (fieldProperty != null)
				ReflectionUtils.setFieldValue(object.getClass(), fieldProperty.getName(), object,
						get(fieldProperty, tokens[partIndex]));
		}
	}

	protected void checkRequiredData(List<RegisterItem> items) {
		int batchSize = 100;

		int from = 0;
		int to = batchSize;

		boolean readAll = false;
		List<RegisterItem> batch;

		while (!items.isEmpty() && !readAll) {
			if (to < items.size()) {
				batch = items.subList(from, to);
				markAsErrorIfDidNotFindAccount(batch);
				markAsErrorIfDuplicate(batch);
				from += batchSize;
				to += batchSize;
			} else {
				List<RegisterItem> lastBatch = items.subList(from, items.size());
				markAsErrorIfDidNotFindAccount(lastBatch);
				markAsErrorIfDuplicate(lastBatch);
				readAll = true;
			}
		}
	}

	private void markAsErrorIfDidNotFindAccount(List<RegisterItem> batch) {
		List<String> batchNumbers = new ArrayList<>();
		batch.forEach(item -> {
			if (!Strings.isNullOrEmpty(item.getAccountNumber())) {
				batchNumbers.add(item.getAccountNumber());
			}
		});

		Map<String, Long> foundAccounts = findAccounts(batchNumbers);

		batch.forEach(item -> {
			Long foundAccount = foundAccounts.get(item.getAccountNumber());
			if (foundAccount != null) {
				item.setAccountId(foundAccount);
			} else {
				item.addError(IMPOSSIBLE_DETERMINE_ACCOUNT);
			}
		});
	}

	private static final String FIND_ACCOUNTS = "RegisterImportService.findAccounts";

	@SuppressWarnings("unchecked")
	@NamedQuery(name = FIND_ACCOUNTS, query = "select pa.id, pa.number from PersonalAccount pa where pa.number in (:numbers)")
	private Map<String, Long> findAccounts(List<String> numbers) {
		if (!numbers.isEmpty()) {
			List<Object[]> qr = em.createNamedQuery(FIND_ACCOUNTS).setParameter("numbers", numbers).getResultList();
			return qr.stream().collect(Collectors.toMap(account -> account[1].toString(),
					account -> Long.parseLong(account[0].toString())));
		}
		return Collections.emptyMap();
	}

	private void markAsErrorIfDuplicate(List<RegisterItem> batch) {
		List<String> paymentDocIds = new ArrayList<>();
		batch.forEach(item -> {
			if (item.getErrors().isEmpty()) {
				String paymentDocId = tr.generatePaymentDocId(item.getAccountId(), item.getPaymentDocNumber(),
						item.getPaymentDocDate(), item.getSum());
				item.setPaymentDocId(paymentDocId);
				paymentDocIds.add(paymentDocId);
			}
		});

		if (!paymentDocIds.isEmpty()) {
			List<String> foundPaymentDocIds = tr.findSameTransactions(paymentDocIds);
			batch.stream().filter(item -> foundPaymentDocIds.contains(item.getPaymentDocId()))
					.forEach(item -> item.addError(TRYING_TO_RE_IMPORT));
		}
	}

}