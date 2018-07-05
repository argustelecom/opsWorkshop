package ru.argustelecom.box.nri.integration.viewmodel;

import org.junit.Test;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;

import static org.junit.Assert.assertTrue;

/**
 * Created by s.kolyada on 06.02.2018.
 */
public class LogicalResourceDataHolderTest {

	@Test
	public void shouldBuildUrlForIP() throws Exception {
		LogicalResourceDataHolder holder = LogicalResourceDataHolder.builder()
				.id(1111L)
				.type(LogicalResourceType.IP_ADDRESS)
				.build();

		String url = holder.buildResourceUrl();

		assertTrue(url.contains("1111"));
		assertTrue(url.contains("IPAddress"));
	}

	@Test
	public void shouldBuildUrlForPN() throws Exception {
		LogicalResourceDataHolder holder = LogicalResourceDataHolder.builder()
				.id(1111L)
				.type(LogicalResourceType.PHONE_NUMBER)
				.build();

		String url = holder.buildResourceUrl();

		assertTrue(url.contains("1111"));
		assertTrue(url.contains("PhoneNumber"));
	}

	@Test
	public void shouldConvertFromDto() throws Exception {
		LogicalResourceDto dto = LogicalResourceDto.builder()
				.id(1L)
				.resourceName("name")
				.type(LogicalResourceType.IP_ADDRESS)
				.build();

		LogicalResourceDataHolder holder = LogicalResourceDataHolder.convert(dto);

		assertTrue(holder.getId().equals(dto.getId()));
		assertTrue(holder.getObjectName().equals(dto.getObjectName()));
		assertTrue(holder.getType().equals(dto.getType()));
	}
}