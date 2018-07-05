package ru.argustelecom.box.env.document.type;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.document.type.DocumentTypeCategory.CONTRACT_TYPE;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.ContractExtensionTypeDtoTranslator;
import ru.argustelecom.box.env.contract.ContractTypeAppService;
import ru.argustelecom.box.env.contract.ContractTypeDtoTranslator;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.dto.ContractRoleDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.document.type.nls.DocumentTypeMessagesBundle;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("documentTypeCreationDm")
@PresentationModel
public class DocumentTypeCreationDialogModel implements Serializable {

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private ContractTypeAppService contractTypeAs;

	@Inject
	private ContractTypeDtoTranslator contractTypeDtoTr;

	@Inject
	private ContractExtensionTypeDtoTranslator contractExtensionTypeDtoTr;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTranslator;

	@Inject
	private ContractRoleDtoTranslator contractRoleDtoTr;

	@Setter
	private Callback<DocumentTypeDto> documentTypeCallback;

	@Getter
	@Setter
	private DocumentTypeCategory newDocTypeCategory;

	@Getter
	@Setter
	private CustomerTypeDto newDocTypeCustomerTypeDto;

	@Getter
	@Setter
	private String newDocTypeName;

	@Getter
	@Setter
	private String newDocTypeDesc;

	@Getter
	@Setter
	private String newDocTypeKey;

	@Getter
	@Setter
	private boolean newAgency;

	@Getter
	@Setter
	private ContractRoleDto newProvider;

	@Getter
	private List<ContractRoleDto> possibleProviders;

	private List<CustomerTypeDto> customerTypes;

	public void onCreationDialogOpen(String widgetVarToHide, String formToUpdate, String widgetVar) {
		if (CONTRACT_TYPE.equals(newDocTypeCategory)) {
			setDefaultProvider();
			refreshPossibleProviders();
		}

		RequestContext.getCurrentInstance().execute(String.format("PF('%s').hide()", widgetVarToHide));
		RequestContext.getCurrentInstance().update(formToUpdate);
		RequestContext.getCurrentInstance().execute(String.format("PF('%s').show()", widgetVar));
	}

	public String getDialogHeader() {
		if (newDocTypeCategory != null) {
			DocumentTypeMessagesBundle messages = LocaleUtils.getMessages(DocumentTypeMessagesBundle.class);
			switch (newDocTypeCategory) {
			case CONTRACT_TYPE:
				return messages.createContract();
			case CONTRACT_EXTENSION_TYPE:
				return messages.createContractExtension();
			case BILL_TYPE:
				return messages.createBill();
			default:
				throw new UnsupportedOperationException("Unsupported document category " + newDocTypeCategory);
			}
		}
		return StringUtils.EMPTY;
	}

	public void createDocumentType() {
		checkState(newDocTypeCategory != null);

		DocumentTypeDto result;
		switch (newDocTypeCategory) {
		case CONTRACT_TYPE: {
			ContractCategory contractCategory = newAgency ? ContractCategory.AGENCY : ContractCategory.BILATERAL;
			result = contractTypeDtoTr.translate(contractTypeAs.createContractType(newDocTypeCustomerTypeDto.getId(),
					newDocTypeName, newDocTypeDesc, contractCategory, newProvider.getId()));
			break;
		}
		case CONTRACT_EXTENSION_TYPE: {
			result = contractExtensionTypeDtoTr.translate(contractTypeAs
					.createContractExtensionType(newDocTypeCustomerTypeDto.getId(), newDocTypeName, newDocTypeDesc));
			break;
		}
		default:
			throw new UnsupportedOperationException("Unsupported document category " + newDocTypeCategory);
		}

		documentTypeCallback.execute(result);
		cleanCreationParams();
	}

	public void cleanCreationParams() {
		newDocTypeCustomerTypeDto = null;
		newDocTypeCategory = null;
		newDocTypeName = null;
		newDocTypeDesc = null;
		newDocTypeKey = null;
		newAgency = false;
		newProvider = null;
	}

	public List<CustomerTypeDto> getCustomerTypes() {
		if (customerTypes == null) {
			customerTypes = DefaultDtoConverterUtils.translate(customerTypeDtoTranslator,
					customerTypeRp.getAllCustomerTypes());
		}
		return customerTypes;
	}

	public void onAgencyChanged() {
		setDefaultProvider();
		refreshPossibleProviders();
	}

	private void setDefaultProvider() {
		PartyRole provider = contractTypeAs.findDefaultProvider(getContractCategory());
		newProvider = provider != null ? contractRoleDtoTr.translate(provider) : null;
	}

	private void refreshPossibleProviders() {
		possibleProviders = contractRoleDtoTr.translate(contractTypeAs.findPossibleProviders(getContractCategory()));
	}

	private ContractCategory getContractCategory() {
		return newAgency ? ContractCategory.AGENCY : ContractCategory.BILATERAL;
	}

	private static final long serialVersionUID = 8490845691282969918L;

}