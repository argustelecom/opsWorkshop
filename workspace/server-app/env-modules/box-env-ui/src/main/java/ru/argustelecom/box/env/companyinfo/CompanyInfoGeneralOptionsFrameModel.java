package ru.argustelecom.box.env.companyinfo;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypePropertyAccessor;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "companyInfoGeneralOptionsFm")
@PresentationModel
public class CompanyInfoGeneralOptionsFrameModel implements Serializable {

	@Getter
	private TypeInstance typeInstance;

	@Getter
	@Setter
	private TypePropertyAccessor<?> accessor;

	public void preRender(Identifiable owner) {
		if (owner instanceof Owner) {
			typeInstance = ((Owner) owner).getParty().getTypeInstance();
		} else {
			throw new SystemException("В качестве контекста передан неверный параметр");
		}
	}

	private static final long serialVersionUID = 5646516483640783642L;

}