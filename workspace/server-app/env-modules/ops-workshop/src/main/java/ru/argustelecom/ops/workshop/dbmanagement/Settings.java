package ru.argustelecom.ops.workshop.dbmanagement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import ru.argustelecom.ops.inf.pref.PrefTableRepository;

/**
 *
 * @author t.vildanov
 */
@ApplicationScoped
public class Settings {
    private static final String DEFAULT_POSTFIX = "_DEFAULT";
    private static final String NOT_RETRIEVED_YET = "default value for pref is not detected" +
            "01234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789" +
            "01234567890123456789012345678901234567890123456789"; // точно больше 256, таких толстых значений не бывает
    private static final String PREF_PREFIX = "ops.dbm.";
    
    @HasDefaultValue
    private static final String STATE_UPDATE_INTERVAL = "stateUpdateInterval";
    private static final Long STATE_UPDATE_INTERVAL_DEFAULT = 5 * 1000L; // в секундах
    
    @HasDefaultValue
    private static final String ORACLE_CONNECTION_STRING = "connection_string.oracle";
    private static final String ORACLE_CONNECTION_STRING_DEFAULT = "jdbc:oracle:thin:@%1$s:%2$s";

    @HasDefaultValue
    private static final String POSTGRE_CONNECTION_STRING = "connection_string.postgre";
    private static final String POSTGRE_CONNECTION_STRING_DEFAULT = "jdbc:postgresql://%1$s/%2$s";

    @Inject
    PrefTableRepository prefRepo;

    private static final Map<String, Object> defaultValues = new HashMap<>();
            
    private Object getDefaultValueFor(String prefName) {
        Object defaultValue = defaultValues.getOrDefault(prefName, NOT_RETRIEVED_YET);
        if (defaultValue instanceof String && ((String) defaultValue).equals(NOT_RETRIEVED_YET)) {
            defaultValue = null;
            for (Field fld: getClass().getDeclaredFields()) {
                if (fld.getAnnotation(HasDefaultValue.class) == null)
                    continue;
                
                Object fldValue;
                
                try {fldValue = fld.get(this);} 
                catch (IllegalAccessException e) { continue; }
                
                if (!(fldValue instanceof String))
                    continue;
                
                String fldStrValue = (String) fldValue;
                if (!fldStrValue.equals(prefName)) 
                    continue;
                
                String fieldNameForDefaultValue = fld.getName() + DEFAULT_POSTFIX;
                Field defaultValueField;
                try { defaultValueField = getClass().getDeclaredField(fieldNameForDefaultValue);}
                catch (NoSuchFieldException e) { continue;}
                
                try {defaultValue = defaultValueField.get(this);} 
                catch (IllegalAccessException e) { continue;}
                
                break;
            }
            defaultValues.put(prefName, defaultValue);
        }
        return defaultValue;
    }
    
    private Object getMyPref(String prefName) {
        return prefRepo.getPref(PREF_PREFIX + prefName, getDefaultValueFor(prefName));
    }
    
    public Long getDBStateUpdateInterval() {
        return (Long) getMyPref(STATE_UPDATE_INTERVAL);
    }
    
    public String getOracleConnectionString(String host, String dbName) {
        return String.format((String) getMyPref(ORACLE_CONNECTION_STRING), host, dbName);
    }
    
    public String getPostgreConnectionString(String host, String dbName) {
        return String.format((String) getMyPref(POSTGRE_CONNECTION_STRING), host, dbName);
    }
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface HasDefaultValue {}