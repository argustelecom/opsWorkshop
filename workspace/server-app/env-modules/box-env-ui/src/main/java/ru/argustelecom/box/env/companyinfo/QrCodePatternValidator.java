package ru.argustelecom.box.env.companyinfo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static ru.argustelecom.box.env.party.model.role.Owner.QR_CODE_PARAMETER;
import static ru.argustelecom.box.env.party.model.role.Owner.QR_CODE_PARAMETER_DELIMITER;
import static ru.argustelecom.box.env.party.model.role.Owner.Characteristic.PARAMETERS;
import static ru.argustelecom.box.env.party.model.role.Owner.Characteristic.PROPERTIES;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.barcode.ST00012QrCodeDataFormatter.ST00012QrCodeItem;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.PartyTypeAppService;
import ru.argustelecom.box.env.party.model.role.Owner.Characteristic;
import ru.argustelecom.box.env.party.nls.OwnerMessageBundle;

@Named
@ConversationScoped
public class QrCodePatternValidator implements Serializable, Validator {

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private PartyTypeAppService partyTypeAs;

	public void validate(FacesContext context, UIComponent component, Object o) throws ValidatorException {
		Map<String, Object> attributes = component.getAttributes();

		Long entityId = checkNotNull(getAttribute(attributes, Attribute.entityId.name(), Long.class, null));
		Boolean isPartyType = getAttribute(attributes, Attribute.partyType.name(), Boolean.class, false);
		Boolean isOwner = getAttribute(attributes, Attribute.owner.name(), Boolean.class, false);

		if (o == null || !String.class.isInstance(o)) {
			return;
		}

		if ((isPartyType && isOwner) || (!isPartyType && !isOwner)) {
			throw new IllegalArgumentException("Only one attribute must be set");
		}

		OwnerMessageBundle messages = getMessages(OwnerMessageBundle.class);
		Consumer<String> throwFun = detail -> throwValidatorException(SEVERITY_ERROR,
				messages.qrCodePatternValidationSummary(), detail);

		Map<Characteristic, Collection<String>> ownerKeywords = newHashMap();
		if (isOwner) {
			ownerKeywords.putAll(ownerAs.getKeywords(entityId));
		} else {
			ownerKeywords.put(PROPERTIES, partyTypeAs.getKeywords(entityId));
		}

		Set<ST00012QrCodeItem> parsedRequiredItems = newHashSet();

		stream(((String) o).split(QR_CODE_PARAMETER_DELIMITER)).forEach(expression -> {
			Matcher matcher = QR_CODE_PARAMETER.matcher(expression);
			if (!matcher.matches()) {
				throwFun.accept(messages.invalidQrCodePatternMapping(expression, QR_CODE_PARAMETER.pattern()));
			}

			String itemKeyword = matcher.group(1);
			ST00012QrCodeItem item = ST00012QrCodeItem.findByKeyword(itemKeyword);
			if (item == null || !item.isOwnerCharacteristic()) {
				throwFun.accept(messages.invalidQrCodeItem(expression, itemKeyword));
			}
			parsedRequiredItems.add(item);

			String characteristicKeyword = matcher.group(2);
			Characteristic characteristic = Characteristic.findByKeyword(characteristicKeyword);
			if (characteristic == null || (isPartyType && PARAMETERS.equals(characteristic))) {
				throwFun.accept(messages.invalidCharacteristic(expression, characteristicKeyword));
			}

			String characteristicItemKeyword = matcher.group(3);
			Collection<String> keywords = ownerKeywords.get(characteristic);
			if (keywords == null || !keywords.contains(characteristicItemKeyword)) {
				throwFun.accept(messages.invalidCharacteristicKeyword(expression, characteristicKeyword, itemKeyword));
			}
		});

		if (!parsedRequiredItems.containsAll(ST00012QrCodeItem.requiredItems())) {
			throwFun.accept(messages.uniqueItemIsMissing());
		}
	}

	private void throwValidatorException(Severity severity, String summary, String detail) {
		throw new ValidatorException(new FacesMessage(severity, summary, detail));
	}

	private <T> T getAttribute(Map<String, Object> attributes, String key, Class<T> clazz, T defaultValue) {
		return ofNullable(attributes.get(key)).filter(clazz::isInstance).map(clazz::cast).orElse(defaultValue);
	}

	private enum Attribute {
		entityId, owner, partyType
	}

	private static final long serialVersionUID = -399659338311434062L;
}
