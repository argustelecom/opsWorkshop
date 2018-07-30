package ru.argustelecom.system.distributive;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;
import ru.argustelecom.system.inf.configuration.Configurator;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaHomePathValidator implements Validator {
    private static final Logger log = Logger.getLogger(JavaHomePathAutoValidator.class.getName());
    /**
     * Валидатор, проверяющий наличие /bin/java в поле ввода 'argus.java.home.path'
     * Работает только в интерактивном режиме инсталлятора, в том числе с опцией -console
     * @param processingClient
     * @return состояние валидациии
     */
    public boolean validate(ProcessingClient processingClient) {

        // Включаем своё логирование.
        ConsoleHandler loghandler = new ConsoleHandler();
        log.addHandler(loghandler);
        loghandler.setLevel(Level.ALL);

        log.info(processingClient.getFieldContents(0));
        String javapath = processingClient.getFieldContents(0);
        String javaVendor = String.valueOf(Configurator.lookupVendorAndVersionJVM(javapath));
        log.info("Detecting Java: " + String.valueOf(Configurator.lookupVendorAndVersionJVM(javapath)));

        if (javaVendor.contains("ORACLE") && Configurator.isValidVersionJVM(Configurator.lookupVendorAndVersionJVM(javapath))) {
            log.info("Validation OK");
            return true;
        } else {
            log.severe("Validation failed!");
            return false;
        }

    }
}