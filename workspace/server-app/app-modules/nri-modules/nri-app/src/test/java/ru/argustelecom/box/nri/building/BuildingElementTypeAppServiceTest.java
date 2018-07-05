package ru.argustelecom.box.nri.building;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by s.kolyada on 24.08.2017.
 */
@RunWith(PowerMockRunner.class)
public class BuildingElementTypeAppServiceTest {

    @Mock
    private BuildingElementTypeDtoTranslator translator;

    @Mock
    private BuildingElementTypeRepository repository;

    @Mock
    private BuildingElementRepository buildingRepository;

    @InjectMocks
    private BuildingElementTypeAppService service;

    @Test
    public void shouldFindAllTypes() throws Exception {
        List<BuildingElementType> allTypes = new ArrayList<>();
        allTypes.add(new BuildingElementType(1L, null, null, null));
        when(translator.translate(any(BuildingElementType.class))).then(invocation ->  {
            BuildingElementType elementType = invocation.getArgumentAt(0, BuildingElementType.class);
            return new BuildingElementTypeDtoTranslator().translate(elementType);
        });

        when(repository.findAll()).thenReturn(allTypes);

        List<BuildingElementTypeDto> result = service.findAllElementTypes();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void shouldFindElementType() throws Exception {
        when(translator.translate(any(BuildingElementType.class))).then(invocation ->  {
            BuildingElementType elementType = invocation.getArgumentAt(0, BuildingElementType.class);
            return new BuildingElementTypeDtoTranslator().translate(elementType);
        });
        when(repository.findOne(anyLong())).thenReturn(new BuildingElementType(1L, null, null, null));

        BuildingElementTypeDto elementType = service.findElementType(1L);
        assertNotNull(elementType);
        assertEquals(new Long(1L), elementType.getId());
        verify(repository, times(1)).findOne(anyLong());
    }

    @Test
    public void shouldUpdateElementType() throws Exception {
        when(translator.translate(any(BuildingElementType.class))).then(invocation ->  {
            BuildingElementType elementType = invocation.getArgumentAt(0, BuildingElementType.class);
            return new BuildingElementTypeDtoTranslator().translate(elementType);
        });
        BuildingElementType buildingElementType = new BuildingElementType(1L, "name", null, null);

        when(repository.update(anyLong(), anyString(), any())).thenReturn(buildingElementType);

        BuildingElementTypeDto newBuildingElementType = service.updateElementType(buildingElementType.getId(), buildingElementType.getName(), buildingElementType.getIcon());
        assertEquals(buildingElementType.getId(), newBuildingElementType.getId());
        verify(repository, times(1)).update(anyLong(), anyString(), any());
    }

    @Test
    public void shouldCreateElementType() throws Exception {
        when(translator.translate(any(BuildingElementType.class))).then(invocation ->  {
            BuildingElementType elementType = invocation.getArgumentAt(0, BuildingElementType.class);
            return new BuildingElementTypeDtoTranslator().translate(elementType);
        });
        when(repository.create(anyString(), anyObject(), any())).then(invocation -> {
            String name = invocation.getArgumentAt(0, String.class);
            return new BuildingElementType(1L, name, null, null);
        });

        BuildingElementTypeDto elementType = service.createElementType("name", null, null);
        assertNotNull(elementType);
        assertEquals("name", elementType.getName());

        verify(repository, times(1)).create(anyString(), anyObject(), any());
    }

    @Test
    public void shouldRemoveElementType() throws Exception {
        doNothing().when(repository).remove(anyLong());
        when(repository.findOne(anyLong())).thenReturn(BuildingElementType.builder().id(1L).build());
        when(buildingRepository.findAllByElementType(any())).thenReturn(Collections.emptyList());

        service.removeElementType(1L);
        verify(repository, times(1)).remove(anyLong());
    }

    @Test(expected = BusinessExceptionWithoutRollback.class)
    public void shouldThrowExceptionWhileRemoving() throws Exception {
        when(repository.findOne(anyLong())).thenReturn(BuildingElementType.builder().id(1L).build());
        when(buildingRepository.findAllByElementType(any())).thenReturn(Collections.singletonList(BuildingElement.builder().id(1L).build()));

        service.removeElementType(1L);
    }

    @Test
    public void shouldFindAllByLevel() {
        when(repository.findAllByLevel(any())).thenReturn(Collections.singletonList(BuildingElementType.builder().build()));
        List<BuildingElementTypeDto> allByLevel = service.findAllByLevel(new LocationLevel(1L));

        assertNotNull(allByLevel);
        assertFalse(allByLevel.isEmpty());
    }

    @Test
    public void shouldReturnNewArrayListBecauseLevelIsNull() {
        LocationLevel level = null;

        List<BuildingElementTypeDto> allByLevel = service.findAllByLevel(level);

        assertNotNull(allByLevel);
        assertTrue(allByLevel.isEmpty());
    }
}