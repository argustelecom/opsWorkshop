package ru.argustelecom.system.distributive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;

/** Выполняет проверку, что процесс сервера приложений не запущен в каталоге INSTALL_PATH
 * 
 * @author v.semchenko (TASK-79981)
 *
 */
public class RunningProcessAutoValidator implements DataValidator {
	private static final Logger log = Logger.getLogger(RunningProcessAutoValidator.class.getName());

	/** Валидатор, проверяющий что в каталоге установки не запущен процесс сервера приложений
	 * 
	 * @param installData
	 * @return состояние валидациии
	 */
	@Override
	public Status validateData(InstallData installData) {
		// Включаем своё логирование.
		ConsoleHandler loghandler = new ConsoleHandler();
		log.addHandler(loghandler);
		loghandler.setLevel(Level.ALL);

		String installPath = installData.getVariable("INSTALL_PATH");

		if (new File(installPath + File.separator + "bin" + File.separator).exists()) {
			if (!SystemUtils.IS_OS_WINDOWS) {
				if (executeShellCommand("ps -ef|grep java|grep " + installPath + "| grep -v grep")
						.contains(installPath)) {

					return Status.ERROR;
				} else {
					return Status.OK;
				}
			} else {
				return Status.OK;
			}
		}
		return Status.OK;
	}

	@Override
	public String getErrorMessageId() {
		return "In installation directory is running the process of application server.\nStop the application server!";
	}
	
	/** Выполнить shell команду для OS *nix  
	 * @param command команда
	 * @return
	 */
	private String executeShellCommand(String command) {
		StringBuffer output = new StringBuffer();
		String[] cmd = { "/bin/sh", "-c", command };
		
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
			} catch(IOException e) {
				throw new IOException("Ошибка чтения результатов команды: " + command, e); 				
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Ошибка выполнения команды: " + command, e);
		}
 		
		return output.toString();
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
