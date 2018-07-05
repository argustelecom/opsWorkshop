package ru.argustelecom.box.env.directory;

import javax.annotation.PostConstruct;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class DirectoryDashboardViewModel extends ViewModel {

	private static final long serialVersionUID = -983618001263067727L;
	private static final String DEFAULT_TREE_NODE_TYPE = "directory";

	@Getter
	private TreeNode overallDirectoryNode;
	@Getter
	private TreeNode billingDirectoryNode;
	@Getter
	private TreeNode crmDirectoryNode;
	@Getter
	private TreeNode productManagementDirectoryNode;

	@Getter
	private TreeNode nriDirectoryNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		initOverallDirectoryNode();
	}

	public void initOverallDirectoryNode() {
		overallDirectoryNode = new DefaultTreeNode("root", null);
		TreeNode addressDirectory = new DefaultTreeNode(OverallDirectory.ADDRESS_DIRECTORIES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.STRUCTURE, addressDirectory);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.TYPES, addressDirectory);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.LEVELS, addressDirectory);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.COVERAGES, addressDirectory);

		TreeNode personnelDirectory = new DefaultTreeNode(OverallDirectory.PERSONNEL_DIRECTORIES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.EMPLOYEES, personnelDirectory);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.APPOINTMENTS, personnelDirectory);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.CONTACT_TYPES, personnelDirectory);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.MEASURES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.LOOKUP_PROPERTIES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.SECURITY_ROLES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.PARTY_TYPES, overallDirectoryNode);

		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, OverallDirectory.DOC_TYPES, overallDirectoryNode);

		billingDirectoryNode = new DefaultTreeNode("root", null);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, BillingDirectory.PROVISION_TERMS, billingDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, BillingDirectory.TELEPHONY_ZONES, billingDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, BillingDirectory.USAGE_INVOICE_SETTINGS, billingDirectoryNode);


		crmDirectoryNode = new DefaultTreeNode("root", null);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, CrmDirectory.CUSTOMER_TYPES, crmDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, CrmDirectory.CUSTOMER_SEGMENTS, crmDirectoryNode);

		productManagementDirectoryNode = new DefaultTreeNode("root", null);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, ProductManagementDirectory.COMMODITY_TYPES,
				productManagementDirectoryNode);

		nriDirectoryNode = new DefaultTreeNode("root", null);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, NriDirectory.BUILDING_ELEMENT_TYPES, nriDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, NriDirectory.SERVICE_SPECIFICATIONS, nriDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, NriDirectory.LOGICAL_RESOURCE_SPECIFICATIONS, nriDirectoryNode);
		new DefaultTreeNode(DEFAULT_TREE_NODE_TYPE, NriDirectory.RESOURCE_LIFECYCLE, nriDirectoryNode);

	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// ****************************************************************************************************************

	public TreeNode getTechServiceDirectoryNode() {
		return new DefaultTreeNode("root", null);
	}

	// *****************************************************************************************************************
	// Inner class
	// *****************************************************************************************************************

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum OverallDirectory {

		//@formatter:off
		ADDRESS_DIRECTORIES("{LocationBundle:box.location.dir}", "fa fa-map", "", "System_AddressStructureEdit,System_AddressObjectsLevelsEdit,System_AddressObjectsTypesEdit,System_CoverageEdit"),
		STRUCTURE("{LocationBundle:box.location.structure.dir}", "fa fa-sitemap", "/views/env/address/AddressDirectoryView.xhtml", "System_AddressStructureEdit"),
		LEVELS("{LocationBundle:box.location.levels.dir}", "fa fa-list-ol", "/views/env/address/LocationLevelDirectoryView.xhtml", "System_AddressObjectsLevelsEdit"),
		TYPES("{LocationBundle:box.location.types.dir}", "fa fa-archive", "/views/env/address/LocationTypeDirectoryView.xhtml", "System_AddressObjectsTypesEdit"),
		COVERAGES("{LocationBundle:box.location.coverage.dir}", "icon-my-location", "/views/env/techservice/coverage/CoverageDirectoryView.xhtml", "System_CoverageEdit"),
		PERSONNEL_DIRECTORIES("{PersonnelBundle:box.personnel}", "fa fa-group (alias)", "", "System_PersonalView,System_AppointmentEdit,System_ContactsTypesEdit"),
		EMPLOYEES("{PersonnelBundle:box.personnel.employee.plural}", "fa fa-user-plus", "/views/env/personnel/EmployeeListView.xhtml", "System_PersonalView"),
		APPOINTMENTS("{AppointmentBundle:box.appointment.plural}", "icon-work", "/views/env/personnel/AppointmentDirectoryView.xhtml", "System_AppointmentEdit"),
		CONTACT_TYPES("{ContactTypeBundle:box.contact.type.plural}", "icon-contacts", "/views/env/contact/ContactTypeDirectoryView.xhtml", "System_ContactsTypesEdit"),
		MEASURES("{MeasureBundle:box.measure.unit.plural}", "", "/views/env/measure/MeasureUnitDirectoryView.xhtml", "System_MeasureUnitEdit"),

		LOOKUP_PROPERTIES("{LookupBundle:box.lookup.dir.plural}", "", "/views/env/type/LookupDirectoryView.xhtml", "System_LookupDirectoryEdit"),
		SECURITY_ROLES("{SecurityRoleBundle:box.role.plural}", "", "/views/env/security/RoleListView.xhtml", "System_RoleView"),
		PARTY_TYPES("{PartyTypeBundle:box.party.type.plural}", "", "/views/env/party/PartyTypeDirectoryView.xhtml", "System_PartyTypeEdit"),
		DOC_TYPES("{DocumentTypeBundle:box.doc.type.plural}", "", "/views/env/document/type/DocumentTypeDirectoryView.xhtml", "System_DocumentTypesEdit");
		//@formatter:on

		private String bundleProperty;
		@Getter
		private String icon;
		@Getter
		private String path;
		@Getter
		private String permissions;

		public String getName() {
			return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
		}
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum BillingDirectory {
		//@formatter:off
		PROVISION_TERMS("{ProvisionTermsBundle:box.provision_terms}", "", "/views/env/billing/provision/ProvisionTermsDirectoryView.xhtml", "ProductManagment_ProvidingTermsEdit"),
		TELEPHONY_ZONES("{TariffBundle:box.telephony.zone.plural}", "", "/views/env/tariff/TelephonyZoneDirectoryView.xhtml", "Billing_TelephonyZoneEdit"),
		USAGE_INVOICE_SETTINGS("{InvoiceBundle:box.invoice.usage_settings}", "", "/views/env/billing/invoice/UsageInvoiceSettingsView.xhtml", "Billing_UsageInvoiceSettingsView");
		//@formatter:on

		private String bundleProperty;
		@Getter
		private String icon;
		@Getter
		private String path;
		@Getter
		private String permissions;

		public String getName() {
			return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
		}

	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum CrmDirectory {
		//@formatter:off
		CUSTOMER_TYPES("{CustomerTypeBundle:box.customer.type.plural}", "", "/views/env/party/CustomerTypeDirectoryView.xhtml", "System_CustomerTypeEdit"),
		CUSTOMER_SEGMENTS("{SegmentBundle:box.segment.customer.plural}", "", "/views/env/party/CustomerSegmentDirectoryView.xhtml", "System_CustomerSegmentsEdit");
		//@formatter:on

		private String bundleProperty;
		@Getter
		private String icon;
		@Getter
		private String path;
		@Getter
		private String permissions;

		public String getName() {
			return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
		}

	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum ProductManagementDirectory {
		//@formatter:off
		COMMODITY_TYPES("{CommodityTypeBundle:box.commodity.type.plural}", "", "/views/env/commodity/CommodityTypeDirectoryView.xhtml", "System_CommodityTypeEdit");
		//@formatter:on

		private String bundleProperty;
		@Getter
		private String icon;
		@Getter
		private String path;
		@Getter
		private String permissions;

		public String getName() {
			return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
		}

	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum NriDirectory {
		//@formatter:off
		BUILDING_ELEMENT_TYPES("{NriDirectoryBundle:box.nri.directory.buidlingtypes.plural}", "", "/views/nri/building/BuildingElementTypeView.xhtml", "Nri_BuildingAreaTypeEdit, Nri_BuildingAreaTypeView"),
		SERVICE_SPECIFICATIONS("{NriDirectoryBundle:box.nri.directory.servicetypes.plural}", "", "/views/nri/service/ServiceSpecificationsView.xhtml", "Nri_ServiceSpecificationEdit, Nri_ServiceSpecificationView"),
		LOGICAL_RESOURCE_SPECIFICATIONS("{NriDirectoryBundle:box.nri.directory.logicalrestypes.plural}", "", "/views/nri/logical/LogicalResourcesSpecDirectoryView.xhtml", "Nri_LogicalResourceSpecEdit, Nri_LogicalResourceSpecView"),
		RESOURCE_LIFECYCLE("{NriDirectoryBundle:box.nri.directory.resource.lifecycle.plural}", "", "/views/nri/resources/lifecycle/LifecyclesView.xhtml", "Nri_ResourceLifecycleView, Nri_ResourceLifecycleEdit");
		//@formatter:on

		private String bundleProperty;
		@Getter
		private String icon;
		@Getter
		private String path;
		@Getter
		private String permissions;

		public String getName() {
			return LocaleUtils.getLocalizedMessage(bundleProperty, getClass());
		}
	}

}