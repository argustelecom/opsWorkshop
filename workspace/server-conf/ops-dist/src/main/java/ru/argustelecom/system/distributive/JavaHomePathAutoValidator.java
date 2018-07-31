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

public class JavaHomePathAutoValidator implements DataValidator {
    private static final Logger log = Logger.getLogger(JavaHomePathAutoValidator.class.getName());

    @Override
    public Status validateData(InstallData installData) {

        // Включаем своё логирование.
        ConsoleHandler loghandler = new ConsoleHandler();
        log.addHandler(loghandler);
        loghandler.setLevel(Level.ALL);

        // Let's skip UserInputPanel_0 validation in case there is no -options argument.

        log.info("installerMode is: " + Installer.getInstallerMode());
        log.info("consoleMode is: " + Installer.getConsoleMode());
        if (Installer.getConsoleMode() != Installer.CONSOLE_FROM_TEMPLATE) {
            return Status.OK;
        }
        String javaHome = installData.getVariable("argus.java.home.path");
        System.out.println(javaHome);
        if (StringUtils.isBlank(javaHome)) {
            log.warning("argus.java.home.path is not defined, trying JAVA_HOME...");
            javaHome = System.getenv("JAVA_HOME");
            System.out.println(javaHome);
            if (StringUtils.isBlank(javaHome)) {
                log.severe("JAVA_HOME is not defined! Make sure you have set either JAVA_HOME environment variable or 'argus.java.home.path' in properties-file.");
            } else {
                log.info("Setting 'argus.java.home.path' to " + "\"" + javaHome + "\"...");
                installData.setVariable("argus.java.home.path", javaHome);
            }
        }
        log.info("Validating 'argus.java.home.path'...");
        Path javaHomePath = Paths.get(javaHome);

        String ext = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            ext = ".exe";
        }
        Path javaHomePathBin = Paths.get(javaHome + "/bin/java" + ext);
        if (Files.notExists(javaHomePath)) {
            log.severe("Dir \"" + javaHomePath + "\" does not exists!");
            return Status.ERROR;
        } else {
            if (Files.exists(javaHomePathBin)) {
                log.info("\"" + javaHomePathBin + "\" found.");
                return Status.OK;
            } else {
                log.severe("\"" + javaHomePathBin + "\" not found.");
                return Status.ERROR;
            }
        }
    }

    @Override
    public String getErrorMessageId() {
        String message = "Unable to set JAVA_HOME";
        return message;
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