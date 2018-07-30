package ru.argustelecom.system.distributive;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;
import ru.argustelecom.system.inf.configuration.Configurator;
import java.util.regex.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminLoginValidator implements Validator {
    private static final Logger log = Logger.getLogger(AdminLoginAutoValidator.class.getName());
    private static final Pattern pattern = Pattern.compile("[a-zA-Z0-9-]+");
    
    /**
     * Валидатор, проверяющий логин админа СП в поле ввода 'argus.app.admin.user' на соответствие 
     * шаблону "[a-zA-Z0-9-]+" (допустимы любые буквенно-цифоровые символы и '-')
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
        String adminName = processingClient.getFieldContents(0);
        
        Matcher valid = pattern.matcher(adminName);
        
        if (valid.matches()) {
        	log.info("Validation OK");
            return true;         	
        } else {
        	log.severe("Validation failed!");
            return false;       	
        }

    }
}