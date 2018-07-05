package ru.argustelecom.box.env.numerationpattern;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.TreeNode;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Named;
import java.io.Serializable;

@Named("numerationPatternFs")
@PresentationState
public class NumerationPatternFrameState implements Serializable {

	@Getter
	@Setter
	private TreeNode root;

	private static final long serialVersionUID = 7770363041498090751L;
}
