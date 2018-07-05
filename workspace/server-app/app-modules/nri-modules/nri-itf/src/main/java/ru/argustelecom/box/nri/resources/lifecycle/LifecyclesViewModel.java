package ru.argustelecom.box.nri.resources.lifecycle;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Модель страницы карточки ресурса
 * Created by s.kolyada on 22.09.2017.
 */
@Named(value = "lifecyclesVM")
@PresentationModel
public class LifecyclesViewModel extends ViewModel {

	private static final long serialVersionUID = -9145670817894282997L;

	/**
	 * Дерево с ЖЦ
	 */
	@Getter
	private TreeNode lifecycles;

	/**
	 * Выбранный узел дерева
	 */
	@Setter
	@Getter
	private TreeNode selectedNode;

	/**
	 * Репозиторий доступа к ЖЦ
	 */
	@Inject
	private ResourceLifecycleRepository lifecycleRepository;

	/**
	 * Транслятор ЖЦ
	 */
	@Inject
	private ResourceLifecycleDtoTranslator lifecycleDtoTranslator;

	/**
	 * Действия после созданя модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		init();
	}

	/**
	 * Инициализация
	 */
	private void init() {
		List<ResourceLifecycleDto> lifecycleList = lifecycleRepository.findAll()
				.stream()
				.map(lifecycleDtoTranslator::translate)
				.collect(Collectors.toList());

		lifecycles = new DefaultTreeNode("root", null);
		lifecycleList.stream().forEach(l -> new DefaultTreeNode(l, lifecycles));
	}

	/**
	 * Получить выбранный ЖЦ
	 * @return ЖЦ
	 */
	public ResourceLifecycleDto getSelectedLifecycle() {
		if (selectedNode == null) {
			return null;
		}
		return (ResourceLifecycleDto) selectedNode.getData();
	}
}
