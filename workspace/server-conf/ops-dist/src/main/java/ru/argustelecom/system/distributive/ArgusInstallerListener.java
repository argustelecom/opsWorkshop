package ru.argustelecom.system.distributive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.bootstrap.Installer;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import ru.argustelecom.system.inf.configuration.Configurator;
import ru.argustelecom.system.inf.configuration.ConfiguratorProperties;
import ru.argustelecom.system.inf.configuration.WorkspaceProperties;

public class ArgusInstallerListener extends SimpleInstallerListener {
    private static final Logger log = Logger.getLogger(ArgusInstallerListener.class.getName());
    /** Полный набор параметров установки. Доступен после выполнения afterPacks */
    private static WorkspaceProperties fromWorkspaceProperties = null;
    
    public ArgusInstallerListener(Resources resources) {
        super(resources);
    }
    // TASK-65589 перед распаковкой чистим старые деплойменты.
    @Override
	public void beforePacks(AutomatedInstallData idata, Integer npacks, AbstractUIProgressHandler handler)
			throws Exception {
		ConfiguratorProperties props = new ConfiguratorProperties(idata.getVariables().getProperties());
		Configurator.uninstallArgus(props);
	}
    
    @Override
    public void afterPacks(AutomatedInstallData idata, AbstractUIProgressHandler handler) throws Exception {


        // Включаем своё логирование.
        ConsoleHandler loghandler = new ConsoleHandler();
        log.addHandler(loghandler);
        loghandler.setLevel(Level.ALL);
        FileHandler filehandler = createFileHandler(idata);
    	log.addHandler(filehandler);
    	
        /**
         * 1. сравниваем полученные после ввода properties (там буду все, включая переменные izpack) с work(my)
         * 2. отличающиеся свойства аппендим в my.properties или заменяем значения, если они уже есть.
         * 3. после чего начинаем подготовку к конфигурации сервака.
         **/

        // В idata хранятся параметры установки на момент вызова Listener'а.
        Properties installatorProperties = idata.getVariables().getProperties();
        String installPath = installatorProperties.getProperty("INSTALL_PATH");
        String myPropsFullPath = installPath + "/.config/" + "my.properties";

        PropertiesConfiguration finalProperties = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = finalProperties.getLayout();
        layout.setGlobalSeparator("=");

        log.info("----Далее идут логи конструктора (создания объекта пропертей). Ценной информации в них нет-----");
        // Здесь берем свойства из файлов .properties, поставляемых с инсталлятором.
        fromWorkspaceProperties = new WorkspaceProperties(installPath + "/.config/");
        log.info("---- конец бесполезной информации ----");
        fromWorkspaceProperties.getProperties().clear();
        fromWorkspaceProperties.getProperties().putAll(fromWorkspaceProperties.readPropertiesFromFile(installPath + "/.config/", "work.properties", true, WorkspaceProperties.ONE_LEVEL_OF_SEARCH));
      
        Properties workspaceProperties = fromWorkspaceProperties.getProperties();
        log.info("Loaded properties:" + fromWorkspaceProperties);

        log.info("initial installatorProperties: " + installatorProperties);
        log.info("initial workspaceProperties: " + workspaceProperties);

        // Если запущены с параметром "-options some.properties", используем все свойства из предложенного файла,
        // включая отсутствующие в установщике.
        Properties propsOptions = new Properties();
        if (Installer.getConsoleMode() == Installer.CONSOLE_FROM_TEMPLATE) {
            File propsOptionsFile = new File(Installer.getPropsPath());
            FileInputStream fileInput = new FileInputStream(propsOptionsFile);
            propsOptions.load(fileInput);
            fileInput.close();
        }

		// TASK-87527, v.semchenko: нет необходимости портить настройки work.properties настройками из idata, если
		// установка производиться с параметром "-options some.properties"
		if (Installer.getConsoleMode() != Installer.CONSOLE_FROM_TEMPLATE) {
			// Ищем разницу в свойствах. Если параметр во время установки был изменен, сообщаем.
			for (Map.Entry<Object, Object> e1 : installatorProperties.entrySet()) {
				String key = (String) e1.getKey();
				String value = (String) e1.getValue();

				if (workspaceProperties.containsKey(key)) {
					String value2 = (String) workspaceProperties.get(key);
					if (!value2.equals(value)) {
						workspaceProperties.setProperty(key, value);
						finalProperties.setProperty(key, value);
						System.out.println("updated key/value is: " + key + "=" + finalProperties.getProperty(key));
					}
				}
			}
		}

        // Если задан файл-свойств через -options, добавляем эти значения
        if (propsOptions != null) {
            for (Map.Entry<Object, Object> e1 : propsOptions.entrySet()) {
                String key = (String) e1.getKey();
                String value = (String) e1.getValue();

                if (workspaceProperties.containsKey(key)) {
                    String value2 = (String) workspaceProperties.get(key);
                    if (!value2.equals(value)) {
                        workspaceProperties.setProperty(key, value);
                        finalProperties.setProperty(key, value);
                        System.out.println("updated key/value is: " + key + "=" + finalProperties.getProperty(key));
                    }
                }
                else {
                    finalProperties.setProperty(key, value);
                    System.out.println("new key/value is: " + key + "=" + finalProperties.getProperty(key));
                }
            }
        }
        finalProperties.save(myPropsFullPath);
        
        // Передаем в groovy-скрипт каталог установки и отключение uninstall, запускаем конфигурирование:
        Binding binding = new Binding();
        binding.setVariable("installPath", installPath);
        // Передаем skipUninstall=true потому что в ином случае, после распаковки происходит удаление деплойментов 
        binding.setVariable("skipUninstall", true);
        binding.setVariable("doChmod", true);
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(new File(installPath + "/.config/configure-server.groovy"));
    }
    
	/**
	 * Создаём FileHandler для внутреннего логирования.<br>
	 * Логгер ловит все, что отправлено через log.метод и пишет в файл.
	 * 
	 * @param idata Данные установщика
	 * @return Настроенный FileHandler
	 */
	public FileHandler createFileHandler(AutomatedInstallData idata) {
		File folder = new File(idata.getVariables().getProperties().getProperty("INSTALL_PATH") + "/.installation");
		if (!folder.exists())
			folder.mkdir();
		FileHandler filehandler = null;
		try {
			filehandler = new FileHandler(folder.getAbsolutePath() + "/install.log");
			filehandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return filehandler;
	}
	
	/**
	 * Запросить у слушателя полный набор параметров
	 * @return WorkspaceProperties все доступные параметры установки сервера приложений
	 */
	public static WorkspaceProperties getWorkspaceProperties() {
		return fromWorkspaceProperties;
	}
	
}