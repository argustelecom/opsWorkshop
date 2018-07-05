package ru.argustelecom.box.nri.resources.lifecycle.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Переход между фазами ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "resource_lifecycle_phase_links")
@Getter
@Setter
public class ResourceLifecyclePhaseTransition extends BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Фаза в которую приходим
	 */
	@JoinColumn(name = "outcoming_phase_id", nullable = false)
	@ManyToOne
	private ResourceLifecyclePhase outcomingPhase;

	/**
	 * Фаза из которой переходим
	 */
	@JoinColumn(name = "incoming_phase_id", nullable = false)
	@ManyToOne
	private ResourceLifecyclePhase incomingPhase;

	/**
	 * Название перехода
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ResourceLifecyclePhaseTransition() {
		super();
	}

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param outcomingPhase фаза в которую переходим
	 * @param incomingPhase фпзп из которой переходим
	 * @param comment название перехода
	 */
	@Builder
	public ResourceLifecyclePhaseTransition(Long id, ResourceLifecyclePhase outcomingPhase, ResourceLifecyclePhase incomingPhase, String comment) {
		this.id = id;
		this.outcomingPhase = outcomingPhase;
		this.incomingPhase = incomingPhase;
		this.comment = comment;
	}
}
