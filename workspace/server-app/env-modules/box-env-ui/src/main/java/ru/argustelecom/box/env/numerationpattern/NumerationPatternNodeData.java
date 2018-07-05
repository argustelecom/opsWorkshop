package ru.argustelecom.box.env.numerationpattern;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.numerationpattern.NumerationPatternFrameModel.NodeType;

@Getter
@NoArgsConstructor
public class NumerationPatternNodeData {
	@Setter
	private NumerationPatternDto numerationPatternDto;
	private TypeDto typeDto;
	@Setter
	private boolean edit;
	private NodeType nodeType;

	@Builder
	public NumerationPatternNodeData(NumerationPatternDto numerationPatternDto, TypeDto typeDto, boolean edit,
			NodeType nodeType) {
		this.numerationPatternDto = numerationPatternDto;
		this.typeDto = typeDto;
		this.edit = edit;
		this.nodeType = nodeType;
	}

	public String getNodeName() {
		return isTypeNodeData() ? typeDto.getName() : nodeType.getName();
	}

	public boolean isTypeNodeData() {
		return typeDto != null;
	}
}
