package ru.argustelecom.box.env.report.impl;

import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.structure.ReportFieldFormat;
import com.haulmont.yarg.structure.ReportParameter;
import com.haulmont.yarg.structure.ReportTemplate;
import com.haulmont.yarg.structure.impl.ReportBuilder;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.box.env.report.api.data.ReportDataImage.ImageFormat;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.report.api.data.format.ReportBandDef;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;
import ru.argustelecom.box.env.report.api.data.format.ReportImageFormat;
import ru.argustelecom.box.env.report.impl.ReportMetadata.IntrospectionInfo;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

public class ReportMetadataTest {

	private ReportBuilderMock reportBuilder;
	private ComplexReportData rdo;
	private Long idSequence;

	@Before
	public void setup() {
		idSequence = 1L;
		reportBuilder = new ReportBuilderMock();
		rdo = createComplexRdo();
	}

	@After
	public void cleanup() {
		rdo = null;
		reportBuilder = null;
		idSequence = null;
	}

	// ************************************************************************************************************
	// IntrospectionInfo Tests
	// ************************************************************************************************************

	@Test
	public void shouldDetectNonNullReportData() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "nested");

		assertThatReportDataFieldIntrospected(info, ReportDataWithRef.class, rdo.getNested());
	}

	@Test
	public void shouldDetectNullReportData() {
		rdo.setNested(null);
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "nested");

		assertThatReportDataFieldIntrospected(info, ReportDataWithRef.class, null);
	}

	@Test
	public void shouldDetectReportDataWhenRdoIsNull() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, null, "nested");

		assertThatReportDataFieldIntrospected(info, ReportDataWithRef.class, null);
	}

	private void assertThatReportDataFieldIntrospected(IntrospectionInfo info, Class<?> sampleClass, Object sample) {
		assertThat(info.isReportData(), is(true));
		assertThat(info.isReportDataImage(), is(false));
		assertThat(info.isReportDataList(), is(false));
		assertThat(info.hasDataFormat(), is(false));

		assertThat(info.getReportDataClass(), is(equalTo(sampleClass)));

		if (sample != null) {
			assertThat(info.toReportData(), is(notNullValue()));
			assertThat(info.getReportDataSample(), is(equalTo(sample)));
		} else {
			assertThat(info.toReportData(), is(nullValue()));
			assertThat(info.getReportDataSample(), is(nullValue()));
		}
	}

	@Test
	public void shouldDetectNonNullReportDataList() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "list");

		assertThatReportDataListFieldIntrospected(info, SimpleReportData.class, rdo.getList().get(0));
	}

	@Test
	public void shouldDetectNullReportDataList() {
		rdo.setList(null);
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "list");

		assertThatReportDataListFieldIntrospected(info, SimpleReportData.class, null);
	}

	@Test
	public void shouldDetectReportDataListWhenRdoIsNull() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, null, "list");

		assertThatReportDataListFieldIntrospected(info, SimpleReportData.class, null);
	}

	private void assertThatReportDataListFieldIntrospected(IntrospectionInfo info, Class<?> sampleClass, Object sample) {
		assertThat(info.isReportData(), is(false));
		assertThat(info.isReportDataImage(), is(false));
		assertThat(info.isReportDataList(), is(true));
		assertThat(info.hasDataFormat(), is(false));

		assertThat(info.getReportDataClass(), is(equalTo(sampleClass)));

		if (sample != null) {
			assertThat(info.toReportDataList(), is(notNullValue()));
			assertThat(info.getReportDataSample(), is(equalTo(sample)));
		} else {
			assertThat(info.toReportDataList(), is(nullValue()));
			assertThat(info.getReportDataSample(), is(nullValue()));
		}
	}

	@Test
	public void shouldDetectRuntimeImage() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "imageRuntime");

		assertThatReportDataImageHasExpectedFormat(info, 250, 250);
	}

	@Test
	public void shouldDetectDesigntimeImageAndIgnoreAnnotationPropertiesIfImageIsNotNull() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "imageDesigntime");

		assertThatReportDataImageHasExpectedFormat(info, 250, 250);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailWhenRuntimeImageIsNullAndGetFormatCalled() {
		rdo.setImageRuntime(null);
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "imageRuntime");

		assertThat(info.isReportDataImage(), is(true));
		assertThat(info.hasDataFormat(), is(true));

		info.getDataFormat();
		fail("IllegalStateException not occured when image is null and IntrospectionInfo#getDataFormat() called");
	}

	@Test
	public void shouldUseAnnotationPropertiesIfDesigntimeImageIsNull() {
		rdo.setImageDesigntime(null);
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "imageDesigntime");

		assertThatReportDataImageHasExpectedFormat(info, 100, 100);
	}

	private void assertThatReportDataImageHasExpectedFormat(IntrospectionInfo info, int width, int height) {
		assertThat(info.isReportDataImage(), is(true));
		assertThat(info.hasDataFormat(), is(true));
		assertThat(info.getDataFormat(), is(equalTo(ReportMetadata.createImageFormat(width, height))));
	}

	@Test
	public void shouldDetectDataFormat() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "formatted");

		assertThat(info.hasDataFormat(), is(true));
		assertThat(info.getDataFormat(), is(equalTo("##.##")));
	}

	@Test
	public void shouldDetectReportBand() {
		IntrospectionInfo info = createIntrospectionInfo(ComplexReportData.class, rdo, "list");
		assertThat(info.getBandName(), is(equalTo("Simples")));

		info = createIntrospectionInfo(ReportDataWithList.class, rdo.getDataWithList(), "list");
		assertThat(info.getBandName(), is(equalTo("List")));

		info = createIntrospectionInfo(ComplexReportData.class, rdo, "nested");
		assertThat(info.getBandName(), is(nullValue()));
	}

	// ************************************************************************************************************
	// ReportMetadata Tests
	// ************************************************************************************************************

	@Test
	public void shouldFindFieldsWithInherited() {
		List<Field> fields = ru.argustelecom.box.env.util.ReflectionUtils.getFields(ComplexReportData.class);

		// inherited field
		assertThatFieldExists(fields, "id");

		// declared fields
		assertThatFieldExists(fields, "nested");
		assertThatFieldExists(fields, "dataWithList");
		assertThatFieldExists(fields, "list");
		assertThatFieldExists(fields, "imageRuntime");
		assertThatFieldExists(fields, "imageDesigntime");
		assertThatFieldExists(fields, "formatted");
	}

	private void assertThatFieldExists(List<Field> fields, String fieldName) {
		for (Field field : fields) {
			if (Objects.equals(fieldName, field.getName())) {
				return;
			}
		}
		fail("Required Field " + fieldName + " not found");
	}

	@Test
	public void shouldBuildReportMetadata() {
		ReportContext reportContext = new ReportContext();
		reportContext.put("Complex", rdo);

		new ReportMetadata(reportBuilder, reportContext).createMetadata();

		assertThatMetadataHasExpectedStructure(reportBuilder);
	}

	@Test
	public void shouldBuildReportMetadataWithEmptyRdo() {
		ComplexReportData emptyRdo = new ComplexReportData(rdo.getId());
		emptyRdo.setImageRuntime(rdo.getImageRuntime());
		emptyRdo.setImageDesigntime(rdo.getImageDesigntime());

		ReportContext reportContext = new ReportContext();
		reportContext.put("Complex", emptyRdo);

		new ReportMetadata(reportBuilder, reportContext).createMetadata();

		assertThatMetadataHasExpectedStructure(reportBuilder);
	}

	@SuppressWarnings("el-syntax")
	private void assertThatMetadataHasExpectedStructure(ReportBuilderMock reportBuilder) {
		assertThat(reportBuilder.getBands().size(), is(1));
		assertThat(reportBuilder.getBands().get(0).getChildren().size(), is(2));

		//@formatter:off
		assertThatBandCreated(
			reportBuilder.getBands(), 
			"Complex", 
			"parameter=Complex_Data $"
		);
		
		assertThatBandCreated(
			reportBuilder.getBands().get(0).getChildren(), 
			"List",
			"parameter=Complex_Data $[?(@.id=='${Complex.id}')].dataWithList.list[*]"
		);
		
		assertThatBandCreated(
			reportBuilder.getBands().get(0).getChildren(), 
			"Simples", 
			"parameter=Complex_Data $[?(@.id=='${Complex.id}')].list[*]"
		);
		//@formatter:on

		assertThatFormatCreated(reportBuilder.getFormats(), "Complex.imageRuntime", "${bitmap:250x250}");
		assertThatFormatCreated(reportBuilder.getFormats(), "Complex.imageDesigntime", "${bitmap:250x250}");
		assertThatFormatCreated(reportBuilder.getFormats(), "Complex.formatted", "##.##");
	}

	private void assertThatBandCreated(List<ReportBand> bands, String bandName, String bandQuery) {
		for (ReportBand band : bands) {
			if (Objects.equals(band.getName(), bandName)) {

				assertThat(band.getReportQueries().size(), is(1));
				assertThat(band.getReportQueries().get(0).getName(), is(equalTo(bandName + "_Query")));
				assertThat(band.getReportQueries().get(0).getScript(), is(equalTo(bandQuery)));
				return;

			}
		}
		fail("ReportBand with name " + bandName + " not found");
	}

	private void assertThatFormatCreated(List<ReportFieldFormat> formats, String fieldName, String formatString) {
		for (ReportFieldFormat format : formats) {
			if (Objects.equals(format.getName(), fieldName)) {

				assertThat(format.getFormat(), is(equalTo(formatString)));
				return;

			}
		}
		fail("ReportFieldFormat for field " + fieldName + " not found");
	}

	// ************************************************************************************************************
	// Helpers & Mocks
	// ************************************************************************************************************

	private Long nextId() {
		return idSequence++;
	}

	private <T extends ReportData> IntrospectionInfo createIntrospectionInfo(Class<T> sampleClass, T sample,
			String fieldName) {

		Field field = ReflectionUtils.getField(sampleClass, fieldName);
		checkState(field != null);
		return new IntrospectionInfo(field, sample);

	}

	private ComplexReportData createComplexRdo() {
		ComplexReportData rdo = new ComplexReportData(nextId());

		rdo.setNested(new ReportDataWithRef(nextId(), new SimpleReportData(nextId(), "Nested")));
		rdo.setDataWithList(new ReportDataWithList(nextId()));
		rdo.setFormatted("Simple field value");
		rdo.setImageRuntime(createImage());
		rdo.setImageDesigntime(createImage());

		rdo.getDataWithList().getList().add(new SimpleReportData(nextId(), "DataWithList # 1"));
		rdo.getDataWithList().getList().add(new SimpleReportData(nextId(), "DataWithList # 2"));
		rdo.getDataWithList().getList().add(new SimpleReportData(nextId(), "DataWithList # 3"));

		rdo.getList().add(new SimpleReportData(nextId(), "Data # 1"));
		rdo.getList().add(new SimpleReportData(nextId(), "Data # 2"));
		rdo.getList().add(new SimpleReportData(nextId(), "Data # 3"));
		rdo.getList().add(new SimpleReportData(nextId(), "Data # 4"));

		return rdo;
	}

	private ReportDataImage createImage() {
		return ReportDataImage.of(ImageFormat.PNG, 250, 250, new byte[] { 0, 1, 2, 3, 4, 5, 6 });
	}

	@Getter
	static class ReportBuilderMock extends ReportBuilder {

		private List<ReportBand> bands = new ArrayList<>();
		private List<ReportFieldFormat> formats = new ArrayList<>();

		public ReportBuilderMock() {
		}

		@Override
		public ReportBuilder band(ReportBand band) {
			bands.add(band);
			return this;
		}

		@Override
		public ReportBuilder format(ReportFieldFormat reportFieldFormat) {
			formats.add(reportFieldFormat);
			return this;
		}

		@Override
		public ReportBuilder template(ReportTemplate reportTemplate) {
			throw new UnsupportedOperationException("ReportBuilder#template is unsupported in testing environment");
		}

		@Override
		public ReportBuilder parameter(ReportParameter reportParameter) {
			throw new UnsupportedOperationException("ReportBuilder#parameter is unsupported in testing environment");
		}

		@Override
		public ReportBuilder name(String name) {
			throw new UnsupportedOperationException("ReportBuilder#name is unsupported in testing environment");
		}

		@Override
		public Report build() {
			throw new UnsupportedOperationException("ReportBuilder#build is unsupported in testing environment");
		}
	}

	@Getter
	@Setter
	static class SimpleReportData extends ReportData {

		private String name;

		public SimpleReportData(Long id, String name) {
			super(id);
			this.name = name;
		}
	}

	@Getter
	@Setter
	static class ReportDataWithRef extends ReportData {

		private SimpleReportData ref;

		public ReportDataWithRef(Long id, SimpleReportData ref) {
			super(id);
			this.ref = ref;
		}
	}

	@Getter
	@Setter
	static class ReportDataWithList extends ReportData {

		private ReportDataList<SimpleReportData> list = new ReportDataList<>();

		public ReportDataWithList(Long id) {
			super(id);
		}
	}

	@Getter
	@Setter
	static class ComplexReportData extends ReportData {

		private ReportDataWithRef nested;

		private ReportDataWithList dataWithList;

		@ReportBandDef(name = "Simples")
		private ReportDataList<SimpleReportData> list = new ReportDataList<>();

		private ReportDataImage imageRuntime;

		@ReportImageFormat
		private ReportDataImage imageDesigntime;

		@ReportDataFormat("##.##")
		private String formatted;

		public ComplexReportData(Long id) {
			super(id);
		}
	}

}
