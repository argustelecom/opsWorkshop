/*
 * Скрипт предназначен для создания jar-архива sql в соответствии с настройками, 
 * указанными в dbm-archive.properties
 * 
 * Непосредственное создание jar-архива делегируется dbmaintain.
 *
 * Базовые свойства dbm-archive.properties могут быть расширены и перекрыты при 
 * помощи командной строки, например
 * mvn clean package -DarchiveConfigExtension=specific/config.properties
 * Это удобно использовать, например, для создания архива, спецефичного 
 * определенному заказчику или какой-то особой настройки
 *
 * Дополнительно, этот скрипт создает dbmaintain.properties, свойства, с которыми
 * будут запускать апдейт в конкретном месте его применения. Шаблон для этих свойств
 * также конфигурируется и может содержать преднастроенные параметры баз заказчика, 
 * такие как хосты, порты, имена баз, логины суперпользователя и т.д.
 */
import Loader;
import org.dbmaintain.MainFactory
import org.dbmaintain.config.DbMaintainProperties

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

final SCRIPT_LOC      = project.properties['this.scriptdir'];
final CONFIG_DEF_PATH = project.properties['this.basedir'] + '/dbm-work.properties';
final CONFIG_ARC_PATH = project.properties['this.basedir'] + '/dbm-archive.properties';
final CONFIG_EXT_PATH = project.properties['archiveConfigExtension'];
final CONFIG_TEMPLATE = project.properties['dbm.props.template'];

println "[INFO]";
println "[INFO] Creating script archive...";
println "[INFO]   DBMaintainer work properties: ${CONFIG_DEF_PATH}";
println "[INFO]   Default archive properties: ${CONFIG_ARC_PATH}";
println "[INFO]   Archive properties extension: ${CONFIG_EXT_PATH}";
println "[INFO]   Scripts location: ${SCRIPT_LOC}" ;
println "[INFO]   dbmaintain.properties template: ${CONFIG_TEMPLATE}";


// ****************************************************************************************************** 
// Стандартные проверки 
// ******************************************************************************************************
def scriptsLocation = new File(SCRIPT_LOC);
if (!scriptsLocation.exists() && !scriptsLocation.isDirectory()) {
    throw new RuntimeException("Invalid script location directory. Check mvn property <this.scriptdir>");
}

// ****************************************************************************************************** 
// Создание архива скриптов для update
// ******************************************************************************************************

def loader = new Loader();

// Загрузка дефолтных свойств из jar
def configuration = loader.loadConfiguration();

// Загрузка dbm-work.properties
def propsFile = new File(CONFIG_DEF_PATH);
if (propsFile.exists()) {
    configuration.putAll(loader.loadPropertiesFromFile(propsFile));
} else {
    throw new RuntimeException("Work configuration is not defined. Check file ${CONFIG_DEF_PATH}");
}

// Загрузка dbm-archive.properties
propsFile = new File(CONFIG_ARC_PATH);
if (propsFile.exists()) {
    configuration.putAll(loader.loadPropertiesFromFile(propsFile));
} else {
    throw new RuntimeException("Archive configuration is not defined. Check file ${CONFIG_ARC_PATH}");
}

// Загрузка расширения, которое определил пользователь при вызове mvn
if (CONFIG_EXT_PATH != null) {
    propsFile = new File(CONFIG_EXT_PATH);
    if (propsFile.exists()) {
        configuration.putAll(loader.loadPropertiesFromFile(propsFile));
        println "[INFO] Archive properties overwritten with user defined extension!";
    }
}

configuration.put(DbMaintainProperties.PROPERTY_SCRIPT_LOCATIONS, scriptsLocation.absolutePath);

def archiveName = (String) configuration.get("dbMaintainer.archive.name");
if (archiveName == null || "".equals(archiveName)){
    archiveName = 'update-all.jar';
}

def archivePath = project.properties['dbm.output.dir'] + '/' + archiveName;
println "[INFO] Script archive path ${archivePath}";

// Архивер расстраивается, если нет директории
File archiveDirectory = new File(archivePath).getParentFile();
if (!archiveDirectory.exists()) {
    archiveDirectory.mkdirs();
}

new MainFactory(configuration).createScriptArchiveCreator().createScriptArchive(archivePath);
println "[INFO] Script archive was successfully created!";


// ****************************************************************************************************** 
// Создание dbmaintain.properties
// ******************************************************************************************************

println "[INFO]";
println "[INFO] Creating dbmaintain.properties...";

// Попытка загрузить шаблон свойств
def dbmProp = new Properties();
if (CONFIG_TEMPLATE != null) {
    propsFile = new File(CONFIG_TEMPLATE);
    if (propsFile.exists()) {
        dbmProp.putAll(loader.loadPropertiesFromFile(propsFile));
        println "[INFO] dbmaintain.properties template successfully loaded!";
    }
}

// Если шаблона нет, то не будем расстраиваться и создадим шаблон по-умолчанию
if (dbmProp.isEmpty()) {
    dbmProp.put(DbMaintainProperties.PROPERTY_DATABASE_NAMES, "");
    dbmProp.put(DbMaintainProperties.PROPERTY_DRIVERCLASSNAME, "");
    dbmProp.put(DbMaintainProperties.PROPERTY_URL, "");
    dbmProp.put(DbMaintainProperties.PROPERTY_USERNAME, "");
    dbmProp.put(DbMaintainProperties.PROPERTY_PASSWORD, "");
    dbmProp.put(DbMaintainProperties.PROPERTY_DIALECT, "");
}

dbmProp.put(DbMaintainProperties.PROPERTY_SCRIPT_LOCATIONS, archiveName);
loader.savePropertiesToFile(dbmProp, new File(project.properties['dbm.output.dir'] + '/dbmaintain.properties'));
println "[INFO] dbmaintain.properties created!";