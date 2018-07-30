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

public class InstallPathAutoValidator implements DataValidator {
    private static final Logger log = Logger.getLogger(InstallPathAutoValidator.class.getName());
	
    /**
     * Валидатор, проверяющий наличие " " в поле ввода 'INSTALL_PATH'
     * @param installData
     * @return состояние валидациии данных
     */
	@Override
	public Status validateData(InstallData installData)
	{
		String installPath = installData.getVariable("INSTALL_PATH");
		if (installPath.contains(" ")) {
			return Status.ERROR;
		} else {
			return Status.OK;
		}
	}
	
	@Override
    public String getErrorMessageId() {
        return "Installation path must not contain spaces.";
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
