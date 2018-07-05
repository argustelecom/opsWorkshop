package ru.argustelecom.box.nri.building;

import org.junit.Test;
import ru.argustelecom.box.nri.building.model.BuildingElementType;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 24.08.2017.
 */
public class BuildingElementTypeDtoTranslatorTest {

    private BuildingElementTypeDtoTranslator translator = new BuildingElementTypeDtoTranslator();

    @Test
    public void shouldTranslateToDto() throws Exception {
        BuildingElementType type = new BuildingElementType(1L, null, null,
				BuildingElementTypeIcon.FA_BASEMENT);
        type.setName("name");

        BuildingElementTypeDto dto = translator.translate(type);

        assertNotNull(dto);
        assertEquals(new Long(1L), dto.getId());
        assertEquals("name", dto.getName());
    }

    @Test
    public void shouldValidateInput() throws Exception {
        assertNull(translator.translate(null));
    }
}