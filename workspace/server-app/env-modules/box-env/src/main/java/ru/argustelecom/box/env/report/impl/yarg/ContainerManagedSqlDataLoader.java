package ru.argustelecom.box.env.report.impl.yarg;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.haulmont.yarg.loaders.ReportDataLoader;
import com.haulmont.yarg.loaders.impl.SqlDataLoader;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportQuery;

@RequestScoped
@Transactional(TxType.REQUIRES_NEW)
public class ContainerManagedSqlDataLoader implements ReportDataLoader {

	@Resource(lookup = "java:jboss/datasources/BoxReportDS")
	private DataSource dataSource;

	@Override
	public List<Map<String, Object>> loadData(ReportQuery query, BandData band, Map<String, Object> params) {
		return new SqlDataLoader(dataSource).loadData(query, band, params);
	}
}
