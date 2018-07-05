import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;

import ru.argustelecom.system.inf.configuration.ConfiguratorUtils;
import ru.argustelecom.system.inf.configuration.ServerConfigurationFile


class BoxCoreModuleProcessor{
    static void process(box_overrides_processing, box_version, argus_imitation_core_package) {
        File source = new File(box_overrides_processing,     '/modules/ru/argustelecom/box-core/main/module.xml');
        File target = new File(argus_imitation_core_package, '/modules/ru/argustelecom/box-core/main/module.xml');

        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        ConfiguratorUtils.copyFile(source, target);

        ServerConfigurationFile moduleXml = new ServerConfigurationFile(target);
        moduleXml.loadAndParse();

        Element coreResourceRoot = (Element) XPathFactory.newInstance().newXPath().evaluate(
                "/module/resources/resource-root[@path='box-core.jar']",
                moduleXml.document,
                XPathConstants.NODE
        );
        if (coreResourceRoot == null) {
            throw new RuntimeException('Unable to find resource-root element for box-core module');
        }

        coreResourceRoot.setAttribute('path', 'box-core-' + box_version + '.jar');
        moduleXml.save();
    }
}

BoxCoreModuleProcessor.process(
        project.properties['box.overrides.processing'],
        project.properties['box.app.version'],
        project.properties['argus.imitation.core.package']
);