package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.system.inf.utils.CDIHelper.lookupCDIBean;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.primefaces.context.RequestContext;
import org.primefaces.util.MessageFactory;

import ru.argustelecom.box.env.address.AddressAppService;
import ru.argustelecom.box.env.address.AddressQueryResult;
import ru.argustelecom.box.env.address.LocationClass;
import ru.argustelecom.box.env.address.SearchLevel;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

/**
 * Компонент для поиска адресов. По-умолчанию, валидными значениями для компонента являются адреса со зданием.
 */
@FacesComponent("inputAddress")
public class InputAddress extends UIInput implements NamingContainer {

	private EntityConverter entityConverter = new EntityConverter();

	@Override
	@SuppressWarnings("unchecked")
	public void encodeBegin(FacesContext context) throws IOException {
		BusinessObjectDto<Location> value = (BusinessObjectDto<Location>) getValue();
		if (value != null) {
			AddressQueryResult address = createAddress(value.getIdentifiable());
			setAddress(address);
		}
		super.encodeBegin(context);
	}

	@Override
	public Object getSubmittedValue() {
		AddressQueryResult address = getAddress();
		if (address != null) {
			Class<? extends Location> clazz = checkNotNull(address.getLocationClass().getClazz(),
					"Unknown location type");
			return format("%s-%s", clazz.getSimpleName(), address.getId());
		}
		return EMPTY;
	}

	@Override
	protected Object getConvertedValue(FacesContext context, Object newValue) throws ConverterException {
		if (newValue == null) {
			return null;
		}

		Location location = entityConverter.convertToObject(Location.class, (String) newValue);
		BusinessObjectDtoTranslator translator = lookupCDIBean(BusinessObjectDtoTranslator.class);
		return translator.translate(location);
	}

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	@Override
	protected void validateValue(FacesContext context, Object newValue) {
		super.validateValue(context, newValue);

		AddressQueryResult address = getAddress();
		if (address != null && !getValidLocationClasses().contains(address.getLocationClass())) {
			Object[] params = new Object[1];
			params[0] = MessageFactory.getLabel(context, this);

			FacesMessage msg = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, SEVERITY_ERROR, params);
			context.addMessage(getClientId(context), msg);
			setValid(false);

			RequestContext.getCurrentInstance()
					.execute(format("Argus.System.AddressInput.validate('%s', %s)", getAutoCompleteClientId(), isValid()));
		}
	}

	@Override
	public void resetValue() {
		super.resetValue();
		getStateHelper().remove(PropertyKeys.address);
	}

	public List<AddressQueryResult> complete(String rawInput) {
		return lookupCDIBean(AddressAppService.class).searchAddress(rawInput, getMaxResults(), getSearchLevel());
	}

	public AddressQueryResult getAddress() {
		return (AddressQueryResult) getStateHelper().get(PropertyKeys.address);
	}

	public void setAddress(AddressQueryResult address) {
		getStateHelper().put(PropertyKeys.address, address);
	}

	public void onItemSelect() {
		AddressQueryResult address = getAddress();
		if (address != null && !getValidLocationClasses().contains(address.getLocationClass())) {
			RequestContext.getCurrentInstance()
					.execute(format("Argus.System.AddressInput.onItemSelect('%s')", getAutoCompleteClientId()));
		}
	}

	private String getAutoCompleteClientId() {
		return format("%s-%s_input", getClientId(), getId());
	}

	private Integer getMaxResults() {
		return (Integer) getAttributes().get(AttributeKeys.maxResults.name());
	}

	private LocationClass getLocationClass() {
		return (LocationClass) getAttributes().get(AttributeKeys.locationClass.name());
	}

	private AddressQueryResult createAddress(Location location) {
		location = EntityManagerUtils.initializeAndUnproxy(location);
		return new AddressQueryResult(location.getId(), LocationClass.findByClass(location.getClass()).name(),
				location.getNameBefore(getLocationClass()));
	}

	private SearchLevel getSearchLevel() {
		return (SearchLevel) getAttributes().get(AttributeKeys.searchLevel.name());
	}

	@SuppressWarnings("unchecked")
	private Collection<LocationClass> getValidLocationClasses() {
		Object value = getAttributes().get(AttributeKeys.validLocationClass.name());
		if (value == null) {
			value = newHashSet(LocationClass.B);
			getAttributes().put(AttributeKeys.validLocationClass.name(), value);
		} else if (value instanceof LocationClass[]) {
			value = newHashSet((LocationClass[]) value);
			getAttributes().put(AttributeKeys.validLocationClass.name(), value);
		}
		return (Collection<LocationClass>) value;
	}

	private enum AttributeKeys {
		locationClass, maxResults, searchLevel, validLocationClass
	}

	private enum PropertyKeys {
		address
	}

}