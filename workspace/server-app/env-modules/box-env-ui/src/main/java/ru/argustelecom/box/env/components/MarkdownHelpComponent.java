package ru.argustelecom.box.env.components;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.components.nls.MarkdownHelpMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesComponent(value="markdownHelp")
public class MarkdownHelpComponent extends UINamingContainer {

	private ResourceBundle bundle;

	public List<MarkdownHelpEntry> getMarkdownHelp() {
		MarkdownHelpMessagesBundle messages = LocaleUtils.getMessages(MarkdownHelpMessagesBundle.class);
		Parser p = Parser.builder().build();
		HtmlRenderer r = HtmlRenderer.builder().build();
		List<MarkdownHelpEntry> entries = new ArrayList<>();

		entries.add(helpEntry(messages.iCase1(), messages.iCase2(), p, r));
		entries.add(helpEntry(messages.bCase1(), messages.bCase2(), p, r));
		entries.add(helpEntry(messages.h1Case1(), messages.h1Case2(), p, r));
		entries.add(helpEntry(messages.h2Case1(), messages.h2Case2(), p, r));
		entries.add(helpEntry(messages.hrefCase1(), messages.hrefCase2(), p, r));
		entries.add(helpEntry(messages.imgCase1(), messages.imgCase2()));
		entries.add(helpEntry(messages.q(), null, p, r));
		entries.add(helpEntry(messages.p(), null, p, r));
		entries.add(helpEntry(messages.ulCase1(), messages.ulCase2(), p, r));
		entries.add(helpEntry(messages.olTab(), null, p, r));
		entries.add(helpEntry(messages.olCase1(), messages.olCase2(), p, r));
		entries.add(helpEntry(messages.hrCase1(), messages.hrCase2(),p, r));

		return unmodifiableList(entries);
	}

	private static MarkdownHelpEntry helpEntry(String case1, String case2) {
		return new MarkdownHelpEntry(case1, case2, null);
	}

	private static MarkdownHelpEntry helpEntry(String case1, String case2, Parser parser, HtmlRenderer renderer) {
		return new MarkdownHelpEntry(case1, case2, renderer.render(parser.parse(case1)));
	}

	public ResourceBundle getBundle() {
		if (bundle == null)
			return LocaleUtils.getBundle("MarkdownHelpBundle", getClass());

		return bundle;
	}

	@Getter
	@AllArgsConstructor
	public static class MarkdownHelpEntry {
		private String case1;
		private String case2;
		private String resultHtml;
	}
}
