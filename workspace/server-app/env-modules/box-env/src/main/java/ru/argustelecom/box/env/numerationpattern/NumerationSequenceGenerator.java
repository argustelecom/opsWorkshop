package ru.argustelecom.box.env.numerationpattern;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class NumerationSequenceGenerator {

	@Inject
	private NumerationSequenceRepository numerationSequenceRepository;

	@Transactional(TxType.REQUIRES_NEW)
	public String getNextNumber(String seq) {
		NumerationSequence numerationSequence = numerationSequenceRepository.findNumerationSequenceByName(seq, true);
		return capacityFormat(numerationSequence.next(), numerationSequence.getCapacity());
	}

	public String getInitialValue(String seq) {
		NumerationSequence numerationSequence = numerationSequenceRepository.findNumerationSequenceByName(seq, false);
		return capacityFormat(numerationSequence.getInitialValue(), numerationSequence.getCapacity());
	}

	private String capacityFormat(Long value, Integer capacity) {
		if (capacity == null) {
			return value.toString();
		}
		return String.format("%0" + capacity + "d", value);
	}

}
