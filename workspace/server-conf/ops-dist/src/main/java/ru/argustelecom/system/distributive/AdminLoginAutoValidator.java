package ru.argustelecom.system.distributive;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;
import com.izforge.izpack.installer.bootstrap.Installer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import ru.argustelecom.system.inf.configuration.Configurator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

public class AdminLoginAutoValidator implements DataValidator {
    private static final Logger log = Logger.getLogger(AdminLoginAutoValidator.class.getName());
    private static final Pattern pattern = Pattern.compile("[a-zA-Z0-9-]+");
    
    /**
     * Валидатор, проверяющий логин админа СП в поле ввода 'argus.app.admin.user' на соответствие 
     * шаблону "[a-zA-Z0-9-]+" (допустимы любые буквенно-цифоровые символы и '-')
     * Работает только в режиме инсталяции через конфигурационный файл (с опцией -options <имя конфигурационного файла>)
     * @param installData
     * @return состояние валидациии
     */    	
	@Override
	public Status validateData(InstallData installData)
	{
		// Включаем своё логирование.
		ConsoleHandler loghandler = new ConsoleHandler();
	    log.addHandler(loghandler);
	    loghandler.setLevel(Level.ALL);
	    
	    // Let's skip UserInputPanel_1 validation in case there is no -options argument.

        log.info("installerMode is: " + Installer.getInstallerMode());
        log.info("consoleMode is: " + Installer.getConsoleMode());
        if (Installer.getConsoleMode() != Installer.CONSOLE_FROM_TEMPLATE) {
            return Status.OK;
        }
	    
	    String adminName = installData.getVariable("argus.app.admin.user");
	    Matcher valid = pattern.matcher(adminName);
	    
		if (valid.matches()) {
			return Status.OK;
		} else {
			return Status.ERROR;
		}
	}
	
	@Override
    public String getErrorMessageId() {
        return "Wrong format for the administrator account.";
    }

    @Override
    public String getWarningMessageId() {
        return null;
    }

    @Override
    public boolean getDefaultAnswer() {
        return false;
    }
}
