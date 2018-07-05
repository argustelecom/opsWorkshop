package ru.argustelecom.box.env.lifecycle.api.definition;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleVariableConfigurator;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeProperty;

/**
 * Предназначен для удобного определения переменной жизненного цикла. Содержит уникальный автогененрируемый
 * идентификатор этой переменной, тип переменной, а также специализированный конфигуратор, при помощи которого
 * переменную можно настроить, например, указав значение по умолчанию или наименование переменной для показа
 * пользователю в специализированном UI.
 * 
 * <p>
 * Переменные жизненного цикла реализовываны при помощи {@linkplain Type фреймворка спецификаций} как
 * {@linkplain TypeProperty}
 * 
 * <p>
 * Пример использования:
 * 
 * <pre>
 * <code>
 * {@literal @}LifecycleRegistrant
 * public class SampleLifecycle implements LifecycleFactory&lt;SampleState, Sample&gt; {
 *    
 *    {@literal @}Override
 *    public void buildLifecycle(LifecycleBuilder&lt;SampleState, Sample&gt; lifecycle) {
 *       ...
 *       lifecycle.route(Routes.ACTIVATE, "Активировать")
 *          .from(SampleState.DRAFT)
 *          .to(SampleState.ACTIVE)
 *             .silent(false)
 *             .contextVar(Variables.COMMENT)
 *             .execute(SampleActionWriteComment.class)
 *          .end()
 *       .end();
 *       ... 
 *    }
 *    
 *    public enum Routes {
 *       ACTIVATE;
 *    }
 *    
 *    public static final class Variables {
 *    
 *       public static final LifecycleVariable&lt;TextProperty&gt; COMMENT = of(TextProperty.class, var -&gt; {
 *          var.setName("Комментарий");
 *          var.setHint("Напишите комментарий, поясняющий изменение статуса объекта");
 *          var.setLinesCount(5);
 *       });
 *
 *       private Variables() {}
 *    }
 * }
 * 
 * {@literal @}LifecycleBean
 * public class SampleActionWriteComment implements LifecycleCdiAction&lt;SampleState, Sample&gt; {
 *    
 *    {@literal @}Inject
 *    private CommentRepository comments;
 *    
 *    {@literal @}Override
 *    public void execute(ExecContext&lt;SampleState, ? extends Sample&gt; ctx) {
 *       Sample businessObject = ctx.getBusinessObject();
 *       checkState(businessObject instanceof HasComments);
 *       
 *       String commentText = ctx.getVariable(SampleLifecycle.Variables.COMMENT);
 *       Comment comment = comments.createComment(commentText, ...);
 *       
 *       businessObject.getCommentContext().addComment(comment);
 *    }
 * }
 * </code>
 * </pre>
 * 
 * @param <P>
 *            - тип переменной
 */
@EqualsAndHashCode(of = "uid")
public final class LifecycleVariable<P extends TypeProperty<?>> implements Serializable {

	private static final long serialVersionUID = -4864580274054817480L;

	private final String uid;
	private final Class<P> type;
	private final LifecycleVariableConfigurator<P> conf;

	private LifecycleVariable(Class<P> type, LifecycleVariableConfigurator<P> conf) {
		this.type = checkRequiredArgument(type, "type");
		this.conf = checkRequiredArgument(conf, "conf");
		this.uid = type.getSimpleName() + "_" + UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * Создает определение переменной для последующего использования
	 * 
	 * @param type
	 *            - тип переменной
	 * @param conf
	 *            - кофигуратор переменной
	 * 
	 * @return определение переменной
	 */
	public static <X extends TypeProperty<?>> LifecycleVariable<X> of(Class<X> type,
			LifecycleVariableConfigurator<X> conf) {
		return new LifecycleVariable<X>(type, conf);
	}

	/**
	 * Возвращает тип переменной
	 */
	public Class<P> type() {
		return type;
	}

	/**
	 * Возвращает конфигуратор переменной
	 */
	public LifecycleVariableConfigurator<P> conf() {
		return conf;
	}

	@Override
	public String toString() {
		return uid;
	}
}
