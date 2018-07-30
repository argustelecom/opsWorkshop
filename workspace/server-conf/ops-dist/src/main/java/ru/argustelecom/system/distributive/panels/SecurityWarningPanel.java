package ru.argustelecom.system.distributive.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.installer.gui.IzPanel;

/**
 * Панель отображения предупреждения о том, что будет подготовлен файл лога установки, в котором видны все настройки. (TASK-62089)
 * <br>
 * Панель отображается только в том случае, если argus.app.security-mode.enabled = true<br>
 * Объявлена в src/main/izpack/install.xml<br>
 * Языковые настройки в CustomLangPack.xml<br>
 * Про Custom Panel: https://izpack.atlassian.net/wiki/display/IZPACK/Custom+Panels
 * @author v.astapkovich
 */
public class SecurityWarningPanel extends IzPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	// Ссылка на параметры установки
	private GUIInstallData idata;

	public SecurityWarningPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources,
			Log log) {
		super(panel, parent, installData, new IzPanelLayout(log), resources);
		idata = installData;
		buildGUI();
	}

	/**
	 * Построение окна
	 */
	private void buildGUI() {
		// Добавляем иконку предупреждения. Список иконоко тут:
		// https://izpack.atlassian.net/wiki/display/IZPACK/Custom+Icons
		ImageIcon infoIcon = parent.getIcons().get("information");
		add(LabelFactory.create(getString("SecurityWarningPanel.warning")+":",infoIcon,LEADING), NEXT_LINE );
		add(LabelFactory.create(getString("SecurityWarningPanel.logfilecreation") + ": /.installation/install.log"), NEXT_LINE);
		add(LabelFactory.create(getString("SecurityWarningPanel.propertywarn")), NEXT_LINE );
		add(LabelFactory.create(getString("SecurityWarningPanel.recomendation")), NEXT_LINE );
		add( new JSeparator() );
		getLayoutHelper().completeLayout();
	}

	/**
	 * Отслеживание выполняемых действий
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//Никаких действий не выполняется, только информирование пользователей.
	}

	/**
	 * Определяем, нужно ли показывать данную панель.<br>
	 * Если argus.app.security-mode.enabled = true, тогда показываем.
	 * Если panelActivate не выполнено - панель не добавится.
	 */
	@Override
	public void panelActivate() {
		if ( isInSecuriteMode() ) {
			super.panelActivate();
		} else {
			parent.skipPanel();
		}
	}
	
	/**
	 * Получить из атрибутов установки атрибут argus.app.security-mode.enabled, указывающий на включённый режим повышенной безопасности.
	 * @return 
	 */
	private boolean isInSecuriteMode(){
		String value = (String) idata.getVariables().getProperties().get("argus.app.security-mode.enabled");
		return value.equals("true");
	}

}