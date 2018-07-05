package ru.argustelecom.box.env.report.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.join;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Strings;
import com.haulmont.yarg.structure.ReportFieldFormat;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportFieldFormatImpl;

import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.report.api.data.format.ReportBandDef;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;
import ru.argustelecom.box.env.report.api.data.format.ReportImageFormat;
import ru.argustelecom.box.env.util.ReflectionUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public class ReportMetadata {

	private ReportBuilder reportBuilder;
	private ReportContext reportContext;
	private Map<String, Set<Class<? extends ReportData>>> inspectedClasses = new HashMap<>();

	public ReportMetadata(ReportBuilder reportBuilder, ReportContext reportContext) {
		this.reportBuilder = reportBuilder;
		this.reportContext = reportContext;
	}

	@SuppressWarnings("el-syntax")
	public static String createNextLevelQuery(String query, String bandName) {
		return join(query, "[?(@.id=='${", bandName, ".id}')]");
	}

	@SuppressWarnings("el-syntax")
	public static String createImageFormat(int width, int height) {
		return String.format("${bitmap:%dx%d}", width, height);
	}

	public static String getQueryName(String bandName) {
		return bandName + "_Query";
	}

	public static String getDataName(String bandName) {
		return bandName + "_Data";
	}

	public void createMetadata() {
		reportContext.forEach((bandName, reportData) -> {
			String query = join("parameter=", getDataName(bandName), " $");
			String queryQualifier = createNextLevelQuery(query, bandName);

			ReportData dataSample = !reportData.isEmpty() ? reportData.get(0) : null;
			Class<? extends ReportData> dataClass = dataSample != null ? dataSample.getClass() : ReportData.class;

			BandBuilder band = createBandBuilder(bandName, query);
			introspect(bandName, bandName, band, queryQualifier, dataClass, dataSample);
			reportBuilder.band(band.build());
		});
	}

	protected void introspect(String fieldQualifier, String parentBandName, BandBuilder parentBand, String queryQualifier,
			Class<? extends ReportData> dataClass, ReportData dataSample) {

		Set<Class<? extends ReportData>> inspectedClassesPerBand = inspectedClasses.get(parentBandName);
		if (inspectedClassesPerBand == null) {
			inspectedClassesPerBand = new HashSet<>();
			inspectedClasses.put(parentBandName, inspectedClassesPerBand);
		}
		if (inspectedClassesPerBand.contains(dataClass)) {
			return;
		}

		inspectedClassesPerBand.add(dataClass);
		List<Field> fields = ReflectionUtils.getFields(dataClass);
		for (Field field : fields) {
			String qualifiedFieldName = join(fieldQualifier, ".", field.getName());
			String qualifiedQuery = join(queryQualifier, ".", field.getName());

			IntrospectionInfo info = new IntrospectionInfo(field, dataSample);

			if (info.isReportData()) {

				introspect(qualifiedFieldName, parentBandName, parentBand, qualifiedQuery, info.getReportDataClass(),
						info.getReportDataSample());

			} else if (info.isReportDataList()) {

				String bandName = checkNotNull(info.getBandName());
				String bandQuery = join(qualifiedQuery, "[*]");
				String bandQueryQualifier = createNextLevelQuery(qualifiedQuery, bandName);

				BandBuilder band = createBandBuilder(bandName, bandQuery);
				introspect(qualifiedFieldName, bandName, band, bandQueryQualifier, info.getReportDataClass(),
						info.getReportDataSample());
				parentBand.child(band.build());

			} else {

				if (info.hasDataFormat()) {
					createFieldFormat(qualifiedFieldName, info.getDataFormat());
				}

			}
		}
	}

	protected BandBuilder createBandBuilder(String bandName, String bandQuery) {
		BandBuilder band = new BandBuilder();
		band.name(bandName);
		band.query(getQueryName(bandName), bandQuery, "json");
		return band;
	}

	protected ReportFieldFormat createFieldFormat(String qualifiedFieldName, String fieldFormat) {
		ReportFieldFormat format = new ReportFieldFormatImpl(qualifiedFieldName, fieldFormat);
		reportBuilder.format(format);
		return format;
	}

	protected static class IntrospectionInfo {

		private Object dataSample;
		private Class<?> dataClass;
		private Type dataType;
		private String memberName;

		private ReportImageFormat imageFormat;
		private ReportDataFormat dataFormat;
		private ReportBandDef bandDef;

		public IntrospectionInfo(Field initialField, Object initialData) {
			dataClass = initialField.getType();
			dataType = initialField.getGenericType();
			memberName = initialField.getName();

			if (initialData != null) {
				try {
					dataSample = initialField.get(initialData);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new SystemException(e);
				}
			}

			if (initialField.isAnnotationPresent(ReportImageFormat.class)) {
				imageFormat = initialField.getAnnotation(ReportImageFormat.class);
			}

			if (initialField.isAnnotationPresent(ReportDataFormat.class)) {
				dataFormat = initialField.getAnnotation(ReportDataFormat.class);
			}

			if (initialField.isAnnotationPresent(ReportBandDef.class)) {
				bandDef = initialField.getAnnotation(ReportBandDef.class);
			}
		}

		public boolean isReportData() {
			return dataSample instanceof ReportData || ReportData.class.isAssignableFrom(dataClass);
		}

		public ReportData toReportData() {
			checkState(isReportData());
			return (ReportData) dataSample;
		}

		public boolean isReportDataList() {
			return dataSample instanceof ReportDataList || ReportDataList.class.isAssignableFrom(dataClass);
		}

		public ReportDataList<?> toReportDataList() {
			checkState(isReportDataList());
			return (ReportDataList<?>) dataSample;
		}

		public boolean isReportDataImage() {
			return dataSample instanceof ReportDataImage || ReportDataImage.class.isAssignableFrom(dataClass);
		}

		public ReportDataImage toReportDataImage() {
			checkState(isReportDataImage());
			return (ReportDataImage) dataSample;
		}

		public boolean hasDataFormat() {
			return isReportDataImage() || imageFormat != null || dataFormat != null;
		}

		public String getDataFormat() {
			if (isReportDataImage()) {
				ReportDataImage image = toReportDataImage();
				if (image != null) {
					return ReportMetadata.createImageFormat(image.getWidth(), image.getHeight());
				}
			}

			if (imageFormat != null) {
				return ReportMetadata.createImageFormat(imageFormat.width(), imageFormat.height());
			}

			if (dataFormat != null && !Strings.isNullOrEmpty(dataFormat.value())) {
				return dataFormat.value();
			}

			throw new IllegalStateException("Unable to determine data format");
		}

		public String getBandName() {
			if (isReportDataList()) {
				if (bandDef != null && !Strings.isNullOrEmpty(bandDef.name())) {
					return bandDef.name();
				}
				return capitalize(memberName);
			}
			return null;
		}

		public ReportData getReportDataSample() {
			if (isReportData()) {
				return (ReportData) dataSample;
			}

			if (isReportDataList()) {
				ReportDataList<?> dataList = (ReportDataList<?>) dataSample;
				if (dataList != null && !dataList.isEmpty()) {
					return dataList.get(0);
				}
			}

			return null;
		}

		@SuppressWarnings("unchecked")
		public Class<? extends ReportData> getReportDataClass() {
			if (isReportData()) {
				return (Class<? extends ReportData>) dataClass;
			}

			if (isReportDataList()) {
				checkState(dataType instanceof ParameterizedType);
				ParameterizedType dataListType = (ParameterizedType) dataType;
				Type itemType = dataListType.getActualTypeArguments()[0];
				checkState(itemType instanceof Class<?>);
				return (Class<? extends ReportData>) itemType;
			}

			throw new IllegalStateException("Data sample class is not instance of ReportData");
		}
	}
}
