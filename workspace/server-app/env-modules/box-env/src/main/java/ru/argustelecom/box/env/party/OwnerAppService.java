package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem.ownerCharacteristicItems;
import static ru.argustelecom.box.env.party.PartyCategory.COMPANY;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Owner.Characteristic;
import ru.argustelecom.box.env.party.nls.OwnerMessageBundle;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.hibernate.engine.spi.ArgusSessionImplementor;

/**
 * Репозиторий для работы с {@linkplain Owner юридическими лицами компании}.
 */
@ApplicationService
public class OwnerAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OwnerRepository ownerRp;

	/**
	 * Возвращает всех юр. лиц компании.
	 */
	public List<Owner> findAll() {
		return ownerRp.findAll();
	}

	/**
	 * Возращает основное юр. лицо компании.
	 */
	public Owner findPrincipal() {
		return ownerRp.findPrincipal();
	}

	/**
	 * Создаёт юр. лицо.
	 *
	 * @param partyTypeId
	 *            тип участника, обязательно должен относиться к категории {@link PartyCategory#COMPANY}
	 * @param name
	 *            название юр. лица
	 * @param taxRate
	 *            НДС
	 * @param principal
	 *            является ли данное юр. лицо основным
	 */
	public Owner create(Long partyTypeId, String name, int taxRate, String qrCodePattern, boolean principal) {
		checkNotNull(partyTypeId);
		checkArgument(isNotBlank(name));
		checkArgument(taxRate >= 0 && taxRate <= 100);

		PartyType partyType = em.find(PartyType.class, partyTypeId);
		checkNotNull(partyType);
		checkArgument(COMPANY.equals(partyType.getCategory()));

		return ownerRp.createOwner(partyType, name, valueOf(taxRate), qrCodePattern, principal);
	}

	/**
	 * Изменяет название юр. лица.
	 * 
	 * @param ownerId
	 *            идентификатор юр. лица, для которого приминяем изменение.
	 * @param name
	 *            новое название.
	 */
	public void changeName(Long ownerId, String name) {
		checkNotNull(ownerId);
		checkArgument(isNotBlank(name));

		Owner owner = em.find(Owner.class, ownerId);
		checkNotNull(owner);

		if (!name.equals(owner.getObjectName())) {
			owner.getParty().setLegalName(name);
		}
	}

	/**
	 * Изменяет шаблон QR-кода
	 *
	 * @param ownerId
	 *            идентификатор юр. лица, для которого приминяем изменение.
	 * @param qrCodePattern
	 *            новый шаблон QR-кода
	 */
	public void changeQrCodePattern(Long ownerId, String qrCodePattern) {
		Owner owner = checkNotNull(em.find(Owner.class, checkNotNull(ownerId)));
		if (!Objects.equals(owner.getQrCodePattern(), qrCodePattern)) {
			owner.setQrCodePattern(qrCodePattern);
		}
	}

	/**
	 * Назначает основное юр. лицо, старое юр. лицо будет разжаловано в простое.
	 * 
	 * @param ownerId
	 *            идентификатор юр. лица, которое должно стать основным.
	 */
	public void markPrincipal(Long ownerId) {
		checkNotNull(ownerId);

		Owner owner = em.find(Owner.class, ownerId);
		checkNotNull(owner);

		if (!owner.isPrincipal()) {
			ownerRp.makePrincipal(owner);
		}
	}

	/**
	 * Удаляет юр. лицо. Удаляемоё юр. лицо должно быть не {@linkplain Owner#isPrincipal() основным}.
	 *
	 * @param ownerId
	 *            идентификатор юр. лица, которое должно быть удалено.
	 */
	public void remove(Long ownerId) {
		checkNotNull(ownerId);

		Owner owner = em.find(Owner.class, ownerId);
		checkNotNull(owner);
		checkArgument(!owner.isPrincipal(), "Can not remove a principal owner");

		em.remove(owner);
	}

	/**
	 * Меняет шаблон письма для рассылки у {@link Owner}
	 *
	 * @param ownerId
	 *            идентификатор {@link Owner}
	 * @param templateName
	 *            имя шаблона
	 * @param template
	 *            шаблон
	 */
	public void changeMailTemplate(Long ownerId, String templateName, byte[] template) {
		Owner owner = checkNotNull(em.find(Owner.class, checkNotNull(ownerId)));
		owner.setEmailTemplateName(templateName);
		owner.setEmailTemplate(((ArgusSessionImplementor) em.getDelegate()).getLobHelper().createBlob(template));
	}

	/**
	 * Удаляет шаблон письма у {@link Owner}
	 *
	 * @param ownerId
	 *            идентификатор {@link Owner}
	 */
	public void removeMailTemplate(Long ownerId) {
		Owner owner = checkNotNull(em.find(Owner.class, checkNotNull(ownerId)));
		owner.setEmailTemplateName(null);
		owner.setEmailTemplate(null);
	}

	/**
	 * Возвращает ассоциативный массив, содержащий все ключевые слова для всех {@link Characteristic}
	 *
	 * @param ownerId
	 *            идентификатор {@link Owner}
	 * @return ассоциативный массив, ключем которого является {@link Characteristic}, а значением - соответствующая
	 *         коллекция ключевых слов
	 */
	public Map<Characteristic, Collection<String>> getKeywords(Long ownerId) {
		Owner owner = checkNotNull(em.find(Owner.class, checkNotNull(ownerId)));
		return stream(Characteristic.values()).collect(toMap(c -> c, c -> owner.getCharacteristics(c).keySet()));
	}

	/**
	 * Возвращает коллекцию ключевых слов для указаной {@link Characteristic}
	 *
	 * @param ownerId
	 *            идентификатор {@link Owner}
	 * @param characteristic
	 *            характеристика, у которой нужно получить коллекцию ключевых слов
	 * @return коллекцию ключевых слов для указаной {@link Characteristic}
	 */
	public Collection<String> getKeywords(Long ownerId, Characteristic characteristic) {
		return checkNotNull(em.find(Owner.class, checkNotNull(ownerId)))
				.getCharacteristics(checkNotNull(characteristic)).keySet();
	}

	public Blob getTemplate(Long ownerId) {
		return checkNotNull(em.find(Owner.class, checkNotNull(ownerId))).getEmailTemplate();
	}

	/**
	 * @return Возвращает подсказку как заполнять шаблон QR-кода
	 */
	public String getQrCodePatternTooltipHint() {
		//@formatter:off
		Map<Boolean, String> map = ownerCharacteristicItems().stream()
				.collect(groupingBy(ST00012QrCodeItem::isRequired,
						mapping(ST00012QrCodeItem::getKeyword, joining(", "))));
		//@formatter:on

		return getMessages(OwnerMessageBundle.class).qrCodePatternTooltipHint(map.get(true), map.get(false));
	}

	private static final long serialVersionUID = 6663491540877357648L;
}