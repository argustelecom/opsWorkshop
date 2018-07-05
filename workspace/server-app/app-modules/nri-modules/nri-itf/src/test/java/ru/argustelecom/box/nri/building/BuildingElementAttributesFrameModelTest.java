package ru.argustelecom.box.nri.building;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.Lodging;


import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
public class BuildingElementAttributesFrameModelTest {



	@Mock
	private BuildingElementAppService service;
	@Mock
	private BuildingElementTypeAppService typeService;
	@Mock
	private LocationRepository locationRepository;

	@InjectMocks
	BuildingElementAttributesFrameModel model;

	@Before
	public void setUp() throws Exception {

		when(typeService.findAllElementTypes()).thenReturn(Lists.newArrayList(
				BuildingElementTypeDto.builder().id(1L).name("type1").level(new LocationLevel(LocationLevel.LODGING)).build(),
				BuildingElementTypeDto.builder().id(2L).name("type2").level(new LocationLevel(LocationLevel.LODGING)).build(),
				BuildingElementTypeDto.builder().id(3L).name("type3").level(new LocationLevel(LocationLevel.BUILDING)).build(),
				BuildingElementTypeDto.builder().id(4L).name("type4").level(new LocationLevel(LocationLevel.BUILDING)).build(),
				BuildingElementTypeDto.builder().id(5L).name("type5").level(new LocationLevel(LocationLevel.STREET)).build()));

	}

	@Test
	public void shouldInitialize(){
		model.initialize();
		assertNotNull(model.getPossibleTypes());
		assertEquals(5,model.getPossibleTypes().size());
	}

	@Test
	public void shouldPreRender() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").level(new LocationLevel(LocationLevel.LODGING)).build()).build();
		BuildingElementDto rootElement = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").level(new LocationLevel(LocationLevel.LODGING)).build()).build();

		when(locationRepository.findAllLodgingsByBuilding(any())).thenReturn(Lists.newArrayList(new Lodging(2L)));
		model.preRender(dto,rootElement);
		assertNotNull(model.getPossibleLodgings());
		assertEquals(1,model.getPossibleLodgings().size());
		model.preRender(rootElement,rootElement);
		assertNotNull(model.getPossibleLodgings());
		assertEquals(0,model.getPossibleLodgings().size());
		model.preRender(dto,rootElement);
		model.preRender(BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").build()).build(),rootElement);
		assertNotNull(model.getPossibleLodgings());
		assertEquals(0,model.getPossibleLodgings().size());
	}

	@Test
	public void isAddressVisibleFalse() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").build()).build();
		model.setCurrentElement(dto);
		assertEquals(dto,model.getCurrentElement());
		assertFalse(model.isAddressVisible());
	}
	@Test
	public void isAddressVisibleTrue() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").level(new LocationLevel(LocationLevel.LODGING)).build()).build();
		model.setCurrentElement(dto);
		assertTrue(model.isAddressVisible());
	}

	@Test
	public void shouldChangeNameAndTypeNullLevel() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").build()).build();
		model.setCurrentElement(dto);

		when(service.changeNameAndType(any(),anyString(),any())).thenReturn(dto);
		model.changeNameAndType();
		verify(service,times(1)).changeLocation(any(),any());

	}
	@Test
	public void shouldChangeNameAndType() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).name("name").type(BuildingElementTypeDto.builder().id(1L).name("type1").level(new LocationLevel(LocationLevel.LODGING)).build()).build();
		model.setCurrentElement(dto);

		when(service.changeNameAndType(any(),anyString(),any())).thenReturn(dto);
		model.changeNameAndType();
		verify(service,times(0)).changeLocation(any(),any());

	}

	@Test
	public void shouldReturnFreeLodgings() throws Exception {
		when(service.getFreeLodgings(anyList(),any())).thenReturn(Collections.emptyList());
		List<Lodging> re = model.getFreeLodgings();
		assertNotNull(re);
	}

	@Test
	public void shouldChangeLocation() throws Exception {
		model.setCurrentElement(BuildingElementDto.builder().id(1L).build());

		when(service.changeLocation(eq(1L), any())).thenReturn(model.getCurrentElement());

		model.changeLocation();

		verify(service, times(1)).changeLocation(eq(1L),any());
	}
}