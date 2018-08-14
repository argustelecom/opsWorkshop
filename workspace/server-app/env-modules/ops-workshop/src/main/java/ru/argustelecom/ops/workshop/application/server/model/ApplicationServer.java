package ru.argustelecom.ops.workshop.application.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.ops.workshop.model.ApplicationServerStatus;
import ru.argustelecom.ops.workshop.model.Customer;
import ru.argustelecom.ops.workshop.model.Team;
import ru.argustelecom.ops.workshop.model.UsageType;
import ru.argustelecom.ops.workshop.model.Version;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class ApplicationServer implements Serializable {

	private static final long serialVersionUID = 3723839088936005054L;

	@Id
	@Getter
	@Setter
	@GeneratedValue
	private long id;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private ApplicationServerStatus state;

	@Column(name = "appserver_name", nullable = false, length = 128)
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
	@JoinTable(schema = "ops", name = "appservers_teams",
			joinColumns = @JoinColumn(name = "appserver_id"),
			inverseJoinColumns = @JoinColumn(name = "team_id"))
	@Getter
	@Setter
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
	@Getter
	@Setter
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
		this.urlAddress = "";
	}

	public boolean addTeam(Team team) {
		return this.teams.add(team);
	}

	@Override
	public String toString() {
		return "ApplicationServer{" +
				"\nid=" + id +
				", \nstate='" + state.toString() + '\'' +
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

}
