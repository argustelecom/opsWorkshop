package ru.argustelecom.box.env.telephony.tariff;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.JPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.JPQLConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.CUSTOMER;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.DTYPE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.ID;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.NAME;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.STATE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.VALID_FROM;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.VALID_TO;


@PresentationModel
public class TariffLazyDataModel extends
		JPQLConvertibleDtoLazyDataModel<AbstractTariff, TariffListDto, TariffQueryWrapper, TariffLazyDataModel.TariffSort> {

	@Inject
	private TariffListFilterModel tariffListFilterModel;

	@Inject
	private TariffListDtoTranslator tariffListDtoTr;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(TariffSort.id, ID);
		addPath(TariffSort.name, NAME);
		addPath(TariffSort.state, STATE);
		addPath(TariffSort.validFrom, VALID_FROM);
		addPath(TariffSort.validTo, VALID_TO);
		addPath(TariffSort.customer, CUSTOMER);
		addPath(TariffSort.type, DTYPE);
	}

	@Override
	protected Class<TariffSort> getSortableEnum() {
		return TariffSort.class;
	}

	@Override
	protected DefaultDtoTranslator<TariffListDto, AbstractTariff> getDtoTranslator() {
		return tariffListDtoTr;
	}

	@Override
	protected JPQLConvertibleDtoFilterModel<AbstractTariff, TariffQueryWrapper> getFilterModel() {
		return tariffListFilterModel;
	}

	public enum TariffSort {
		id, name, type, state, validFrom, validTo, customer
	}

	private static final long serialVersionUID = 7090547588547398473L;
}
