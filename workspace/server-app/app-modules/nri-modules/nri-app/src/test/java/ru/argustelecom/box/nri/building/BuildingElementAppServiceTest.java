package ru.argustelecom.box.nri.building;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDtoTranslator;
import ru.argustelecom.box.nri.coverage.ResourceInstallationRepository;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;

import java.util.List;
import java.util.stream.LongStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.apache.commons.collections.CollectionUtils.union;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.wildfly.common.Assert.assertTrue;

/**
 * @author d.khekk
 * @since 01.09.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class BuildingElementAppServiceTest {

	@Mock
	private BuildingElementDtoTranslator translator = new BuildingElementDtoTranslator();

	@Mock
	private BuildingElementRepository repository;

	@Mock
	private BuildingElementTypeRepository typeRepository;

	@Mock
	private ResourceInstallationRepository resInstallationRepository;

	@Mock
	private ResourceInstallationDtoTranslator resInstallationTranslator;

	@Mock
	private LocationAppService locationAppService;

	@InjectMocks
	private BuildingElementAppService service;

	private static BuildingElementType BUILDING = BuildingElementType.builder().id(8L).name("Здание")
			.locationLevel(new LocationLevel(LocationLevel.BUILDING)).build();

	private static BuildingElementType APARTMENT = BuildingElementType.builder().id(9L).name("Квартира")
			.locationLevel(new LocationLevel(LocationLevel.LODGING)).build();

	private static BuildingElementTypeDto BUILDING_DTO = BuildingElementTypeDto.builder()
			.id(BUILDING.getId()).name("Здание").level(new LocationLevel(LocationLevel.BUILDING)).build();

	private static BuildingElementTypeDto APARTMENT_DTO = BuildingElementTypeDto.builder()
			.id(APARTMENT.getId()).name("Квартира").level(new LocationLevel(LocationLevel.LODGING)).build();

	private static BuildingElement defaultPersistedHome = BuildingElement.builder().type(BUILDING).build();
	private static BuildingElementDto defaultTranslatedHome = BuildingElementDto.builder().build();

	@Before
	public void before() {
		when(typeRepository.findOne(BUILDING.getId())).thenReturn(BUILDING);
		when(typeRepository.findOne(APARTMENT.getId())).thenReturn(APARTMENT);
		when(translator.translate(defaultPersistedHome)).thenReturn(defaultTranslatedHome);
	}

	@Test
	public void shouldFindElementByBuilding() throws Exception {
		Location homeAddress = new Location(1L);
		when(repository.findElementByLocation(homeAddress)).thenReturn(defaultPersistedHome);
		BuildingElementDto element = service.findElementByLocation(homeAddress);
		assertEquals(element, defaultTranslatedHome);
	}

	@Test
	public void shouldFindElementById() throws Exception {
		when(repository.findElementById(anyLong())).thenReturn(defaultPersistedHome);
		assertEquals(defaultPersistedHome, service.findElementById(777L));
	}

	@Test
	public void shouldCreateNewElement() throws Exception {
		BuildingElementDto home = BuildingElementDto.builder().name("Новый дом").type(BUILDING_DTO).build();

		when(repository.create(eq("Новый дом"), eq(BUILDING), any(), any())).thenReturn(defaultPersistedHome);

		BuildingElementDto newHome = service.createNewElement(home, null);
		assertEquals(newHome, defaultTranslatedHome);
	}

	@Test
	public void shouldDeleteElement() throws Exception {
		doNothing().when(repository).delete(anyLong());
		service.delete(1L);

		verify(repository, times(1)).delete(anyLong());
	}

	@Test
	public void shouldChangeElementParent() throws Exception {
		BuildingElementDto home = BuildingElementDto.builder().id(1L).build();
		BuildingElementDto otherHome = BuildingElementDto.builder().id(2L).build();
		doNothing().when(repository).changeParent(home.getId(), otherHome.getId());
		service.changeElementParent(home, otherHome);

		verify(repository, times(1)).changeParent(anyLong(), anyLong());
	}

	@Test
	public void shouldChangeNameAndType() throws Exception {
		when(repository.updateNameAndType(1L, "Дом в глуши", BUILDING)).thenReturn(defaultPersistedHome);

		BuildingElementDto changedHome = service.changeNameAndType(1L, "Дом в глуши", BUILDING.getId());
		assertEquals(changedHome, defaultTranslatedHome);
	}

	@Test
	public void shouldChangeLocation() throws Exception {
		Location newLocation = new Location(1L);
		when(repository.updateLocation(1L, newLocation)).thenReturn(defaultPersistedHome);

		BuildingElementDto changedHome = service.changeLocation(1L, newLocation);
		assertEquals(changedHome, defaultTranslatedHome);
	}

	@Test
	public void shouldGetFreeLodgings() throws Exception {
		BuildingElementDto home = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();

		// у нас будет 10 адресов по дому
		List<Lodging> possibleLodgings = LongStream.rangeClosed(1, 10).mapToObj(Lodging::new).collect(toList());
		// и два элемента строения с занятыми адресами
		List<Lodging> occupiedLodgings = possibleLodgings.stream().limit(2).collect(toList());
		occupiedLodgings.stream()
				.map(lodging -> BuildingElementDto.builder().location(lodging).childElements(newArrayList()).build())
				.forEachOrdered(home.getChildElements()::add);

		List<Lodging> freeLodgings = service.getFreeLodgings(possibleLodgings, home);
		assertTrue(isEqualCollection(possibleLodgings, union(freeLodgings, occupiedLodgings)));
	}

	@Test
	public void shouldCreateNewElementWithChildren() {
		BuildingElementDto apartment = BuildingElementDto.builder().name("Квартира").type(APARTMENT_DTO).build();
		BuildingElementDto home = BuildingElementDto.builder().name("Дом на Звездной").type(BUILDING_DTO).build();
		home.getChildElements().add(apartment);

		BuildingElement persistedApt = BuildingElement.builder().build();
		when(repository.create(eq(apartment.getName()), any(), any(), any())).thenReturn(persistedApt);
		when(repository.create(eq(home.getName()), any(), any(), any())).thenReturn(defaultPersistedHome);

		BuildingElementDto newHome = service.createNewElementWithChildren(home, null);
		assertEquals(newHome, defaultTranslatedHome);
	}

	@Test
	public void shouldGetResourceInstallations() throws Exception {
		BuildingElementDto home = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();
		ResourceInstallation cisco = ResourceInstallation.builder().comment("Циска в доме").build();
		when(resInstallationRepository.findByInstalledAtIdIn(asList(home.getId()))).thenReturn(singletonList(cisco));

		ResourceInstallationDto translatedCisco = ResourceInstallationDto.builder().build();
		when(resInstallationTranslator.translate(cisco)).thenReturn(translatedCisco);

		List<ResourceInstallationDto> homeInstallations = service.getResourceInstallations(home);
		assertEquals(translatedCisco, homeInstallations.get(0));
	}

	@Test
	public void shouldFindBuildingByResInstallation() {
		BuildingElementDto apartment = BuildingElementDto.builder().id(23L).build(); //Квартира
		ResourceInstallationDto cisco = ResourceInstallationDto.builder().installedAt(apartment).build();

		BuildingElement persistedHome = BuildingElement.builder().build();
		BuildingElement persistedApt = BuildingElement.builder().parent(persistedHome).build();
		persistedHome.setChildren(newArrayList(persistedApt));
		when(repository.findElementById(apartment.getId())).thenReturn(persistedApt);
		when(translator.translate(persistedHome)).thenReturn(defaultTranslatedHome);

		assertEquals(defaultTranslatedHome, service.findBuildingByResInstallation(cisco));
	}

	@Test
	public void shouldFindAllByLocationName() throws Exception {
		String addressString = "спб, звездная, -4";
		List<Location> addresses = singletonList(new Location(1L));
		when(locationAppService.getLocationsLike(eq(addressString), anyInt())).thenReturn(addresses);

		when(repository.findAllByLocation(any())).thenReturn(singletonList(defaultPersistedHome));

		List<BuildingElementDto> buildingElements = service.findAllByLocationName(addressString);
		assertTrue(buildingElements.contains(defaultTranslatedHome));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldVerifyBuildingLocationInputs() throws Exception {
		service.findBuildingElementLocation(null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotFindBuildingLocationDueNoBuildingFound() throws Exception {
		BuildingElementDto stairs = BuildingElementDto.builder().id(23L).build();
		Location res = service.findBuildingElementLocation(stairs);
		assertNotNull(res);
	}

	@Test
	public void shouldFindBuildingLocation() throws Exception {
		BuildingElementDto stairs = BuildingElementDto.builder().id(23L).build();


		BuildingElement parent = BuildingElement.builder().id(2L).location(new Building(1L)).build();
		BuildingElement elm = BuildingElement.builder().id(1L).parent(parent).build();

		when(repository.findElementById(stairs.getId())).thenReturn(elm);

		Location res = service.findBuildingElementLocation(stairs);
		assertNotNull(res);
	}

	@Test
	public void shouldChangeType() {
		doNothing().when(repository).changeType(any(), any());
		when(repository.findAllByElementType(any())).thenReturn(singletonList(defaultPersistedHome));

		service.changeType(8L, 9L);

		verify(repository, atLeastOnce()).changeType(any(), any());
	}

	@Test
	public void shouldNotChangeType() {
		service.changeType(1L, 2L);

		verify(repository, never()).changeType(any(), any());
	}
}