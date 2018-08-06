package ru.argustelecom.ops.env.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ru.argustelecom.ops.env.security.model.Permission;
import ru.argustelecom.ops.env.security.model.Role;
import ru.argustelecom.ops.env.security.model.RoleStatus;
import ru.argustelecom.ops.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.configuration.packages.model.PackageDescriptor;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import com.google.common.base.Strings;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@PresentationModel
public class RoleCardViewModel extends ViewModel {

	private static final long serialVersionUID = 7520427260714769338L;

	public static final String VIEW_ID = "/views/env/security/RoleCardView.xhtml";

	@Inject
	private PermissionRepository permissionRepository;

	@Inject
	private RoleRepository roleRepository;

	@Inject
	private OutcomeConstructor outcome;

	@Inject
	private CurrentRole currentRole;
	private Role role;

	private TreeNode permissionTree;
	private TreeNode selectedNode;
	private boolean permissionChanged = false;
	private String searchQuery;
	private SearchResult<AbstractPermissionNode<?>> searchResult;
	private Map<Serializable, AbstractPermissionNode<?>> nodeIndex = new HashMap<>();

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	protected void refresh() {
		checkNotNull(currentRole.getValue(), "currentRole required");
		if (currentRole.changed(role)) {
			role = currentRole.getValue();
			log.debugv("postConstruct. role_id={0}", role.getId());
		}
	}

	public Role getRole() {
		return role;
	}

	public RoleStatus[] getRoleStatuses() {
		return RoleStatus.values();
	}

	public boolean isPermissionChanged() {
		return permissionChanged;
	}

	public TreeNode getPermissionTree() {
		if (permissionTree == null) {
			permissionTree = createTree();
			updateTree();
		}
		return permissionTree;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (this.selectedNode != null)
			this.selectedNode.setSelected(false);

		this.selectedNode = selectedNode;

		if (this.selectedNode != null)
			this.selectedNode.setSelected(true);
	}

	public String removeRole() throws BusinessExceptionWithoutRollback {
		roleRepository.removeRole(role);
		//Используется для обработки исключения, иначе редирект будет происходить до появления сообщения об ошибке
		em.flush();
		return outcome.construct(RoleCreateDialogModel.VIEW_ID);
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public void permissionChanged() {
		permissionChanged = true;
	}

	public void submitPermission() {
		Set<Permission> selected = nodeIndex.values().stream().filter(n -> n instanceof PermissionNode && n.isChecked())
				.map(n -> (Permission) n.getDelegate()).collect(Collectors.toSet());
		role.updatePermissions(selected);
		permissionChanged = false;
	}

	public void cancelPermission() {
		updateTree();
		permissionChanged = false;
	}

	public void searchPermission() {
		setSelectedNode(null);

		if (Strings.isNullOrEmpty(searchQuery))
			return;

		if (searchResult == null || !searchResult.isActual(searchQuery)) {
			searchResult = doSearchPermissions(searchQuery);
		}

		if (!searchResult.isEmpty())
			setSelectedNode(searchResult.nextResult().getTreeNode());
	}

	private TreeNode createTree() {
		TreeNode root = new DefaultTreeNode("root", null);
		List<Permission> permissions = permissionRepository.queryAllPermissions();
		nodeIndex.clear();

		// Первый цикл строит дерево без учета иерархии
		permissions.forEach(permission -> {
			if (!nodeIndex.containsKey(permission.getModule().getId())) {
				// Для модуля родителем всегда будет корень дерева, это нам известно с самого начала
				// поэтому не имеет смысла откладывать правильную сборку иерархии до следующего шага
				ModuleNode moduleNode = new ModuleNode(permission.getModule());
				moduleNode.setTreeNode(new DefaultTreeNode(moduleNode, root));
				nodeIndex.put(permission.getModule().getId(), moduleNode);
			}

			PermissionNode permissionNode = new PermissionNode(permission);
			permissionNode.setTreeNode(new DefaultTreeNode(permissionNode));
			nodeIndex.put(permission.getId(), permissionNode);
		});

		// Второй цикл восстанавшивает иерархию
		nodeIndex.values().stream().filter(n -> n instanceof PermissionNode).map(n -> (PermissionNode) n)
				.forEach(node -> {
					TreeNode parent = null;

					if (node.getDelegate().getParent() != null) {
						parent = nodeIndex.get(node.getDelegate().getParent().getId()).getTreeNode();
					} else {
						parent = nodeIndex.get(node.getDelegate().getModule().getId()).getTreeNode();
					}

					// Должны были определить либо модуль, либо родительский узел. Это должно гарантироваться
					// констрейнтами в БД
					checkState(parent != null);
					node.getTreeNode().setParent(parent);
					parent.getChildren().add(node.getTreeNode());
					parent.setExpanded(true);
				});

		return root;
	}

	private void updateTree() {
		final List<Permission> rolePermissions = role.getPermissions();
		nodeIndex.values().stream().filter(node -> node instanceof PermissionNode).forEach(node -> {
			node.setChecked(rolePermissions.contains(node.getDelegate()));
		});
	}

	private SearchResult<AbstractPermissionNode<?>> doSearchPermissions(String searchQuery) {
		// Для поиска узлов по имени или описанию нужно обходить дерево в том порядке, в котором оно было
		// построено, это позволит последовательно переходить от одного узла к другому в порядке их следования в дереве
		SearchResult<AbstractPermissionNode<?>> searchResult = new SearchResult<>(searchQuery);
		if (permissionTree != null) {
			permissionTree.getChildren().forEach(node -> {
				browseTreeRecursive(searchResult, node);
			});
		}
		return searchResult;
	}

	private void browseTreeRecursive(SearchResult<AbstractPermissionNode<?>> searchResult, TreeNode node) {
		if (node.getData() instanceof AbstractPermissionNode) {
			AbstractPermissionNode<?> permissionNode = (AbstractPermissionNode<?>) node.getData();
			if (searchResult.matchesOneOf(permissionNode.getObjectName(), permissionNode.getDescription()))
				searchResult.add(permissionNode);
		}

		node.getChildren().forEach(child -> {
			browseTreeRecursive(searchResult, child);
		});
	}

	public static abstract class AbstractPermissionNode<T> implements NamedObject {

		private TreeNode treeNode;
		private boolean checked = false;
		private T delegate;

		public AbstractPermissionNode(T delegate) {
			this.delegate = checkNotNull(delegate);
		}

		public abstract boolean isRenderCheckbox();

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public T getDelegate() {
			return delegate;
		}

		public TreeNode getTreeNode() {
			return treeNode;
		}

		public void setTreeNode(TreeNode node) {
			this.treeNode = node;
		}

		@Override
		public abstract String getObjectName();

		public abstract String getDescription();

		public abstract String getId();
	}

	public static class ModuleNode extends AbstractPermissionNode<PackageDescriptor> {

		public ModuleNode(PackageDescriptor delegate) {
			super(delegate);
		}

		@Override
		public boolean isRenderCheckbox() {
			return false;
		}

		@Override
		public String getDescription() {
			return getDelegate().getDescription();
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public String getObjectName() {
			return getDelegate().getObjectName();
		}
	}

	public static class PermissionNode extends AbstractPermissionNode<Permission> {

		public PermissionNode(Permission delegate) {
			super(delegate);
		}

		@Override
		public boolean isRenderCheckbox() {
			return true;
		}

		@Override
		public String getObjectName() {
			return getDelegate().getObjectName();
		}

		@Override
		public String getDescription() {
			return getDelegate().getDescription();
		}

		@Override
		public String getId() {
			return getDelegate().getId();
		}
	}

	public static class SearchResult<T> implements Iterable<T> {
		private String preparedQuery;
		private String searchQuery;
		private List<T> results = new ArrayList<>();
		private Iterator<T> cursor;

		public SearchResult(String searchQuery) {
			this.searchQuery = searchQuery;
			this.preparedQuery = searchQuery.toLowerCase();
		}

		@Override
		public Iterator<T> iterator() {
			return results.iterator();
		}

		public String getSearchQuery() {
			return searchQuery;
		}

		public void add(T result) {
			results.add(result);
		}

		public int size() {
			return results.size();
		}

		public boolean isEmpty() {
			return results.isEmpty();
		}

		public T nextResult() {
			if (results.isEmpty())
				return null;
			if (cursor == null || !cursor.hasNext()) {
				cursor = results.iterator();
			}
			return cursor.next();
		}

		public boolean isActual(String searchQuery) {
			return Objects.equals(this.searchQuery, searchQuery);
		}

		public boolean matchesOneOf(String... values) {
			if (values == null || values.length == 0)
				return false;
			for (String value : values) {
				if (!Strings.isNullOrEmpty(value) && value.toLowerCase().contains(preparedQuery))
					return true;
			}
			return false;
		}
	}

	private static final Logger log = Logger.getLogger(RoleCardViewModel.class);
}
