package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredItemRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by b.bazarov on 12.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequiredItemAppServiceTest {

	@Mock
	private RequiredItemRepository requiredItemRepository;
	@Mock
	private ResourceSchemaRepository resourceSchemaRepository;
	@Mock
	private ResourceSpecificationRepository resourceSpecificationRepository;

	@Mock
	private RequiredItemDtoTranslator requiredItemDtoTranslator;

	@InjectMocks
	private RequiredItemAppService service;

	@Test
	public void shouldCreateInRequiredItem(){
		ResourceSpecification spec = ResourceSpecification.builder().build();
		when(resourceSpecificationRepository.findOne(eq(1L))).thenReturn(spec);
		RequiredItem ri = RequiredItem.builder().build();
		when(requiredItemRepository.findById(1L)).thenReturn(ri);
		service.create(1L, RequiredItemDto.builder().id(1L).build());
		verify(requiredItemRepository,times(1)).create(eq(spec),eq(ri));
	}
	@Test
	public void shouldCreateInSchema(){
		ResourceSpecification spec = ResourceSpecification.builder().build();
		when(resourceSpecificationRepository.findOne(eq(1L))).thenReturn(spec);
		ResourceSchema rs = ResourceSchema.builder().build();
		when(resourceSchemaRepository.findById(1L)).thenReturn(rs);
		service.create(1L, ResourceSchemaDto.builder().id(1L).build());
		verify(requiredItemRepository,times(1)).create(eq(spec),eq(rs));
	}
	@Test
	public void shouldRemoveItemWithRemovingFromSchema() {
		RequiredItem defaultRequiredItem = RequiredItem.builder().id(1L).build();
		ResourceSchema defaultResourceSchema = ResourceSchema.builder().id(1L).build();
		defaultResourceSchema.addRequirement(defaultRequiredItem);
		defaultRequiredItem.setTpSchema(defaultResourceSchema);
		when(requiredItemRepository.findById(anyLong())).thenReturn(defaultRequiredItem);
		when(resourceSchemaRepository.findById(anyLong())).thenReturn(defaultResourceSchema);
		service.removeItem(1L);
		verify(requiredItemRepository, times(1)).delete(any());
	}

	@Test
	public void shouldRemoveItem() throws Exception {
		RequiredItem defaultRequiredItem = RequiredItem.builder().id(1L).build();
		when(requiredItemRepository.findById(anyLong())).thenReturn(defaultRequiredItem);
		service.removeItem(1L);
		verify(requiredItemRepository, times(1)).delete(any());
	}
}