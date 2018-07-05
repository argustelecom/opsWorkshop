package ru.argustelecom.box;

import org.primefaces.model.TreeNode;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.utils.comparators.AlphanumObjectNameComparator;

import static java.util.Comparator.comparing;

/**
 * Утилиты для дерева
 *
 * @author d.khekk
 * @since 19.10.2017
 */
public class TreeUtils {

	/**
	 * Дефолтный конструктор
	 */
	private TreeUtils() {
		//Дефолтный конструктор
	}

	/**
	 * Отсортировать дерево
	 *
	 * @param treeNode узел дерева
	 */
	public static void sortTree(TreeNode treeNode) {
		if (treeNode.getChildren().stream().allMatch(child -> child.getData() instanceof NamedObject)) {
			treeNode.getChildren().sort(comparing(tNode -> (NamedObject) tNode.getData(),
					new AlphanumObjectNameComparator()));
			treeNode.getChildren().forEach(TreeUtils::sortTree);
		}
	}
}
