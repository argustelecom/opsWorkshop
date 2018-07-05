package ru.argustelecom.box.env.product;

import static ru.argustelecom.box.env.product.ProductTypeCategory.COMPOSITE;
import static ru.argustelecom.box.env.product.ProductTypeCategory.GROUP;
import static ru.argustelecom.box.env.product.ProductTypeCategory.SIMPLE;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.product.nls.ProductMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "productTypeCreationDm")
@PresentationModel
public class ProductTypeCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 2747560944342042497L;

	@Inject
	private ProductTypeRepository productTypeRepository;

	@Inject
	private ProductTypeDirectoryViewState productTypeDirectoryViewState;

	private ProductTypeCategory newCategory;
	private String newName;
	private String newKeyword;
	private String newDescription;
	private String newGroup;

	public boolean showProductTypeAttributes() {
		return newCategory != null && !newCategory.equals(GROUP);
	}

	public String getCreationDialogHeader() {
		ProductMessagesBundle messages = LocaleUtils.getMessages(ProductMessagesBundle.class);
		if (newCategory != null)
			switch (newCategory) {
			case GROUP:
				return messages.productGroupCreation();
			case SIMPLE:
				return messages.productPlainCreation();
			case COMPOSITE:
				return messages.productCompositeCreation();
			default:
				throw new SystemException(String.format("Unsupported product type: '%s'", newCategory));
			}
		return StringUtils.EMPTY;
	}

	public AbstractProductUnit create() {
		AbstractProductUnit abstractProductUnit = null;
		if (newCategory.equals(GROUP)) {
			abstractProductUnit = new ProductTypeGroupUnit(
					productTypeRepository.createProductTypeGroup(newName, newDescription));
		}
		if (newCategory.equals(SIMPLE)) {
			abstractProductUnit = new ProductTypeUnit(productTypeRepository.createProductType(newName, newKeyword,
					newDescription, ((ProductTypeGroupUnit) currentProductTypeUnit()).getProductTypeGroup()));
		}
		if (newCategory.equals(COMPOSITE)) {
			abstractProductUnit = new ProductTypeCompositeUnit(
					productTypeRepository.createProductTypeComposite(newName, newKeyword, newDescription,
							((ProductTypeGroupUnit) currentProductTypeUnit()).getProductTypeGroup()));
		}
		cleanCreationParams();
		return abstractProductUnit;
	}

	public void cleanCreationParams() {
		newCategory = null;
		newName = null;
		newKeyword = null;
		newDescription = null;
		newGroup = null;
	}

	public AbstractProductUnit currentProductTypeUnit() {
		return productTypeDirectoryViewState.getProductTypeUnit();
	}

	public ProductTypeCategory getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(ProductTypeCategory newCategory) {
		this.newCategory = newCategory;
	}

	public String getNewName() {
		return newName;
	}

	public String getNewKeyword() {
		return newKeyword;
	}

	public void setNewKeyword(String newKeyword) {
		this.newKeyword = newKeyword;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public String getNewGroup() {
		return newGroup;
	}

	public void setNewGroup(String newGroup) {
		this.newGroup = newGroup;
	}

}