package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named(value = "tariffCardVm")
public class TariffCardViewModel extends ViewModel {

	private static final Logger log = Logger.getLogger(TariffCardViewModel.class);

	public static final String VIEW_ID = "/views/env/telephony/tariff/TariffCardView.xhtml";

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private TariffDtoTranslator tariffDtoTr;

	@Getter
	@Inject
	private CurrentTariff currentTariff;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private TariffCardViewState viewState;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	@Override
	public void preRender() {
		super.preRender();
		refresh();
	}

	public String removeTariff() {
		tariffAs.removeTariff(currentTariff.getValue().getId());
		return outcomeConstructor.construct("/views/env/telephony/tariff/TariffListView.xhtml");
	}

	private void refresh() {
		viewState.setTariffDto(tariffDtoTr.translate(currentTariff.getValue()));
		log.debugv("postConstruct. tariff_id={0}", currentTariff.getValue());
	}

	private static final long serialVersionUID = -141585564153086536L;
}
