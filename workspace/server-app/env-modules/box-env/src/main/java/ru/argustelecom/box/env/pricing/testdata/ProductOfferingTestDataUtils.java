package ru.argustelecom.box.env.pricing.testdata;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.pricing.ProductOfferingRepository;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

import static com.google.common.base.Preconditions.checkState;

public class ProductOfferingTestDataUtils implements Serializable {

	@Inject
	private ProductOfferingRepository productOfferingRepository;

	private static final long serialVersionUID = -1052086969274917236L;

	/**
	 * Метод необходимо использовать тогда, когда нужно гарантировать все параметры предложения
	 */
	public PeriodProductOffering createTestPeriodProductOffering(
			CommonPricelist commonPricelist, ProductType productType, RecurrentTerms recurrentTerms, Money price
	) {
		// вот amount -- где-то Long, где-то int, где-то BigDecimal.
		// Currency -- где-то свой тип, где-то тип из java.util. Как не сломать голову в модели box?
		// хотелось брать код валюты из ru.argustelecom.box.env.stl.Currency.RUB.code. Но ..
		Currency currency = Currency.getInstance("RUB");
		return productOfferingRepository.createPeriodProductOffering(
				commonPricelist,
				productType,
				price,
				currency,
				12L,
				PeriodUnit.MONTH,
				recurrentTerms
		);
	}

	/**
	 * Метод необходимо использовать, когда нам не важны параметры предложения и подойдет любое
	 */
	public PeriodProductOffering findOrCreateTestPeriodProductOffering(
			CommonPricelist commonPricelist, ProductType productType, RecurrentTerms recurrentTerms, Money price
	) {
		PeriodProductOffering productOffering = findAnyPeriodProductOffering(commonPricelist);
		if (productOffering != null) {
			return productOffering;
		}

		return createTestPeriodProductOffering(commonPricelist, productType, recurrentTerms, price);
	}

	private @Nullable PeriodProductOffering findAnyPeriodProductOffering(CommonPricelist commonPricelist) {
		List<ProductOffering> productOfferings = productOfferingRepository.findProductOfferings(commonPricelist);
		if (productOfferings.isEmpty()) {
			return null;
		}

		// есть шальной вариант, что кто-то пошел и подло навесил на наш тестовый прайс-лист неподходящее нам ручное
		// единовременное предложение. Грязный хакер. Ругаемся. Если случится, надо доработать
		// productOfferingRepository.findProductOfferings -> productOfferingRepository.findPeriodProductOfferings
		checkState(productOfferings.get(0) instanceof PeriodProductOffering);
		return  (PeriodProductOffering) productOfferings.get(0);
	}
}