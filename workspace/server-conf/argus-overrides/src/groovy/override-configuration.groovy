import ru.argustelecom.system.inf.configuration.Configurator;

/* ����������� ������������ ���������� � ���������� ��� standalone */
Configurator.overwriteSubsystemsAndExtensions(
	project.properties['argus.imitation.core.package'] + "/standalone/configuration",
	"standalone.xml",
	project.properties['ops.overrides.processing']  + "/configuration/subsystem-overrides.xml"
);

/* ����������� ������������ ���������� � ���������� ��� domain */
Configurator.overwriteSubsystemsAndExtensions(
	project.properties['argus.imitation.core.package'] + "/domain/configuration",
	"domain.xml",
	project.properties['ops.overrides.processing']  + "/configuration/subsystem-overrides.xml"
);