import Loader;

final WORK_PROPS = project.properties['this.basedir'] + '/dbm-work.properties';
final MY_PROPS   = project.properties['this.basedir'] + '/dbm-my.properties';
final OUTPUT     = project.properties['this.target']  + '/dbmaintain-default.properties';

println "[INFO]";
println "[INFO] Overwriting dbm default properties with dbm-work.properties...";
println "[INFO]   DBM work properties location: " + WORK_PROPS;
println "[INFO]   Result: " + OUTPUT;

def propsFile = new File(WORK_PROPS);
def loader = new Loader();
def props = propsFile.exists() ? loader.loadConfiguration(propsFile) : loader.loadConfiguration();

// Загрузка дефолтных свойств из jar
def configuration = loader.loadConfiguration();

// Загрузка стандартного расширения конфигурации из соответствующего файла
propsFile = new File(WORK_PROPS);
if (propsFile.exists()) {
    configuration.putAll(loader.loadPropertiesFromFile(propsFile));
    println "[INFO] DBM work properties loaded";
} else {
    throw new RuntimeException("DBM work properties is not defined. Check file ${WORK_PROPS}");
}

propsFile = new File(MY_PROPS);
if (propsFile.exists()) {
    configuration.putAll(loader.loadPropertiesFromFile(propsFile));
    println "[INFO] DBM my properties loaded";
}

loader.savePropertiesToFile(props, new File(OUTPUT));
println "[INFO] DBM default properties was successfully overwritten!";


