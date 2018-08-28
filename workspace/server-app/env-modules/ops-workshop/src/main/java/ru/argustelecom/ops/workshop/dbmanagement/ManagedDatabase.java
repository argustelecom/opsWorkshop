package ru.argustelecom.ops.workshop.dbmanagement;

import javax.inject.Inject;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.OverridesAttribute;

import ru.argustelecom.ops.workshop.model.OpsSuperClass;
/**
 *
 * @author t.vildanov
 */
@Entity
@Table(name = "managed_database",
		uniqueConstraints = {@UniqueConstraint(columnNames = {
				ManagedDatabase.COLUMN_DB_TYPE,
				ManagedDatabase.COLUMN_DB_SID,
				ManagedDatabase.COLUMN_DB_HOST
		})})
public class ManagedDatabase extends OpsSuperClass {
	static final String COLUMN_DB_SID = "db_sid";
	static final String COLUMN_DB_TYPE = "db_type";
	static final String COLUMN_DB_HOST = "db_host";

    public final String STATE_ICON_OFFLINE = "offline";
    public final String STATE_ICON_ONLINE = "online";
    public final String STATE_ICON_LOCKED = "locked";
    public final String STATE_ICON_FLASHBACKON = "flashbackon";

	@Inject
	private Settings settings;

    @Id
	@GeneratedValue
	@Column(name = "managed_database_id", unique = true, nullable = false)
    private Long databaseId;

	@Column(name = "db_sid", nullable = false)
	private String sid;

	@Column(name = "db_host", nullable = false)
	private String host;

	@Column(name = "db_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private DatabaseType type;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = Status.VALUE_ATTR_NAME, column = @Column(name = "is_online")),
			@AttributeOverride(name = Status.CHECK_ATTR_NAME, column = @Column(name = "last_online_check"))
	})
    private Status onlineStatus;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = Status.VALUE_ATTR_NAME, column = @Column(name = "flashback_on")),
			@AttributeOverride(name = Status.CHECK_ATTR_NAME, column = @Column(name = "last_flashback_check"))
	})
    private Status flashbackStatus;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = Status.VALUE_ATTR_NAME, column = @Column(name = "is_locked"))
	})
    private Status lockStatus;
    
    @Column(name = "last_online_check")
    private Long lastDbOnlineCheck;
    
    @Column(name = "last_props_check")
    private Long lastPropsCheck;
                
    private boolean isDbStateDetectingState;
        
    private boolean needUpdateDbState() {
        return (lastDbStateUpdate == null) ||
                ((System.currentTimeMillis() - lastDbStateUpdate) > settings.getDBStateUpdateInterval());
    }
    
    private void detectDbState() {
        lastDbStateUpdate = System.currentTimeMillis();
    }
    
    public String getDbStateIconId() {
        if (needUpdateDbState())
            detectDbState();
        
        if (!getIsOnline())
            return STATE_ICON_OFFLINE;
        
        if (getIsLocked())
            return STATE_ICON_LOCKED;
        
        if (getFlashbackOn())
            return STATE_ICON_FLASHBACKON;
            
        return STATE_ICON_ONLINE;
    }
    
    private ManagedDatabase() {}
    public ManagedDatabase(String databaseSID, String host) {
        databaseId = new DatabaseId(databaseSID, host);
        detectDbState();
    }
    
    private Boolean isOnlineStatusUpdating;
    public boolean onlineStatusUpdating() {
        synchronized(isOnlineStatusUpdating) {
            return isOnlineStatusUpdating;
        }
    }
    
    public void updateOnlineStatus() {
        if (!needUpdateDbState())
            return;
        synchronized(isOnlineStatusUpdating) {
            if (isOnlineStatusUpdating)
                return;
            isOnlineStatusUpdating = true;
        }
        try {
            doUpdateOnlineStatus();
        } 
        finally {
            synchronized(isOnlineStatusUpdating) {
                isOnlineStatusUpdating = false;
            }
        }
    }
    
    private void doUpdateOnlineStatus() {
        
    }
    
    public Boolean getIsOnline() {
        synchronized(isOnline) {
            return isOnline;
        }
    }
    
    private Boolean isPropsUpdating;
    public boolean propsUpdating() {
        synchronized(isPropsUpdating) {
            return isPropsUpdating;
        }
    }
    
    
    public void updateProps() {
        
    }
    
    private Boolean isExtendedPropsUpdating;
    public boolean extendedPropsUpdating() {
        synchronized(isExtendedPropsUpdating) {
            return isExtendedPropsUpdating;
        }
    }
    
    public void updateExtendedProps() {

    }
}
