package ru.argustelecom.box.env.report.impl.yarg;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.haulmont.yarg.loaders.ReportDataLoader;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.GroovyDataLoader;
import com.haulmont.yarg.loaders.impl.JsonDataLoader;
import com.haulmont.yarg.util.groovy.DefaultScriptingImpl;

@RequestScoped
public class LoaderFactory extends DefaultLoaderFactory {

	@Inject
	private ContainerManagedSqlDataLoader sqlDataLoader;

	@PostConstruct
	private void postConstruct() {
		setGroovyDataLoader(new GroovyDataLoader(new DefaultScriptingImpl()));
		setJsonDataLoader(new JsonDataLoader());
	}

	@Override
	public DefaultLoaderFactory setSqlDataLoader(ReportDataLoader dataLoader) {
		throw new UnsupportedOperationException("LoaderFactory.setSqlDataLoader is not supported");
	}

	@Override
	public ReportDataLoader createDataLoader(String loaderType) {
		return SQL_DATA_LOADER.equals(loaderType) ? sqlDataLoader : super.createDataLoader(loaderType);
	}
}
