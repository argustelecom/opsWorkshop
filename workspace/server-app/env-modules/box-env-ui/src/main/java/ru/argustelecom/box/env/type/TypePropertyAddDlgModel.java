package ru.argustelecom.box.env.type;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyRef;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.page.PresentationModel;

import static java.util.Optional.ofNullable;

@Named(value = "typePropertyAddDm")
@PresentationModel
public class TypePropertyAddDlgModel implements Serializable {

	@Inject
	private CurrentType currentType;

	@Inject
	private DirectoryCacheService directoryCacheService;

	@Inject
	private TypePropertyTranslator typePropertyTr;

	@Getter
	private TypePropertyDto propertyDto = new TypePropertyDto();

	@Setter
	private Callback<TypeProperty<?>> addPropertyCallback;

	public void clear() {
		propertyDto = new TypePropertyDto();
	}

	public Set<TypePropertyRef> getSupportedPropertyTypes() {
		return ofNullable(currentType.getValue()).map(Type::getSupportedPropertyTypes).orElse(null);
	}

	public void addProperty() {
		propertyDto.setType(currentType.getValue());
		addPropertyCallback.execute(typePropertyTr.translate(propertyDto));
		clear();
	}

	public Collection<MeasureUnit> getPossibleMeasureUnits() {
		return directoryCacheService.getDirectoryObjects(MeasureUnit.class);
	}

	public String getSpecificParamsBlockStyle() {
		if (propertyDto.getType() == null) {
			return "";
		}
		return propertyDto.getType().toString();
	}

	private static final long serialVersionUID = 1L;
}
