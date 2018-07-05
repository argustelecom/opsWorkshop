package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceRepository;
import ru.argustelecom.box.env.privilege.PrivilegeRepository;
import ru.argustelecom.box.env.privilege.discount.DiscountRepository;

public abstract class AbstractAccountingServiceTest extends AbstractAccountingTest {

	@Mock
	protected PrivilegeRepository privilegeRp;

	@Mock
	protected DiscountRepository discountsRp;

	@Mock
	protected LongTermInvoiceRepository invoiceRp;

	@InjectMocks
	protected SubscriptionAccountingServiceImpl accountingSvc;

	@Before
	@Override
	public void setup() {
		MockitoAnnotations.initMocks(this);
		super.setup();
	}

}
