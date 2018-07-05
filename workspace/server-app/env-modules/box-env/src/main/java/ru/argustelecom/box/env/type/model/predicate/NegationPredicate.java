package ru.argustelecom.box.env.type.model.predicate;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

class NegationPredicate implements TypePropertyPredicate {

	private TypePropertyPredicate predicate;

	NegationPredicate(TypePropertyPredicate predicate) {
		checkRequiredArgument(predicate, "predicate");
		this.predicate = predicate;
	}

	@Override
	public String render() {
		return "NOT(" + predicate.render() + ")";
	}
}
