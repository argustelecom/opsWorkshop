package ru.argustelecom.box.nri.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationAppService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ResourcesViewModelTest {

	@InjectMocks
	private ResourcesViewModel resourcesViewModel;

	/**
	 * Состояние страницы
	 */
	@Mock
	private ResourcesViewState viewState;

	/**
	 * Сервис спецификаций ресурсов
	 */
	@Mock
	private ResourceSpecificationAppService resSpecService;

	/**
	 * Сервис адресов
	 */
	@Mock
	private LocationAppService locationService;

	/**
	 * Сервис ресурсов
	 */
	@Mock
	private ResourceInstanceAppService resService;


	@Mock
	private List<ParamDescriptorDto> paramDescriptors;

	@Mock
	private ResourceInstanceList lazyResources;

	@Mock
	private UnitOfWork unitOfWork;



	@Test
	public void postConstruct() {
		List<ResourceSpecificationDto> specList;
		specList = new ArrayList<>();
		specList.add(ResourceSpecificationDto.builder().id(1L).isIndependent(true).parameters(
				Collections.singletonList(ParameterSpecificationDto.builder().id(1L).build())).build());
		specList.add(ResourceSpecificationDto.builder().id(2L).isIndependent(true).parameters(
				Collections.singletonList(ParameterSpecificationDto.builder().id(2L).build())).build());
		specList.add(ResourceSpecificationDto.builder().id(3L).isIndependent(true).parameters(
				Collections.singletonList(ParameterSpecificationDto.builder().id(3L).build())).build());
		specList.add(ResourceSpecificationDto.builder().id(4L).isIndependent(false).parameters(
				Collections.singletonList(ParameterSpecificationDto.builder().id(4L).build())).build());
		when(resSpecService.findAllSpecifications()).thenReturn(specList);
		Mockito.doNothing().when(unitOfWork).makePermaLong();
		resourcesViewModel.postConstruct();
		assertNotNull(resourcesViewModel.getAvailableParams());
		assertNotNull(resourcesViewModel.getAvailableSpecifications());
		assertNotNull(resourcesViewModel.getRootSpecifications());
		assertNull(resourcesViewModel.getRootSpecifications().stream().filter((spec) -> !spec.getIsIndependent()).findFirst().orElse(null));
		assertEquals(specList, resourcesViewModel.getAvailableSpecifications());
	}

	@Test
	public void completeLocationReturnEmpty() {
		List<String> result = resourcesViewModel.completeLocation("");
		assertTrue(result.isEmpty());
	}

	@Test
	public void completeLocationReturnEmptyIfLess3() {
		List<String> result = resourcesViewModel.completeLocation("1");
		assertTrue(result.isEmpty());
	}

	@Test
	public void completeLocation() {
		List<Location> locations = new ArrayList<>();
		Building loc1 = new Building(1L);
		loc1.setName("name1");
		Building loc3 = new Building(3L);
		loc3.setName("name3");
		Lodging loc3child = new Lodging(4L);
		loc3child.setName("loc3child");
		loc3child.setParent(loc3);
		loc3.addChild(loc3child);

		locations.add(loc3);
		locations.add(loc1);
		when(locationService.getLocationsLike(eq("name"), eq(5))).thenReturn(locations);
		List<String> result = resourcesViewModel.completeLocation("name");
		assertEquals(3, result.size());

		assertTrue("name1, ".equals(result.get(0)));
		assertTrue("name3, ".equals(result.get(1)));
		assertTrue("name3, loc3child, ".equals(result.get(2)));
	}

	@Test
	public void updateLocator() {
		UIComponent component = mock(UIComponent.class);
		String oldValue = "prevname";
		String newValue = "newname";
		ValueChangeEvent s = new ValueChangeEvent(component, oldValue, newValue);
		when(viewState.getLocationString()).thenReturn("");
		resourcesViewModel.updateLocator(s);
		verify(viewState, times(1)).setLocationString(eq(newValue));
	}

	@Test
	public void toggleAdditionalFiltersTrue() {
		resourcesViewModel.setUseAdditionalFilters(true);
		when(viewState.getParamDescriptors()).thenReturn(paramDescriptors);
		resourcesViewModel.toggleAdditionalFilters();
		verify(paramDescriptors, times(1)).clear();
		assertFalse(resourcesViewModel.isUseAdditionalFilters());
	}

	@Test
	public void toggleAdditionalFiltersFalse() {
		resourcesViewModel.setUseAdditionalFilters(false);
		when(viewState.getParamDescriptors()).thenReturn(paramDescriptors);
		resourcesViewModel.toggleAdditionalFilters();
		verify(paramDescriptors, times(1)).add(any());
		assertTrue(resourcesViewModel.isUseAdditionalFilters());
	}

	@Test
	public void deleteSelectedResource() {
		resourcesViewModel.setSelectedResource(ResourceInstanceListDto.builder().id(1L).build());
		resourcesViewModel.deleteSelectedResource();
		verify(resService, times(1)).removeResource(new Long(1L));
		verify(lazyResources, times(1)).reloadData();
	}

	@Test
	public void addParamFilter() {
		when(viewState.getParamDescriptors()).thenReturn(paramDescriptors);
		resourcesViewModel.addParamFilter();
		verify(paramDescriptors, times(1)).add(any());
	}

	@Test
	public void getStatuses() {
		assertEquals(asList(ResourceStatus.values()), resourcesViewModel.getStatuses());
	}

	@Test
	public void deleteParamOutOfIndex() {
		when(viewState.getParamDescriptors()).thenReturn(paramDescriptors);
		when(paramDescriptors.size()).thenReturn(9);
		resourcesViewModel.deleteParam(10);
		verify(paramDescriptors, times(0)).remove(any());
	}

	@Test
	public void deleteParam() {
		when(viewState.getParamDescriptors()).thenReturn(paramDescriptors);
		when(paramDescriptors.size()).thenReturn(9);
		resourcesViewModel.deleteParam(8);
		verify(paramDescriptors, times(1)).remove(8);
	}

	@Test
	public void cleanCreationParams() {
		resourcesViewModel.setNewElemSpecificationId(new Long(1L));
		resourcesViewModel.cleanCreationParams();
		assertNull(resourcesViewModel.getNewElemSpecificationId());
	}
}