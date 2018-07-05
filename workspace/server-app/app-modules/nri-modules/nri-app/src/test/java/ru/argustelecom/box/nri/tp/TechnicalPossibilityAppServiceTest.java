package ru.argustelecom.box.nri.tp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.integration.nri.TechPossibility;
import ru.argustelecom.box.nri.building.BuildingElementRepository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.ResourceInstallationRepository;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;


/**
 * Created by s.kolyada on 04.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class TechnicalPossibilityAppServiceTest {

	@Mock
	private BuildingElementRepository elementRepository;

	@Mock
	private ResourceInstallationRepository installationRepository;

	@Mock
	private ResourceSchemaRepository schemaRepository;

	@InjectMocks
	private TechnicalPossibilityAppService service;

	private Building building;

	private Lodging lodging;

	private ServiceSpec serviceSpecification;

	private ResourceSpecification resourceSpecification;

	private ParameterSpecification parameterSpecification;

	private BuildingElement be = BuildingElement.builder()
			.id(1L)
			.name("some name")
			.build();

	private ResourceInstallation ri;

	@Before
	public void init() {
		building = new Building(1L);
		lodging = new Lodging(2L);
		lodging.setParent(building);

		parameterSpecification = ParameterSpecification.builder()
				.id(1111L)
				.name("parameter")
				.dataType(ParameterDataType.STRING)
				.build();

		resourceSpecification = ResourceSpecification.builder()
				.id(2L)
				.parameters(Arrays.asList(parameterSpecification))
				.build();

		serviceSpecification = ServiceSpec.builder()
				.id(2L).build();

		ParameterValue parameterValue = ParameterValue.builder()
				.id(67238L)
				.specification(parameterSpecification)
				.value("value")
				.build();

		ResourceInstance resource = ResourceInstance.builder()
				.id(1L)
				.specification(resourceSpecification)
				.parameterValues(Arrays.asList(parameterValue))
				.build();

		ri = ResourceInstallation.builder()
			.id(1L)
			.resource(resource)
			.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateInput() throws Exception {
		service.checkPossibility(serviceSpecification, null);
	}

	@Test
	public void shouldReturnNotEnoughData() {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(null);
		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.NOT_ENOUGH_DATA, res);
	}

	@Test
	public void shouldReturnNotAvailable() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.emptyList());

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.NOT_AVAILABLE, res);
	}

	@Test
	public void shouldReturnAvailableInBuilding() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.singletonList(ri));
		when(installationRepository.findInstallationsCoveringBuildingElement(eq(building))).thenReturn(Collections.emptyList());

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.AVAILABLE_IN_BUILDING, res);
	}

	@Test
	public void shouldReturnCoveredBySomeElement() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.singletonList(ri));
		when(installationRepository.findInstallationsCoveringBuildingElement(eq(building))).thenReturn(Collections.singletonList(ri));

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.COVERED_BY_SOME_RESOURCES, res);
	}

	@Test
	public void shouldReturnNotAvailableForLodging() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.emptyList());

		TechPossibility res = service.checkPossibility(serviceSpecification, lodging);
		assertEquals(TechPossibility.NOT_AVAILABLE, res);
	}

	@Test
	public void shouldFindPassingResource() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.singletonList(ri));
		when(installationRepository.findInstallationsCoveringBuildingElement(eq(building))).thenReturn(Collections.singletonList(ri));

		RequiredItem item = RequiredItem.builder()
				.id(4234234L)
				.resourceSpecification(resourceSpecification)
				.build();

		ResourceSchema schema1 = ResourceSchema.builder()
				.id(1L)
				.requirements(Arrays.asList(item))
				.build();
		List<ResourceSchema> schemas = new ArrayList<>();
		schemas.add(schema1);
		when(schemaRepository.findByServiceSpecification(any())).thenReturn(schemas);

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.FULL_AVAILABILITY, res);
	}

	@Test
	public void shouldFindPassingResourceWithParameters() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.singletonList(ri));
		when(installationRepository.findInstallationsCoveringBuildingElement(eq(building))).thenReturn(Collections.singletonList(ri));

		RequiredParameterValue reqParameterValue = RequiredParameterValue.builder()
				.id(431212L)
				.requiredValue("value")
				.parameterSpecification(parameterSpecification)
				.compareAction(CompareAction.EQUALS)
				.build();

		RequiredItem item = RequiredItem.builder()
				.id(4234234L)
				.resourceSpecification(resourceSpecification)
				.requiredParameters(Arrays.asList(reqParameterValue))
				.build();

		ResourceSchema schema1 = ResourceSchema.builder()
				.id(1L)
				.requirements(Arrays.asList(item))
				.build();
		List<ResourceSchema> schemas = new ArrayList<>();
		schemas.add(schema1);
		when(schemaRepository.findByServiceSpecification(any())).thenReturn(schemas);

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.FULL_AVAILABILITY, res);

		reqParameterValue.setRequiredValue("not value");
		res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.COVERED_BY_SOME_RESOURCES, res);
	}

	@Test
	public void shouldNotFindPassingResourceWithParametersDueDifferentParents() throws Exception {
		when(elementRepository.findElementByLocation(eq(building))).thenReturn(be);
		when(installationRepository.findAllInstallationsByBuilding(eq(be))).thenReturn(Collections.singletonList(ri));

		ParameterSpecification parameterSpecification1 = ParameterSpecification.builder()
				.id(1143351L)
				.name("parameter")
				.dataType(ParameterDataType.STRING)
				.build();

		ResourceSpecification resourceSpecification1 = ResourceSpecification.builder()
				.id(224456887L)
				.build();

		ResourceSpecification resourceSpecification2 = ResourceSpecification.builder()
				.id(22443456887L)
				.parameters(Arrays.asList(parameterSpecification1))
				.build();


		ParameterValue hasIptv = ParameterValue.builder()
				.id(67238L)
				.specification(parameterSpecification1)
				.value("value2")
				.build();

		ParameterValue noIptv = ParameterValue.builder()
				.id(67238L)
				.specification(parameterSpecification1)
				.value("value1112")
				.build();

		ResourceInstance port1 = ResourceInstance.builder()
				.id(1346L)
				.specification(resourceSpecification2)
				.parameterValues(Arrays.asList(noIptv))
				.build();

		ResourceInstance port2 = ResourceInstance.builder()
				.id(13467L)
				.specification(resourceSpecification2)
				.parameterValues(Arrays.asList(hasIptv))
				.build();

		ResourceInstance ethSwitch = ResourceInstance.builder()
				.id(123543L)
				.specification(resourceSpecification1)
				.children(Arrays.asList(port1, port2))
				.build();

		ResourceInstallation ri2 = ResourceInstallation.builder()
				.id(16753L)
				.resource(ethSwitch)
				.build();
		when(installationRepository.findInstallationsCoveringBuildingElement(eq(building))).thenReturn(Arrays.asList(ri,ri2));

		RequiredParameterValue reqParameterValue = RequiredParameterValue.builder()
				.id(431212L)
				.requiredValue("value2")
				.parameterSpecification(parameterSpecification1)
				.compareAction(CompareAction.EQUALS)
				.build();

		RequiredItem item = RequiredItem.builder()
				.id(4234234L)
				.resourceSpecification(resourceSpecification1)
				.requiredParameters(Collections.EMPTY_LIST)
				.children(Collections.singletonList(RequiredItem.builder()
						.id(4234223234L)
						.resourceSpecification(resourceSpecification2)
						.requiredParameters(Arrays.asList(reqParameterValue))
						.build()))
				.build();

		ResourceSchema schema1 = ResourceSchema.builder()
				.id(1L)
				.requirements(Arrays.asList(item))
				.build();
		List<ResourceSchema> schemas = new ArrayList<>();
		schemas.add(schema1);
		when(schemaRepository.findByServiceSpecification(any())).thenReturn(schemas);

		TechPossibility res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.FULL_AVAILABILITY, res);

		item = RequiredItem.builder()
				.id(4234234L)
				.resourceSpecification(resourceSpecification)
				.requiredParameters(Collections.EMPTY_LIST)
				.children(Collections.singletonList(RequiredItem.builder()
						.id(4234223234L)
						.resourceSpecification(resourceSpecification2)
						.requiredParameters(Arrays.asList(reqParameterValue))
						.build()))
				.build();
		schema1 = ResourceSchema.builder()
				.id(1L)
				.requirements(Arrays.asList(item))
				.build();
		schemas.clear();
		schemas.add(schema1);
		when(schemaRepository.findByServiceSpecification(any())).thenReturn(schemas);

		res = service.checkPossibility(serviceSpecification, building);
		assertEquals(TechPossibility.COVERED_BY_SOME_RESOURCES, res);
	}
}