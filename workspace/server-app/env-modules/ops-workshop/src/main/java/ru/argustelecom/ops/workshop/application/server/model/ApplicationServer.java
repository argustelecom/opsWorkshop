package ru.argustelecom.ops.workshop.application.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.ops.workshop.model.ApplicationServerStatus;
import ru.argustelecom.ops.workshop.model.Customer;
import ru.argustelecom.ops.workshop.model.OpsSuperClass;
import ru.argustelecom.ops.workshop.model.Team;
import ru.argustelecom.ops.workshop.model.UsageType;
import ru.argustelecom.ops.workshop.model.Version;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * |Имя	                        |тип	|Обяз?	|Редакт?	|Откуда данные	|
 * ------------------------------------------------------------------------------------------------------------------
 * |состояние 	                |Иконки	|ДА	    |НЕТ	    |Список: включен, выключен, заблокирован для показа. Исследование.	|
 * |имя сервера	                |текст	|ДА	    |ДА	        |                |
 * |точный номер сборки (точная версия ПО)	|текст	|НЕТ	|НЕТ	|Исследование.	|
 * |Заказчик	                |текст	|НЕТ	|ДА	        |Список из справочника Заказчики |
 * |Версия	                    |текст	|НЕТ	|ДА	        |Список из справочника Список версий |
 * |Предназначение	            |текст	|НЕТ	|ДА	        |Выбор из справочника Типы использования	|
 * |Примечание	                |текст	|НЕТ	|ДА	        |Многострочный текст до 512симв	|
 * |Команда, использующая СП	|список	|НЕТ	    |ДА	|Множественный выбор из справочника Команды.	|
 * |host	                    |текст	|ДА	    |ДА	        |	|
 * |PortSet	                    |число	|ДА	    |ДА	        |	|
 * |Install dir	                |текст	|ДА	    |ДА	        |Путь установки в Linux формате	|
 * |Ссылка на сервер	        |ссылка	|НЕТ	|НЕТ	|Вычисляемое: http://host:port/argus	|
 *
 * v.semchenko
 */
@Entity
@Table(name = "application_server", schema = "ops")
@NoArgsConstructor
public class ApplicationServer extends OpsSuperClass {

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private ApplicationServerStatus state;

	@Column(name = "name", nullable = false, length = 128)
	@Getter
	@Setter
	private String appServerName;

	@Column(name = "build_number")
	private String buildNumber;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	@Getter
	@Setter
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "version_id")
	@Getter
	@Setter
	private Version version;

	@ManyToOne
	@JoinColumn(name = "usage_type_id")
	@Getter
	@Setter
	private UsageType usageType;

	@Column(length = 512)
	@Getter
	@Setter
	private String comment;

	@ManyToMany
	@JoinTable(schema = "ops", name = "application_server_team",
			joinColumns = @JoinColumn(name = "application_server_id"),
			inverseJoinColumns = @JoinColumn(name = "team_id"))
	@Getter
	private Set<Team> teams = new LinkedHashSet<>();

	@Column(nullable = false)
	@Getter
	@Setter
	private String host;

	@Column(name = "port_offset", nullable = false)
	@Getter
	@Setter
	private int portOffSet;

	@Column(name = "appserver_install_path", nullable = false)
	@Getter
	@Setter
	private String installPath;

	@Column(name = "url_address")
	private String urlAddress;

	public ApplicationServer(ApplicationServerStatus state, String appServerName, String buildNumber,
			Customer customer, Version version, UsageType usageType, String comment,
			String host, int portOffSet, String installPath) {
		this.state = state;
		this.appServerName = appServerName;
		this.buildNumber = buildNumber;
		this.customer = customer;
		this.version = version;
		this.usageType = usageType;
		this.comment = comment;
		this.host = host;
		this.portOffSet = portOffSet;
		this.installPath = installPath;
		//вычисляем url адрес
		if ( (this.host != null) && (!this.host.isEmpty()) ) {
			this.urlAddress = "http://" + this.host + ":" + (8080 + this.portOffSet) + "/argus";
		} else {
			this.urlAddress = "";
		}
	}

	/**
	 * Добавляет в команду newTeam текущей сущность СП(this) и добавляет в колекцию teams newTeam
	 * @param newTeam
	 * @return
	 */
	public boolean addTeam(Team newTeam) {
		newTeam.addApplicationServer(this);
		return this.teams.add(newTeam);
	}

	/**
	 * Удаляет из сущности команду team из текущей сущность СП(this) и удаляет из колекции teams team
	 * @param team
	 * @return
	 */
	public boolean removeTeam(Team team) {
		team.removeApplicationServer(this);
		return this.teams.remove(team);
	}

	@Override
	public String toString() {
		return "ApplicationServer{" +
				"\nid='"+ getId() +"\nstate='" + state.toString() + '\'' +
				", \nappServerName='" + appServerName + '\'' +
				", \nbuildNumber='" + buildNumber + '\'' +
				", \n\ncustomer=" + customer.getName() +
				", \n\nversion='" + version.toString() + '\'' +
				", \nusageType='" + usageType.getName() + '\'' +
				", \ncomment='" + comment + '\'' +
				", \n\nteams=" + teams.stream().map(t -> t.getName()).toArray() +
				", \n\nhost='" + host + '\'' +
				", \nportOffSet=" + portOffSet +
				", \ninstallDirPath='" + installPath + '\'' +
				", \nurlAddress='" + urlAddress + '\'' +
				'}';
	}

	public String getUrlAddress() {
		if ( (host != null) && (!host.isEmpty()) ) {
			return urlAddress = "http://" + this.host + ":" + (8080 + this.portOffSet) + "/argus";
		} else {
			return urlAddress = "";
		}
	}
}
