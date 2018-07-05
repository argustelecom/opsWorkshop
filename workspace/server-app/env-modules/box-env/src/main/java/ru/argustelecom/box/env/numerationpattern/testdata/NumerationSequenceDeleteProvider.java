package ru.argustelecom.box.env.numerationpattern.testdata;

import ru.argustelecom.box.env.numerationpattern.NumerationPatternRepository;
import ru.argustelecom.box.env.numerationpattern.NumerationSequenceRepository;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class NumerationSequenceDeleteProvider implements TestDataProvider {

    public static final String SEQUENCE_NAME = "numeration.sequence.delete.provider.sequence.name";

    @Inject
    private NumerationSequenceRepository numerationSequenceRepository;

    @Inject
    private NumerationPatternRepository numerationPatternRepository;

    @Override
    public void provide(TestRunContext testRunContext) {

        List<NumerationSequence> allSequences = numerationSequenceRepository.getAllSequences();

        Optional<NumerationSequence> optionalSequence = allSequences
                .stream()
                .filter(seq -> numerationPatternRepository.canBeDeleted(seq.getName()))
                .findAny();

        if (optionalSequence.isPresent()) {
            testRunContext.setBusinessPropertyWithMarshalling(SEQUENCE_NAME, optionalSequence.get().getName());
            return;
        }

        NumerationSequence sequence = numerationSequenceRepository.createNumerationSequence(
                "TestSequence",
                1L,
                1L,
                5,
                NumerationSequence.PeriodType.DAY
        );
        testRunContext.setBusinessPropertyWithMarshalling(SEQUENCE_NAME, sequence.getName());
    }
}