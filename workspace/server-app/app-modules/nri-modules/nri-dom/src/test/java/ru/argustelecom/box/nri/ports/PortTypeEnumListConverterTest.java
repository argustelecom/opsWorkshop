package ru.argustelecom.box.nri.ports;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import ru.argustelecom.box.nri.ports.model.PortType;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class PortTypeEnumListConverterTest {

	private PortTypeEnumListConverter testingClass = new PortTypeEnumListConverter();

	@Test
	public void shouldConvert() {
		Set<PortType> portTypeList = new HashSet<>(2);
		portTypeList.add(PortType.COMBO_PORT);
		portTypeList.add(PortType.ETHERNET_PORT);

		String res = testingClass.convertToDatabaseColumn(portTypeList);

		assertTrue(StringUtils.isNotBlank(res));
		assertTrue(res.contains(PortType.COMBO_PORT.name()));
		assertTrue(res.contains(PortType.ETHERNET_PORT.name()));
		assertTrue(res.contains(","));

		Set<PortType> resList = testingClass.convertToEntityAttribute(res);

		assertTrue(CollectionUtils.isNotEmpty(resList));
		assertTrue(resList.size() == 2);
	}
}