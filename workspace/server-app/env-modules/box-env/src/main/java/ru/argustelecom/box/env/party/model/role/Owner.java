package ru.argustelecom.box.env.party.model.role;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.env.party.model.role.Owner.Characteristic.PARAMETERS;
import static ru.argustelecom.box.env.party.model.role.Owner.Characteristic.PROPERTIES;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;

/**
 * Представляет <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=3539371">владельца</a> или
 * "Юридическое лицо компании"
 */
@Entity
@Table(schema = "system")
@Access(FIELD)
@NoArgsConstructor(access = PROTECTED)
public class Owner extends PartyRole implements Printable {

	public static final BigDecimal MIN_TAX_RATE = BigDecimal.ZERO;
	public static final BigDecimal MAX_TAX_RATE = BigDecimal.valueOf(100);
	public static final String QR_CODE_PATTERN_ENABLED = "box.qr-code-pattern";
	public static final String QR_CODE_PARAMETER_DELIMITER = ",";
	public static final Pattern QR_CODE_PARAMETER = compile("^(\\w+)=\\{(\\w+).(\\w+)}$");

	private static final Map<Characteristic, Function<Owner, Map<String, String>>> mapping;

	static {
		Map<Characteristic, Function<Owner, Map<String, String>>> map = new EnumMap<>(Characteristic.class);
		map.put(PROPERTIES, owner -> owner.getParty().getTypeInstance().getPropertyValueMap());
		map.put(PARAMETERS, owner -> owner.getAdditionalParameters().stream()
				.collect(toMap(OwnerParameter::getKeyword, OwnerParameter::getValue)));
		mapping = unmodifiableMap(map);
	}

	/**
	 * Признак юридического лица по-умолчанию. Для изменения используйте
	 * {@link ru.argustelecom.box.env.party.OwnerRepository#makePrincipal}
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	private boolean principal;

	/**
	 * Ставка НДС. Измеряется от 0 до 100
	 */
	@Getter
	@Column(precision = 4, scale = 2, nullable = false, columnDefinition = "system.RATE")
	private BigDecimal taxRate;

	/**
	 * Название E-mail шаблона
	 */
	@Getter
	@Setter
	@Column(length = 128)
	private String emailTemplateName;

	/**
	 * Шаблон E-mail
	 */
	@Getter
	@Setter
	@Lob
	private Blob emailTemplate;

	/**
	 * Список дополнительных параметров
	 */
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "owner_id")
	private List<OwnerParameter> additionalParameters = newArrayList();

	/**
	 * Шаблон для генерации QR-кода
	 */
	@Getter
	@Column(nullable = true)
	private String qrCodePattern;

	@Transient
	private List<OwnerParameter> cachedAdditionalParameters;

	public Owner(Long id, BigDecimal taxRate) {
		super(id);
		checkState(taxRate != null && taxRate.compareTo(MIN_TAX_RATE) >= 0 && taxRate.compareTo(MAX_TAX_RATE) <= 0);
		this.taxRate = taxRate;
	}

	@Override
	public String getObjectName() {
		return getParty().getObjectName();
	}

	@Override
	public Company getParty() {
		return (Company) super.getParty();
	}

	@Override
	public void setParty(Party party) {
		checkArgument(party instanceof Company, "Owner's party must be company instance");
		super.setParty(party);
	}

	@Override
	public OwnerRdo createReportData() {
		Map<String, String> params = additionalParameters.stream()
				.collect(toMap(OwnerParameter::getKeyword, param -> ofNullable(param.getValue()).orElse(EMPTY)));
		return new OwnerRdo(getId(), getParty().createReportData(), params);
	}

	public List<OwnerParameter> getAdditionalParameters() {
		if (cachedAdditionalParameters == null) {
			cachedAdditionalParameters = unmodifiableList(additionalParameters);
		}
		return cachedAdditionalParameters;
	}

	public boolean removeAdditionalParameter(OwnerParameter parameter) {
		checkNotNull(parameter);

		boolean removed = additionalParameters.remove(parameter);
		if (removed) {
			additionalParameters.stream().filter(param -> param.getOrdinal() > parameter.getOrdinal())
					.forEach(param -> param.setOrdinal(param.getOrdinal() - 1));
			evictCachedAdditionalParameters();
		}
		return removed;
	}

	public void setQrCodePattern(String qrCodePattern) {
		this.qrCodePattern = qrCodePattern;
	}

	/**
	 * Возвращает ассоциативный массив, ключем которого является ключевое слово, а значением - значение для данного
	 * ключевого слова
	 * 
	 * @param characteristic
	 *            характеристика, для которой нужно получить ассоциативный массив
	 * @return ассоциативный массив
	 */
	public Map<String, String> getCharacteristics(Characteristic characteristic) {
		return mapping.get(checkNotNull(characteristic)).apply(this);
	}

	/**
	 * Возвращает значение для указаных параметров
	 * 
	 * @param characteristic
	 *            характеристика владельца, из которой необходимо полчить значение
	 * @param keyword
	 *            ключевое слово
	 * @return значение для указаной характеристики и ключевого слова
	 */
	public String getCharacteristicValue(Characteristic characteristic, String keyword) {
		return getCharacteristics(checkNotNull(characteristic)).get(checkNotNull(keyword));
	}

	protected boolean addAdditionalParameter(OwnerParameter parameter) {
		checkNotNull(parameter);

		boolean contains = additionalParameters.contains(parameter);
		if (!contains) {
			additionalParameters.add(parameter);
			parameter.setOrdinal(additionalParameters.size());
			evictCachedAdditionalParameters();
		}
		return !contains;
	}

	private void evictCachedAdditionalParameters() {
		cachedAdditionalParameters = null;
	}

	public static class OwnerQuery extends PartyRoleQuery<Owner> {

		private final EntityQueryLogicalFilter<Owner> principal;
		private final EntityQueryNumericFilter<Owner, BigDecimal> taxRate;

		public OwnerQuery() {
			super(Owner.class);
			principal = createLogicalFilter(Owner_.principal);
			taxRate = createNumericFilter(Owner_.taxRate);
		}

		public EntityQueryLogicalFilter<Owner> principal() {
			return principal;
		}

		public EntityQueryNumericFilter<Owner, BigDecimal> taxRate() {
			return taxRate;
		}
	}

	public enum Characteristic {

		PROPERTIES("props"), PARAMETERS("params");

		@Getter
		private String keyword;

		Characteristic(String keyword) {
			this.keyword = keyword;
		}

		public static Characteristic findByKeyword(String keyword) {
			return stream(values()).filter(characteristic -> characteristic.getKeyword().equals(keyword)).findFirst()
					.orElse(null);
		}
	}

	private static final long serialVersionUID = 2365200502873116345L;
}
