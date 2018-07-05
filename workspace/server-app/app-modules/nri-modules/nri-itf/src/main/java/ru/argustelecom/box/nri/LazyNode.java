package ru.argustelecom.box.nri;

import lombok.EqualsAndHashCode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Ленивая нода. Primefaces не предоставляет нам возможности делать ленивые ноды из коробки. поэтому создаем сами.
 */
@EqualsAndHashCode(callSuper = true)
public class LazyNode extends DefaultTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Загружался ли ранее. Нода загружается один раз
	 */
	private boolean isLoaded = false;

	/**
	 * Метод загрузки детей
	 */
	private Consumer<LazyNode> loadChildren;

	/**
	 * Конструктор
	 *
	 * @param data         дата
	 * @param parent       родитель
	 * @param loadChildren погрузчик детей
	 */
	public LazyNode(Object data, TreeNode parent, Consumer<LazyNode> loadChildren) {
		super(data, parent);
		this.loadChildren = loadChildren;
	}

	/**
	 * загрузить детей
	 */
	public void loadChildren() {
		if (!isLoaded) {
			loadChildren.accept(this);
			isLoaded = true;
		}
	}
}