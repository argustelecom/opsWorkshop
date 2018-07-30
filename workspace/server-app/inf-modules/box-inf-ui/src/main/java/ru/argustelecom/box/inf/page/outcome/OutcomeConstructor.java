package ru.argustelecom.box.inf.page.outcome;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class OutcomeConstructor {

	@Inject
	private Conversation conversation;

	public String construct(String viewId, OutcomeParam... params) {
		return construct(viewId, true, ConversationPropagationMode.IGNORE, params);
	}

	public String construct(String viewId, boolean facesRedirect, ConversationPropagationMode propagationMode,
			OutcomeParam... params) {

		StringBuilder outcome = new StringBuilder();
		outcome.append(viewId);

		boolean firstParam = true;

		if (params != null && params.length > 0) {
			for (OutcomeParam param : params) {
				firstParam = appendParamDelimiter(outcome, firstParam);
				outcome.append(param.toUriString());
			}
		}

		if (facesRedirect) {
			firstParam = appendParamDelimiter(outcome, firstParam);
			outcome.append("faces-redirect=true");
		}

		if (propagationMode != ConversationPropagationMode.IGNORE) {
			firstParam = appendParamDelimiter(outcome, firstParam);
			if (propagationMode == ConversationPropagationMode.CURRENT) {
				outcome.append("cid=");
				outcome.append(conversation.getId());
			} else {
				outcome.append("conversationPropagation=");
				outcome.append(propagationMode.getUriParamValue());
			}
		}

		return outcome.toString();
	}

	private boolean appendParamDelimiter(StringBuilder outcome, boolean firstParam) {
		outcome.append(firstParam ? "?" : "&");
		return false;
	}
}
