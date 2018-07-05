package ru.argustelecom.box.env.techservice.coverage.model;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.queryCachedEntities;

import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.ImmutableMap;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.utils.CheckUtils;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = @UniqueConstraint(name = "uc_coverage_state", columnNames = { "name" }))
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = CoverageState.CACHE_REGION_NAME)
public class CoverageState extends BusinessDirectory {

	private static final long serialVersionUID = -7878376837516744989L;

	public static final String CACHE_REGION_NAME = "ru.argustelecom.box.techservice-cache-region";

	public static final long CONNECT_ID = 1;
	public static final long WAIT_CONNECTION_ID = 2;

	public CoverageState connect() {
		return find(CONNECT_ID, null);
	}

	public CoverageState wainConnection() {
		return find(WAIT_CONNECTION_ID, null);
	}

	public static CoverageState find(long id, EntityManager em) {
		checkArgument(CheckUtils.isValidId(id));
		return ensure(em).find(CoverageState.class, id);
	}

	public static List<CoverageState> values() {
		return values(ensure(null));
	}

	public static List<CoverageState> values(EntityManager em) {
		return queryCachedEntities(ensure(em), CoverageState.class, CoverageState.CACHE_REGION_NAME);
	}

	private static final Map<Long, String> colorMap = ImmutableMap.of(CONNECT_ID, "green", WAIT_CONNECTION_ID,
			"orange");

	// TODO: пока не понятно будет ли много состояний, будет ли возможность у пользователя создавать состояния, поэтому
	// цвет получаем хардкодом
	public String getColor() {
		return colorMap.get(getId());
	}

	protected CoverageState() {
		super();
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name", length = 32)
	public String getObjectName() {
		return super.getObjectName();
	}

	@Override
	public Boolean getIsSys() {
		return true;
	}

}