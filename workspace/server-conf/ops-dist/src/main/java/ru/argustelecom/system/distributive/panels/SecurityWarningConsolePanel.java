package ru.argustelecom.system.distributive.panels;

import java.util.Properties;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.resource.Messages;
import com.izforge.izpack.installer.console.AbstractConsolePanel;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.util.Console;

import ru.argustelecom.system.distributive.ArgusInstallerListener;
import ru.argustelecom.system.inf.configuration.WorkspaceProperties;

/**
 * Консольная версия панели SecurityWarningPanel.<br>
 * Пример можно посмотреть тут: https://github.com/codehaus/izpack/blob/master/izpack-panel/src/main/java/com/izforge/izpack/panels/target/TargetConsolePanel.java<br>
 * @author v.astapkovich
 */
public class SecurityWarningConsolePanel extends AbstractConsolePanel{
	
	public SecurityWarningConsolePanel(PanelView<ConsolePanel> panel) {
		super(panel);
	}

	/**
	 * Будет использован именно этот метод, поэтому определяем его.<br>
	 * Более того, консоль из сосденего метода не получить, поэтому всё пишем в sysout<br>
	 * Важно(!): При запуске через java -DDEBUG=true -jar [jarfile] -options [properties file] на экран не выводится данный sysout. 
	 */
	@Override
	public boolean run(InstallData arg0, Properties arg1) {
		WorkspaceProperties configuration = ArgusInstallerListener.getWorkspaceProperties();
		if ( !configuration.getProperties().get("argus.app.security-mode.enabled").equals("true") ) return true; 
		//Выводить в консоль только в режиме повышенной безопасности
		Messages msg = arg0.getMessages(); //Доступ к CustomLangPack
		System.out.println( msg.get("SecurityWarningPanel.warning"));
		System.out.println( msg.get("SecurityWarningPanel.logfilecreation") + " " + configuration.getProperties().get("INSTALL_PATH")+".installation/install.log" );
		System.out.println( msg.get("SecurityWarningPanel.propertywarn"));
		System.out.println( msg.get("SecurityWarningPanel.recomendation"));
		return true;
	}

	@Override
	public boolean run(InstallData arg0, Console arg1) {
		return true;
	}
	
}