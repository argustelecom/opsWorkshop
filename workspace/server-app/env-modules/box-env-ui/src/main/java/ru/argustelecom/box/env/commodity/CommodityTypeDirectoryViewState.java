package ru.argustelecom.box.env.commodity;

import java.io.Serializable;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "commodityTypeDirectoryVs")
@PresentationState
public class CommodityTypeDirectoryViewState implements Serializable {

	@Getter
	@Setter
	private CommodityTypeTreeNodeDto nodeDto;

	private static final long serialVersionUID = -4637598217422334345L;

}