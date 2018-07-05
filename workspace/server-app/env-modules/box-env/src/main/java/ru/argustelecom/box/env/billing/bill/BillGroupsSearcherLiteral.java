package ru.argustelecom.box.env.billing.bill;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;

public class BillGroupsSearcherLiteral extends AnnotationLiteral<BillGroupsSearcher> implements BillGroupsSearcher {

	private GroupingMethod value;

	public BillGroupsSearcherLiteral(GroupingMethod value) {
		this.value = value;
	}

	@Override
	public GroupingMethod value() {
		return value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return BillGroupsSearcher.class;
	}

	private static final long serialVersionUID = -1875724926696067977L;

}