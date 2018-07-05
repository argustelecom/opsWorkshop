package ru.argustelecom.box.inf.page;

import static com.google.common.base.Preconditions.checkState;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.google.common.collect.ImmutableMap;

import ru.argustelecom.box.inf.nls.LocaleUtils;

@Named
@RequestScoped
public class LanguageBean {
	
	// @formatter:off
	private static final Map<Locale, String> localeIcons = ImmutableMap.of(
		new Locale("ru", "RU"), "ru_RU",
		Locale.ENGLISH, "en_UK", 
		new Locale("lv"), "lv_LV"
	);
	// @formatter:on

	public String getDisplayName(Locale locale) {
		return LocaleUtils.getDisplayName(locale);
	}

	public Map<Locale, String> getLocaleIcons() {
		return localeIcons;
	}

	public Set<Locale> getSupportedLocales() {
		return localeIcons.keySet();
	}

	public Locale getLocale() {
		Locale current = LocaleUtils.getCurrentLocale();
		checkState(current != null);
		checkViewRoot(current);
		return current;
	}

	private void checkViewRoot(Locale current) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale viewRootLocale = ctx.getViewRoot().getLocale();
		if (viewRootLocale == null || !Objects.equals(viewRootLocale, current)) {
			ctx.getViewRoot().setLocale(current);
		}
	}
}