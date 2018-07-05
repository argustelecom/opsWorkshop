package ru.argustelecom.box.nri.resources.lifecycle.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Фаза жизненного цикла ресурса
 * Created by s.kolyada on 02.11.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_lifecycle_phase")
@Getter
@Setter
public class ResourceLifecyclePhase extends BusinessObject implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Название фазы жизненного цикла
	 */
	@Column(name = "phase_name", nullable = false)
	private String phaseName;

	/**
	 * Жизненный цикл к оторому относится фаза
	 */
	@ManyToOne
	@JoinColumn(name = "lifecycle_id", nullable = false)
	private ResourceLifecycle currentLifecycle;

	/**
	 * Исходящие переходы в другие фазы
	 */
	@OneToMany(mappedBy = "incomingPhase", cascade= CascadeType.ALL)
	private Set<ResourceLifecyclePhaseTransition> outcomingPhases = new HashSet<>();


	/**
	 * Входящие переходы в другие фазы
	 */
	@OneToMany(mappedBy = "outcomingPhase", cascade= CascadeType.ALL)
	private Set<ResourceLifecyclePhaseTransition> incomingPhases = new HashSet<>();

	/**
	 * Координата х для отображения на графе
	 */
	@Column(name = "x")
	private String x;

	/**
	 * Координата у для отображения на графе
	 */
	@Column(name = "y")
	private String y;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceLifecyclePhase() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param phaseName имя фазы
	 * @param currentLifecycle текущий ЖЦ
	 * @param outcomingPhases выходы из фазы
	 * @param incomingPhases входы в фазу
	 * @param x координата х
	 * @param y координата у
	 */
	@Builder
	public ResourceLifecyclePhase(Long id, String phaseName,
								  ResourceLifecycle currentLifecycle,
								  Set<ResourceLifecyclePhaseTransition> outcomingPhases,
								  Set<ResourceLifecyclePhaseTransition> incomingPhases,
								  String x, String y) {
		this.id = id;
		this.phaseName = phaseName;
		this.currentLifecycle = currentLifecycle;
		this.outcomingPhases = Optional.ofNullable(outcomingPhases).orElse(new HashSet<>());
		this.incomingPhases = Optional.ofNullable(incomingPhases).orElse(new HashSet<>());
		this.x = x;
		this.y = y;
	}

	@Override
	public String getObjectName() {
		return phaseName;
	}
}
