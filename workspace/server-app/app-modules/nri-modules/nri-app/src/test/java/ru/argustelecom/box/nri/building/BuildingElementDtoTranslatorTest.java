package ru.argustelecom.box.nri.building;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.nri.building.model.BuildingElement;

import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 25.08.2017.
 */
@RunWith(PowerMockRunner.class)
public class BuildingElementDtoTranslatorTest {

    @Mock
    private BuildingElementTypeDtoTranslator typeDtoTranslator;

    @InjectMocks
    private BuildingElementDtoTranslator translator;

    @Test
    public void shouldTranslateToDto() throws Exception {
        BuildingElement type = new BuildingElementDtoTranslatorTest.BuildingElementTester(1L);
        type.setLocation(new Building(1L));
        type.addChild(new BuildingElementDtoTranslatorTest.BuildingElementTester(2L));

        when(typeDtoTranslator.translate(anyObject())).thenReturn(null);

        BuildingElementDto dto = translator.translate(type);

        Assert.assertNotNull(dto);
        Assert.assertEquals(new Long(1L), dto.getId());
        org.springframework.util.Assert.notEmpty(dto.getChildElements());
    }

    @Test
    public void shouldValidateInput() throws Exception {
        Assert.assertNull(translator.translate(null));
    }

    /**
     * Обёртка для доступа к конструктору в тесте
     */
    private static final class BuildingElementTester extends BuildingElement {
        public BuildingElementTester(Long id) {
            this.id = id;
        }
    }
}