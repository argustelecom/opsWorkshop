package ru.argustelecom.box.env.security.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import ru.argustelecom.system.inf.configuration.packages.model.PackageDescriptor;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "ru.argustelecom.readonly-cache-region")
public class Permission implements Serializable, NamedObject {

	@Id
	@Column(name = "id", length = 100)
	private String id;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Permission parent;

	@Size(max = 100)
	@Column(length = 100)
	private String name;

	@Size(max = 250)
	@Column(length = 250)
	private String description;

	@OneToMany(mappedBy = "parent")
	private List<Permission> children = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "entity_package_id")
	private PackageDescriptor module;

	protected Permission() {
	}

	public String getId() {
		return id;
	}

	public Permission getParent() {
		return parent;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public PackageDescriptor getModule() {
		return module;
	}

	public List<Permission> getChildren() {
		return Collections.unmodifiableList(children);
	}

	private static final long serialVersionUID = 829509381183815885L;
}
