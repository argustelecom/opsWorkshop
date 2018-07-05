package ru.argustelecom.box.env.numerationpattern;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.box.env.numerationpattern.parser.NumerationPatternParser;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "numerationSequenceEditDM")
@PresentationModel
public class NumerationSequenceEditDialogModel implements Serializable {

	private static final long serialVersionUID = -3133696186831465267L;

	@Inject
	private NumerationSequenceAppService numerationSequenceAppService;

	@Inject
	private NumerationPatternParser parser;

	@Getter
	private NumerationSequenceDto sequence;

	@Setter
	@Getter
	private List<NumerationPatternNodeData> numerationPatternNodeDataList;

	@Setter
	@Getter
	private boolean ignoreWarnings;

	private String warningMessage;

	public void open() {
		RequestContext.getCurrentInstance().update("sequence_edit_form-sequence_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('sequenceEditDlgVar').show()");
	}

	public void preRender() {
		if (sequence == null) {
			sequence = new NumerationSequenceDto();
		}
	}

	public boolean isEditMode() {
		return sequence.getId() != null;
	}

	public void submit() {
		if (isEditMode()) {
			edit();
		} else {
			create();
		}
		clear();
	}

	public void clear() {
		sequence = null;
		warningMessage = null;
	}

	public NumerationSequenceDto getEditableSequence() {
		return sequence;
	}

	public void setEditableSequence(NumerationSequenceDto sequence) {
		this.sequence = sequence;
	}

	public PeriodType[] getPeriodTypes() {
		return PeriodType.values();
	}

	private void create() {
		NumerationSequence np = numerationSequenceAppService.createNumerationSequence(sequence.getName(),
				sequence.getInitialValue(), sequence.getIncrement(), sequence.getCapacity(), sequence.getPeriod());
	}

	private void edit() {
		NumerationSequence np = numerationSequenceAppService.editNumerationSequence(sequence.getId(),
				sequence.getInitialValue(), sequence.getIncrement(), sequence.getCapacity(), sequence.getPeriod());
	}

	public String getInfo() {
		if (warningMessage != null) {
			return warningMessage;
		}
		return warningMessage = numerationPatternNodeDataList.stream().map(this::getNodeName).filter(Objects::nonNull)
				.collect(Collectors.joining(", "));
	}

	private String getNodeName(NumerationPatternNodeData nodeData) {
        return parser.parse(nodeData.getNumerationPatternDto().getClassName(),
						nodeData.getNumerationPatternDto().getPattern())
				.stream()
				.filter(statement -> statement.getType() == Statement.StatementType.SEQUENCE
						&& statement.getValue().equals(sequence.getName()))
                .findFirst().map(statement -> nodeData.getNodeName())
				.orElse(null);
	}
}
