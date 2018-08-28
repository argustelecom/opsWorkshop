package ru.argustelecom.ops.workshop.dbmanagement;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 *
 * @author t.vildanov
 */
@ApplicationScoped
public class ManagedDatabases implements Iterable<ManagedDatabase> {
	private List<ManagedDatabase> getDbList() {
		return null;
	}

	@Override
	public Iterator<ManagedDatabase> iterator() {
		List<ManagedDatabase> dbList = getDbList();
		return dbList.iterator();
	}

	@Override
	public void forEach(Consumer<? super ManagedDatabase> action) {
		for (ManagedDatabase db: this)
			action.accept(db);
	}

	@Override
	public Spliterator<ManagedDatabase> spliterator() {
		return null;
	}
}
