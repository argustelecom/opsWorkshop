package ru.argustelecom.box.nri.coverage;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.building.BuildingElementDtoTranslator;
import ru.argustelecom.box.nri.building.BuildingElementTypeDto;
import ru.argustelecom.box.nri.building.BuildingElementTypeDtoTranslator;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDtoTranslator;
import ru.argustelecom.box.nri.resources.inst.ParameterValueDtoTranslator;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
public class ResourceInstallationDtoTranslatorTest {

	@Spy
	private ParameterValueDtoTranslator paramTranslator = new ParameterValueDtoTranslator();
	@Spy
	private ResourceSpecificationDtoTranslator specTranslator = new ResourceSpecificationDtoTranslator();

	@Spy
	private BuildingElementTypeDtoTranslator typeTranslator = new BuildingElementTypeDtoTranslator();

	@Spy
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator = new LogicalResourceDtoTranslator();

	@Spy
	@InjectMocks
	private ResourceInstanceDtoTranslator resourceDtoTranslator = new ResourceInstanceDtoTranslator();

	@Spy
	@InjectMocks
	private BuildingElementDtoTranslator elementDtoTranslator = new BuildingElementDtoTranslator();

	@Mock
	private PhoneNumberDtoTranslator phoneTranslator;

	@InjectMocks
	ResourceInstallationDtoTranslator translator;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void shouldTranslateNull() throws Exception {
		assertNull(translator.translate(null));
	}
	@Test
	public void shouldTranslate() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build()).build();

		ResourceInstallationDto dto = translator.translate(installation);

		assertNotNull(dto);
		assertNotNull(dto.getInstalledAt());
		assertEquals(new Long(2L),dto.getInstalledAt().getId());
		assertNotNull(dto.getCover());
		assertEquals(1,dto.getCover().size());
		assertEquals(new Long(3L),dto.getCover().get(0).getId());

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitInstallationElementNullDto() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build()).build();
		translator.initInstallationElement(null,installation);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitInstallationElementNullInstallation() throws Exception {
		translator.initInstallationElement(ResourceInstallationDto.builder().id(1L).build(),null);
	}
	@Test
	public void shouldInitInstallationElement() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.build();
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(2L).comment("comment").build();
		translator.initInstallationElement(dto,installation);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitCoverageNullDto() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build()).build();
		translator.initCoverage(null,installation);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitCoverageNullCoverage() throws Exception {
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(2L).comment("comment").build();
		translator.initCoverage(dto,null);
	}

	@Test
	public void shouldInitCoverage() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build()).build();
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(1L).comment("comment").build();
		dto = translator.initCoverage(dto,installation);
		assertNotNull(dto.getCover());
		assertEquals(1,dto.getCover().size());
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitResourceNullDto() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.resource(ResourceInstance.builder().id(1L).build())
				.build();
		translator.initResource(null,installation);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotInitResourceNullCoverage() throws Exception {
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(2L).comment("comment").build();
		translator.initResource(dto,null);
	}

	@Test
	public void shouldInitResource() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build())
				.resource(ResourceInstance.builder().id(1L).specification(ResourceSpecification.builder().id(1L).name("spec").build())
						.logicalResources(new ArrayList<>()).build()).build();
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(1L).comment("comment").build();
		dto = translator.initResource(dto,installation);
		assertNotNull(dto.getResource());
	}

	@Test
	public void shouldInitCoverageEmptyList() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L).build();
		ResourceInstallationDto dto = ResourceInstallationDto.builder().id(1L).comment("comment")
				.cover(Lists.newArrayList(BuildingElementDto.builder().name("building").type(BuildingElementTypeDto.builder().id(2L).build()).build()))
				.build();
		dto = translator.initCoverage(dto,installation);
		assertNotNull(dto.getCover());
		assertEquals(0,dto.getCover().size());
	}
}