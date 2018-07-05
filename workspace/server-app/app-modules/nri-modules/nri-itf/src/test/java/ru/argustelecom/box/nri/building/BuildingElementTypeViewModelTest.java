package ru.argustelecom.box.nri.building;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 28.08.2017
 */
@RunWith(PowerMockRunner.class)
public class BuildingElementTypeViewModelTest {

    @Mock
    private BuildingElementTypeAppService appService;

    @Mock
    private BuildingElementAppService buildingService;

    @Mock
    private UnitOfWork unitOfWork;

    @InjectMocks
    private BuildingElementTypeViewModel viewModel;

    @Test
    public void shouldReturnAllTypes() throws Exception {
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(Collections.singletonList(testElementType));

        List<BuildingElementTypeDto> types = viewModel.getTypes();

        assertNotNull(types);
        Assert.assertFalse(types.isEmpty());
        assertEquals(viewModel.getAllTypes(),types);
    }

    @Test
    public void shouldCreateNewType() throws Exception {
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        BuildingElementTypeDto newElementType = BuildingElementTypeDto.builder().id(2L).name("name 2").build();

        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));
        when(appService.createElementType(anyString(), anyObject(), any())).thenReturn(newElementType);

        viewModel.getTypes();
        viewModel.create();

        assertTrue(viewModel.getTypes().size() == 2);
        assertTrue(viewModel.getTypes().contains(newElementType));
    }

    @Test
    public void shouldUpdateRow() throws Exception {
        BuildingElementTypeDto elementTypeDto = BuildingElementTypeDto.builder().id(1L).name("name").build();
        RowEditEvent rowEditEvent = new RowEditEvent(new DataTable(), new AjaxBehavior(), elementTypeDto);

        when(appService.updateElementType(anyLong(), anyString(), any())).thenReturn(null);

        viewModel.onRowEdit(rowEditEvent);

        verify(appService, Mockito.times(1)).updateElementType(anyLong(), anyString(), any());
    }

    @Test
    public void shouldRemoveSelectedTypes() throws Exception {
        doNothing().when(appService).removeElementType(anyLong());
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));

        viewModel.setSelectedType(testElementType);
        assertEquals(1, viewModel.getTypes().size());
        viewModel.removeSelectedType();
        assertTrue(viewModel.getTypes().isEmpty());
        assertNull(viewModel.getSelectedType());
    }

    @Test
    public void shouldValidateNull(){
        FacesContext context = ContextMocker.mockFacesContext();
        viewModel.nameValidator(context,null,null);
        assertEquals(0, FacesContext.getCurrentInstance().getMessageList().size());
    }

    @Test(expected = ValidatorException.class)
    public void shouldNotValidate(){
        FacesContext context = ContextMocker.mockFacesContext();
        viewModel.nameValidator(context,null,"");
    }

    @Test
    public void shouldValidate(){
        FacesContext context = ContextMocker.mockFacesContext();
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));
        viewModel.getTypes();
        viewModel.nameValidator(context,null,"New name");
        assertEquals(0, FacesContext.getCurrentInstance().getMessageList().size());
    }

    @Test(expected = ValidatorException.class)
    public void shouldNotValidateEqName(){
        FacesContext context = ContextMocker.mockFacesContext();
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));
        viewModel.getTypes();
        viewModel.setSelectedType(null);
        viewModel.nameValidator(context,null,"name");
    }

    @Test
    public void shouldMoveBuildingElements() {
        doNothing().when(buildingService).changeType(any(), any());
        doNothing().when(appService).remove(any());
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));

        viewModel.setMoveFrom(viewModel.getTypes().get(0));
        viewModel.setMoveTo(BuildingElementTypeDto.builder().build());
        viewModel.moveBuildingElements();

        verify(buildingService, atLeastOnce()).changeType(any(), any());
        verify(appService, atLeastOnce()).remove(any());
    }

    @Test
    public void shouldPostConstruct() {
        doNothing().when(unitOfWork).makePermaLong();
        viewModel.postConstruct();
    }

    @Test
    public void shouldShowErrorWhileRemoving() throws Exception {
        doThrow(BusinessExceptionWithoutRollback.class).when(appService).removeElementType(any());
        when(appService.findAllByLevel(any())).thenReturn(new ArrayList<>());
        ContextMocker.mockFacesContext();
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));

        viewModel.setSelectedType(testElementType);
        viewModel.removeSelectedType();
    }

    @Test
    public void shouldShowDialogWhileRemoving() throws Exception {
        doThrow(BusinessExceptionWithoutRollback.class).when(appService).removeElementType(any());
        ArrayList<BuildingElementTypeDto> sameLevelTypes = new ArrayList<>();
        sameLevelTypes.add(BuildingElementTypeDto.builder().build());
        when(appService.findAllByLevel(any())).thenReturn(sameLevelTypes);
        ContextMocker.mockFacesContext();
        BuildingElementTypeDto testElementType = BuildingElementTypeDto.builder().id(1L).name("name").build();
        when(appService.findAllElementTypes()).thenReturn(newArrayList(testElementType));

        viewModel.setSelectedType(testElementType);
        viewModel.removeSelectedType();

        assertNotNull(viewModel.getMoveFrom());
    }
}