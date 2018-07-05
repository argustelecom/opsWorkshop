package ru.argustelecom.box.env.saldo.export;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.account.model.PersonalAccountState.ACTIVE;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionChargesService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Stateless
public class SaldoExportService implements Serializable {

	private static final long serialVersionUID = 3549020879191268645L;

	private static final String FIND_PERSONAL_ACCOUNTS = "SaldoExportService.findPersonalAccounts";

	private static final String SALDO_HEADER_PATTERN = "#%s %s\n";
	private static final String SALDO_ROW_PATTERN = "%s;%s;%s;%s;\n";
	private static final String SALDO_ROW_ADDRESS_PATTERN = "%s,%s,%s";

	private static final String DEFAULT_TYPE = "7";

	@Inject
	private SubscriptionChargesService subscriptionChargesService;

	@Inject
	private SaldoExportIssueRepository saldoExportIssueRepository;

	@Inject
	private PersonalAccountBalanceService balanceService;

	@PersistenceContext
	private EntityManager em;

	public void export(@NotNull SaldoExportIssue issue) throws Exception {
		// TODO
		/*
		 * Blob result = generateExportResult(issue); saldoExportIssueRepository.saveFile(issue, result);
		 */
	}

	private Blob generateExportResult(@NotNull SaldoExportIssue issue) throws Exception {
		Money totalSaldo = Money.ZERO;
		StringBuilder body = new StringBuilder();
		List<PersonalAccount> personalAccounts = findPersonalAccounts();
		for (PersonalAccount personalAccount : personalAccounts) {
			Range<LocalDateTime> period = Range.closed(toLocalDateTime(issue.getFrom()),
					toLocalDateTime(issue.getTo()));
			Map<Subscription, Money> subscriptionsAmount = subscriptionChargesService
					.getSubscriptionsCharges(personalAccount, period);
			if (!subscriptionsAmount.isEmpty()) {
				String name = generateName(personalAccount);
				Subscription anySubscription = subscriptionsAmount.keySet().iterator().next();
				Location location = anySubscription.getLocations().iterator().next();
				String address = generateAddress(location);
				String personalAccountNumber = personalAccount.getNumber();
				Money saldo = Money.ZERO;
				for (Money amount : subscriptionsAmount.values()) {
					saldo = saldo.add(amount);
				}
				saldo = saldo.subtract(balanceService.getAvailableBalance(personalAccount));
				body.append(String.format(SALDO_ROW_PATTERN, name, address, personalAccountNumber,
						getSaldoFormatter().format(saldo.getRoundAmount())));
				totalSaldo = totalSaldo.add(saldo);
			}
		}

		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		StringBuilder result = new StringBuilder();
		result.append(String.format(SALDO_HEADER_PATTERN, "FILESUM",
				getSaldoFormatter().format(totalSaldo.getRoundAmount())));
		result.append(String.format(SALDO_HEADER_PATTERN, "TYPE", DEFAULT_TYPE));
		result.append(String.format(SALDO_HEADER_PATTERN, "NOTE", messages.defaultNote()));
		result.append(body);

		return new SerialBlob(result.toString().getBytes(Charset.forName("cp1251")));
	}

	@NamedQuery(name = FIND_PERSONAL_ACCOUNTS, query = "select pa from PersonalAccount pa inner join pa.customer c inner join c.party p "
			+ "where pa.state = :personalAccountActive and type(p) = 'Person'")
	private List<PersonalAccount> findPersonalAccounts() {
		EntityGraph<?> personalAccountEntityGraph = em.getEntityGraph(PersonalAccount.FOR_BILL_GRAPH_NAME);
		return em.createNamedQuery(FIND_PERSONAL_ACCOUNTS, PersonalAccount.class)
				.setParameter("personalAccountActive", ACTIVE)
				.setHint("javax.persistence.loadgraph", personalAccountEntityGraph).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	private String generateName(PersonalAccount personalAccount) {
		Party party = personalAccount.getCustomer().getParty();

		checkState(party instanceof Person);
		Person person = (Person) party;
		return person.getName().shortName(true);
	}

	private String generateAddress(Location location) {
		if (!(location instanceof Lodging || location instanceof Building)) {
			return StringUtils.EMPTY;
		}

		Lodging lodging = null;
		Building building = null;
		if (location instanceof Lodging) {
			lodging = (Lodging) location;
			if (!(EntityManagerUtils.initializeAndUnproxy(location.getParent()) instanceof Building)) {
				return StringUtils.EMPTY;
			}

			building = (Building) EntityManagerUtils.initializeAndUnproxy(location.getParent());
		} else {
			if (location instanceof Building) {
				building = (Building) location;
			} else {
				return StringUtils.EMPTY;
			}
		}

		if (!(EntityManagerUtils.initializeAndUnproxy(building.getParent()) instanceof Street)) {
			return StringUtils.EMPTY;
		}
		Street street = (Street) EntityManagerUtils.initializeAndUnproxy(building.getParent());

		if (!(EntityManagerUtils.initializeAndUnproxy(street.getParent()) instanceof Region)) {
			return StringUtils.EMPTY;
		}
		Region region = (Region) EntityManagerUtils.initializeAndUnproxy(street.getParent());

		String result = String.format(SALDO_ROW_ADDRESS_PATTERN, getRegionName(region), getStreetName(street),
				getBuildingName(building));
		if (lodging != null) {
			result = result + "," + getLodgingName(lodging);
		}

		return result;
	}

	private String getRegionName(Region region) {
		StringBuilder result = new StringBuilder();
		result.append(region.getName());
		if (!region.getType().getName().equalsIgnoreCase("город")) {
			result.append(" ").append(region.getType().getShortName());
		}
		return result.toString();
	}

	private String getStreetName(Street street) {
		StringBuilder result = new StringBuilder();
		result.append(street.getName()).append(" ").append(street.getType().getShortName());
		return result.toString();
	}

	private String getBuildingName(Building building) {
		StringBuilder result = new StringBuilder(10);
		result.append(building.getNumber());
		if (building.getCorpus() != null) {
			result.append("K").append(building.getCorpus());
		}
		if (building.getWing() != null) {
			result.append("СТР").append(building.getWing());
		}
		return result.toString();
	}

	private String getLodgingName(Lodging lodging) {
		return lodging.getNumber();
	}

	private DecimalFormat getSaldoFormatter() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(false);
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);
		return df;
	}

}
