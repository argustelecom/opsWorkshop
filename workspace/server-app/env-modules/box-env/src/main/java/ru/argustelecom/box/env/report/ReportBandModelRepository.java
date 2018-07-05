package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.haulmont.yarg.structure.BandOrientation;

import ru.argustelecom.box.env.report.model.DataLoaderType;
import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.util.HasParent;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ReportBandModelRepository implements Serializable {

	private static final Pattern VALID_KEYWORD_SYMBOLS = Pattern.compile("[a-zA-Z_0-9]*");

	@PersistenceContext
	private EntityManager em;

	/**
	 * Создает полосу, дочернюю от parentBand
	 * 
	 * @param type
	 *            тип отчета
	 * @param parentBand
	 *            родительская полоса для создаваемой полосы
	 * @param keyword
	 *            ключевое слово, по котому нужно обращаться к полосе. Может содержать латинские символы, символ
	 *            подчеркивания и цифры
	 * @return
	 */
	public ReportBandModel createBand(ReportType type, DataLoaderType dataLoaderType, ReportBandModel parentBand,
			String keyword, BandOrientation orientation) {
		checkNotNull(type);
		checkNotNull(dataLoaderType);
		checkArgument(Objects.equals(type.getRootBand(), ofNullable(parentBand).map(HasParent::findRoot).orElse(null)));
		checkArgument(!Objects.isNull(keyword) && VALID_KEYWORD_SYMBOLS.matcher(keyword).matches());

		ReportBandModel band = new ReportBandModel(MetadataUnit.generateId(em));

		band.setDataLoaderType(dataLoaderType);
		band.setKeyword(keyword);
		band.setOrientation(orientation);
		band.changeParent(parentBand);

		// нужно для того, чтобы в зависимости от переданного dataLoaderType значение скрипта заполнилось обязательным
		// значением.
		band.setQuery(null);

		em.persist(band);

		return band;
	}

	private static final long serialVersionUID = -8636517177844394361L;
}
