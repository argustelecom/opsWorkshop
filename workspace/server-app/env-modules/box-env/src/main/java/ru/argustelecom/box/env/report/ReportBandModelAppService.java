package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.haulmont.yarg.structure.BandOrientation;

import ru.argustelecom.box.env.report.model.DataLoaderType;
import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ReportBandModelAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ReportBandModelRepository reportBandModelRp;

	public ReportBandModel create(Long reportTypeId, DataLoaderType dataLoaderType, Long parentId, String keyword,
			BandOrientation orientation) {
		checkNotNull(reportTypeId);
		checkNotNull(dataLoaderType);
		checkNotNull(parentId);
		checkNotNull(keyword);
		checkNotNull(orientation);

		ReportType reportType = em.find(ReportType.class, reportTypeId);
		ReportBandModel parent = em.find(ReportBandModel.class, parentId);

		return reportBandModelRp.createBand(reportType, dataLoaderType, parent, keyword, orientation);
	}

	public void remove(Long bandId) {
		checkNotNull(bandId);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);
		band.changeParent(null);

		em.remove(band);
	}

	public void changeBandKeyword(Long bandId, String keyword) {
		checkNotNull(bandId);
		checkNotNull(keyword);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);

		if (!band.getKeyword().equals(keyword)) {
			band.setKeyword(keyword);
		}
	}

	public void changeBandParent(Long bandId, Long parentId) {
		checkNotNull(bandId);
		checkNotNull(parentId);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);
		ReportBandModel parent = em.find(ReportBandModel.class, parentId);

		band.changeParent(parent);
	}

	public void changeBandQuery(Long bandId, String query) {
		checkNotNull(bandId);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);

		if (!Objects.equals(band.getQuery(), query)) {
			band.setQuery(query);
		}
	}

	public void changeBandDataLoaderType(Long bandId, DataLoaderType dataLoaderType) {
		checkNotNull(bandId);
		checkNotNull(dataLoaderType);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);

		if (!Objects.equals(band.getDataLoaderType(), dataLoaderType)) {
			band.setDataLoaderType(dataLoaderType);
		}
	}

	public void changeBandOrientation(Long bandId, BandOrientation orientation) {
		checkNotNull(bandId);
		checkNotNull(orientation);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);

		if (!Objects.equals(band.getOrientation(), orientation)) {
			band.setOrientation(orientation);
		}
	}

	public void changeOrdinalNumber(Long bandId, int newOrdinalNumber) {
		checkNotNull(bandId);
		checkNotNull(newOrdinalNumber);

		ReportBandModel band = em.find(ReportBandModel.class, bandId);
		band.changeOrdinalNumber(newOrdinalNumber);
	}

	public List<ReportBandModel> findChildren(Long bandId) {
		checkNotNull(bandId);

		return em.find(ReportBandModel.class, bandId).getChildren();
	}

	private static final long serialVersionUID = 7557381492358857349L;
}