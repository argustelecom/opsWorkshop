package ru.argustelecom.box.env.report.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.google.common.base.Strings;

import lombok.ToString;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@ToString
public class ReportContext {

	private Map<String, ReportDataList<? extends ReportData>> bands = new HashMap<>();

	public <T extends ReportData> void put(String bandName, T data) {
		checkBandUnique(bandName);
		checkBandData(data);
		ReportDataList<T> dataList = new ReportDataList<>();
		dataList.add(data);
		bands.put(bandName, dataList);
	}

	public <T extends ReportData> void put(String bandName, Iterable<T> data) {
		checkBandUnique(bandName);
		checkBandData(data);
		ReportDataList<T> dataList = new ReportDataList<>();
		for (T dataItem : data) {
			dataList.add(dataItem);
		}
		bands.put(bandName, dataList);
	}

	public <T extends ReportData> void put(String bandName, ReportDataList<T> data) {
		checkBandUnique(bandName);
		checkBandData(data);
		bands.put(bandName, data);
	}

	protected void checkBandUnique(String bandName) {
		checkArgument(!Strings.isNullOrEmpty(bandName), "BandName is required");
		checkArgument(!bands.containsKey(bandName), LocaleUtils.format("Band with name {0} already exists", bandName));
	}

	protected <T extends ReportData> void checkBandData(Iterable<T> data) {
		checkArgument(data != null, "BandData list is required");
		data.forEach(this::checkBandData);
	}

	protected <T extends ReportData> void checkBandData(T data) {
		checkArgument(data != null, "BandData item is required");
	}

	public Map<String, ReportDataList<? extends ReportData>> getBands() {
		return bands;
	}

	public void forEach(BiConsumer<String, ReportDataList<? extends ReportData>> action) {
		Objects.requireNonNull(action);
		for (Entry<String, ReportDataList<? extends ReportData>> band : bands.entrySet()) {
			action.accept(band.getKey(), band.getValue());
		}
	}
}
