package ru.argustelecom.box.nri.resources.spec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 17.10.2017
 */
@RunWith(PowerMockRunner.class)
public class ResourceSpecificationAppServiceTest {

	@Mock
	private ResourceSpecificationRepository repository;

	@Mock
	private ResourceSpecificationDtoTranslator translator;

	@InjectMocks
	private ResourceSpecificationAppService service;

	private ResourceSpecification defaultSpec = ResourceSpecification.builder().id(1L).build();
	private ResourceSpecificationDto defaultSpecDto = ResourceSpecificationDto.builder().id(1L).build();

	@Before
	public void setUp() throws Exception {
		when(translator.translate(defaultSpec)).thenReturn(defaultSpecDto);
	}

	@Test
	public void shouldFindAllSpecifications() throws Exception {
		when(repository.findAll()).thenReturn(Collections.singletonList(defaultSpec));
		List<ResourceSpecificationDto> allSpecs = service.findAllSpecifications();

		assertNotNull(allSpecs);
		assertFalse(allSpecs.isEmpty());
		assertTrue(allSpecs.contains(defaultSpecDto));
	}

	@Test
	public void shouldFindSpecification() throws Exception {
		when(repository.findOne(anyLong())).thenReturn(defaultSpec);
		ResourceSpecificationDto spec = service.findSpecification(1L);

		assertNotNull(spec);
		assertEquals(defaultSpecDto, spec);
	}
}