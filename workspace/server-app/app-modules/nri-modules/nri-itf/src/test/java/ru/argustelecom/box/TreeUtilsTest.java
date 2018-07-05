package ru.argustelecom.box;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * @author d.khekk
 * @since 19.10.2017
 */
public class TreeUtilsTest {

	@Test
	public void sortTree() throws Exception {
		TreeNode root = new DefaultTreeNode("root", null);
		new DefaultTreeNode(new NamedObjectTestImpl("name 10"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 4"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 2"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 15"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 01"), root);

		TreeUtils.sortTree(root);

		List<String> list = root.getChildren().stream().map(node -> ((NamedObject) node.getData()).getObjectName()).collect(toList());
		assertEquals(Arrays.asList("name 01", "name 2", "name 4", "name 10", "name 15"), list);
	}

	@Test
	public void shouldntSortTree() throws Exception {
		TreeNode root = new DefaultTreeNode("root", null);
		new DefaultTreeNode(new NamedObjectTestImpl("name 10"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 4"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 2"), root);
		new DefaultTreeNode(new NamedObjectTestImpl("name 15"), root);
		new DefaultTreeNode("name 01", root);

		TreeUtils.sortTree(root);

		List<String> list = root.getChildren().stream().map(node -> node.getData() instanceof NamedObject ? ((NamedObject) node.getData()).getObjectName() : node.getData().toString()).collect(toList());
		assertEquals(Arrays.asList("name 10", "name 4", "name 2", "name 15", "name 01"), list);
	}

	@Getter
	@AllArgsConstructor
	private class NamedObjectTestImpl implements NamedObject {

		private String name;

		@Override
		public String getObjectName() {
			return name;
		}
	}

}