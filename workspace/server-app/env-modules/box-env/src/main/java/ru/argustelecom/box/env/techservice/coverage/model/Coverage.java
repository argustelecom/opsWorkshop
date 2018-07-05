package ru.argustelecom.box.env.techservice.coverage.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = @UniqueConstraint(name = "uc_coverage", columnNames = { "building_id" }))
public class Coverage extends BusinessObject {

	private static final long serialVersionUID = -3158172775549916371L;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "building_id")
	public Building building;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "state_id")
	public CoverageState state;

	@Column(length = 512)
	private String note;

	protected Coverage() {
	}

	public Coverage(Long id) {
		super(id);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public CoverageState getState() {
		return state;
	}

	public void setState(CoverageState state) {
		this.state = state;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class CoverageQuery extends EntityQuery<Coverage> {

		private EntityQueryEntityFilter<Coverage, Building> building = createEntityFilter(Coverage_.building);
		private EntityQueryEntityFilter<Coverage, CoverageState> state = createEntityFilter(Coverage_.state);

		public CoverageQuery() {
			super(Coverage.class);
		}

		public EntityQueryEntityFilter<Coverage, Building> building() {
			return building;
		}

		public EntityQueryEntityFilter<Coverage, CoverageState> state() {
			return state;
		}

	}

}