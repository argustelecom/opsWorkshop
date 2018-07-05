package ru.argustelecom.box.env.components;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.components.InputLookupArray.Property.items;

import java.util.List;

import javax.faces.component.FacesComponent;

import ru.argustelecom.box.env.type.model.lookup.LookupEntry;

@FacesComponent("inputLookupArray")
public class InputLookupArray extends AbstractCompositeInput {

	public List<LookupEntry> complete(String fragment) {

		List<LookupEntry> items = items();
		List<LookupEntry> value = getValue();

		if (fragment == null) {
			if (value != null) {
				items.removeAll(value);
			}
			return items;
		}

		//@formatter:off
		return items().stream()
				.filter(entry -> !ofNullable(value).filter(entries -> entries.contains(entry)).isPresent()
						&& entry.getObjectName().startsWith(fragment))
				.collect(toList());
		//@formatter:on
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<LookupEntry> getValue() {
		return (List<LookupEntry>) super.getValue();
	}

	@SuppressWarnings("unchecked")
	private List<LookupEntry> items() {
		return (List<LookupEntry>) getAttributes().get(items.toString());
	}

	protected enum Property {
		items
	}
}
