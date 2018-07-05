package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.LocationCategory.COUNTRY;
import static ru.argustelecom.box.env.address.LocationCategory.REGION;
import static ru.argustelecom.box.env.address.LocationCategory.STREET;
import static ru.argustelecom.box.env.address.model.LocationType.LEVEL_QUERY_PARAM;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.tree.LazyTreeNode;

@PresentationModel
public class AddressDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -1644513452899174199L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationLevelRepository locationLevelRepository;

	@Inject
	private AddressDirectoryViewState addressDirectoryViewState;

	@Inject
	private CurrentLocation currentLocation;

	@Inject
	private AddressAppService addressAppSrv;

	private AddressLazyTreeNodeStrategy addressLazyTreeNodeStrategy;
	private AddressLazyTreeNodeLoader addressLazyTreeNodeLoader;

	private LazyTreeNode<?> locationNode;
	private TreeNode selectedNode;
	private Location location;

	private List<LocationLevel> levels;
	private LocationCategory newCategory;
	private String newName;
	private LocationLevel newLevel;
	private LocationType newType;

	private LocationMessagesBundle messages;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		messages = LocaleUtils.getMessages(LocationMessagesBundle.class);
		currentLocation.setValue(addressDirectoryViewState.getLocation());
		location = addressDirectoryViewState.getLocation();
		initNode();
	}

	public List<LocationType> getTypes() {
		newLevel = newCategory != null && newCategory.equals(REGION) ? getNewLevel() : locationLevelRepository.street();
		return em.createNamedQuery(LocationType.FIND_TYPES_BY_LEVEL, LocationType.class)
				.setParameter(LEVEL_QUERY_PARAM, newLevel).getResultList();
	}

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('locationCategoriesPanel').hide()");
		RequestContext.getCurrentInstance().update("location_creation_form-location_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('locationCreationDlg').show()");
	}

	public void create() {
		Location newLocation = null;
		String resultMessage = "";
		if (newCategory.equals(COUNTRY)) {
			newLocation = locationRepository.findCountry(newName);
			if (newLocation != null) {
				Notification.error(messages.cannotCreateCountry(), messages.countryAlreadyExist(newName));
				return;
			}
			newLocation = locationRepository.createCountry(newName);
			addNode(newLocation, locationNode);
			resultMessage = messages.countrySuccessfullyCreated(newLocation.getObjectName());
		}

		Location parent = (Location) selectedNode.getData();

		if (newCategory.equals(REGION)) {
			newLocation = locationRepository.findRegion(parent, newType, newName);
			if (newLocation != null) {
				Notification.error(messages.cannotCreateRegion(),
						messages.regionAlreadyExist(newType.getShortName(), newName));
				return;
			}

			newLocation = locationRepository.createRegion(parent, newName, newType);
			addNode(newLocation, selectedNode);
			resultMessage = messages.regionSuccessfullyCreated(newLocation.getObjectName());
		}
		if (newCategory.equals(STREET)) {
			newLocation = locationRepository.findStreet(parent, newType, newName);
			if (newLocation != null) {
				Notification.error(messages.cannotCreateStreet(),
						messages.streetAlreadyExist(newType.getShortName(), newName, parent.getFullName()));
				return;
			}
			newLocation = locationRepository.createStreet(parent, newName, newType);
			addNode(newLocation, selectedNode);
			resultMessage = messages.streetSuccessfullyCreated(newLocation.getObjectName());
		}

		em.persist(newLocation);
		em.flush();

		addressAppSrv.reindex(newLocation.getId());

		cleanParams();

		RequestContext.getCurrentInstance().update("dialogs-location_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('locationCreationDlg').hide();");
		Notification.info(messages.locationCreated(), resultMessage);
	}

	public void remove() {
		selectedNode = addressLazyTreeNodeStrategy.getSelectedNode();
		if (selectedNode.getChildCount() == 0) {
			Location removableLocation = (Location) selectedNode.getData();
			TreeNode parent = selectedNode.getParent();
			parent.getChildren().remove(selectedNode);
			setSelectedNode(null);
			addressDirectoryViewState.setLocation(null);
			currentLocation.setValue(null);
			em.remove(removableLocation);
			em.flush();
			addressAppSrv.reindex(removableLocation.getId());
			Notification.info(messages.locationRemoved(),
					messages.locationSuccessfullyRemoved(location.getObjectName()));
		} else {
			Notification.warn(messages.cannotRemoveLocation(location.getObjectName()),
					messages.locationHaveChildObjects());
		}
	}

	public void cleanParams() {
		newCategory = null;
		newName = null;
		newLevel = null;
		newType = null;
	}

	public boolean canCreateRegion() {
		return location != null && (location instanceof Country || location instanceof Region);
	}

	public boolean canCreateStreet() {
		return location != null && location instanceof Region;
	}

	public String getCreationDialogHeader() {
		if (newCategory != null) {
			switch (newCategory) {
			case COUNTRY:
				return messages.countryCreation();
			case REGION:
				return messages.regionCreation();
			case STREET:
				return messages.streetCreation();
			default:
				// FIXME o.naumov - что делать, если newCategory будет содержать другой тип
				break;
			}
		}

		return StringUtils.EMPTY;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initNode() {
		addressLazyTreeNodeLoader = new AddressLazyTreeNodeLoader(em);
		addressLazyTreeNodeStrategy = new AddressLazyTreeNodeStrategy();
		if (location != null) {
			addressLazyTreeNodeStrategy.setExpandedUnits(Sets.newHashSet(getParents(location)));
			addressLazyTreeNodeStrategy.setSelectedUnit(location);
		}
		locationNode = LazyTreeNode.createRoot(loadRootNodes(), addressLazyTreeNodeStrategy, addressLazyTreeNodeLoader);
		selectedNode = addressLazyTreeNodeStrategy.getSelectedNode();
	}

	private TreeNode addNode(Location newLocation, TreeNode parentNode) {
		TreeNode newLocationNode = LazyTreeNode.create(Lists.newArrayList(newLocation), addressLazyTreeNodeStrategy,
				addressLazyTreeNodeLoader, parentNode).get(0);
		parentNode.getChildren().add(newLocationNode);
		selectNode(newLocationNode);
		return newLocationNode;
	}

	private void selectNode(TreeNode newSelectedNode) {
		if (selectedNode != null) {
			selectedNode.setSelected(false);
			selectedNode.setExpanded(true);
		}
		newSelectedNode.setSelected(true);
		setSelectedNode(newSelectedNode);
		addressLazyTreeNodeStrategy.setSelectedNode((LazyTreeNode) newSelectedNode);
		addressLazyTreeNodeStrategy.setExpandedUnits(Sets.newHashSet(getParents(location)));
		selectedNode.getParent().getChildren().stream().filter(treeNode -> !treeNode.equals(selectedNode))
				.forEach(treeNode -> treeNode.setSelected(false));
	}

	private List<Location> loadRootNodes() {
		return em.createNamedQuery(Country.GET_ALL_COUNTRIES, Location.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Location> getParents(Location location) {
		List<Object> objects = em.createNamedQuery(Location.FIND_PARENTS).setParameter("location_id", location.getId())
				.getResultList();
		List<Long> ids = objects.stream().map(o -> ((BigInteger) o).longValue()).collect(Collectors.toList());
		return EntityManagerUtils.findList(em, Location.class, ids);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public TreeNode getLocationNode() {
		return locationNode;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null) {
			location = EntityManagerUtils.initializeAndUnproxy((Location) selectedNode.getData());
			addressDirectoryViewState.setLocation(location);
			currentLocation.setValue(location);
			if (!(selectedNode instanceof DefaultTreeNode)) {
				addressLazyTreeNodeStrategy.setSelectedNode((LazyTreeNode) selectedNode);
			}
		}
		this.selectedNode = selectedNode;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<LocationLevel> getLevels() {
		if (levels == null) {
			levels = locationLevelRepository.findRegionLevels();
		}
		return levels;
	}

	public LocationCategory getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(LocationCategory newCategory) {
		this.newCategory = newCategory;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public LocationLevel getNewLevel() {
		return newLevel == null ? getLevels().get(0) : newLevel;
	}

	public void setNewLevel(LocationLevel newLevel) {
		this.newLevel = newLevel;
	}

	public LocationType getNewType() {
		return newType;
	}

	public void setNewType(LocationType newType) {
		this.newType = newType;
	}

}