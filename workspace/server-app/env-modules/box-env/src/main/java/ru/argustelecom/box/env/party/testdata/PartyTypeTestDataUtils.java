package ru.argustelecom.box.env.party.testdata;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.PartyTypeRepository;
import ru.argustelecom.box.env.party.model.PartyType;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;

public class PartyTypeTestDataUtils implements Serializable {

	private static final long serialVersionUID = 6703086845467116299L;

	private static final String KEYWORD = "TestPartyType";

	@Inject
	private PartyTypeRepository partyTypeRp;

	/**
	 * Безусловно создает новый тестовый тип участника. Гарантирует, что либо будет создан новый тип участника, либо
	 * будет брошено исключение. Никогда не вернет null
	 * 
	 * @param partyCategory
	 * @return
	 */
	public PartyType createDemoPartyType(PartyCategory partyCategory) {
		return partyTypeRp.createPartyType(
				"Тестовый тип участника", partyCategory, KEYWORD,
				"Тип участника, предназанчен для UI тестирования. Пожалуйста, не меняйте его keyword!"
		);
	}

	public PartyType createDemoPartyType() {
		return createDemoPartyType(PartyCategory.PERSON);
	}

	public PartyType findOrCreateTestPartyType(PartyCategory partyCategory) {
		return getOrElse(partyTypeRp.findPartyTypesBy(partyCategory), () -> createDemoPartyType(partyCategory));
	}

	public PartyType findOrCreateTestPartyType() {
		return findOrCreateTestPartyType(PartyCategory.PERSON);
	}
}