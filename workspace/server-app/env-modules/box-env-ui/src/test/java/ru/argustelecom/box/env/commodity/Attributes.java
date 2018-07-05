package ru.argustelecom.box.env.commodity;

import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

class Attributes {

    @FindByFuzzyId("commodity_type_attribute_form-name_out")
    public OutputText name;

    @FindByFuzzyId("commodity_type_attribute_form-keyword_out")
    public OutputText keyword;
}