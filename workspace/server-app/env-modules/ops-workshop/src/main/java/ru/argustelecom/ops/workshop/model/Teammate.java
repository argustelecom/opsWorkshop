package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "teammate")
@NoArgsConstructor
public class Teammate extends OpsSuperClass {

	@Column
	@Getter
	@Setter
	private String FIO;

	@Column(name = "jira_name")
	@Getter
	@Setter
	private String jiraName;

	@Column(name = "email")
	@Getter
	@Setter
	private String email;

	@ManyToMany(cascade = CascadeType.ALL)
	@Getter
	@JoinTable(
			name = "team_teammate",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "teammate_id"))
	private List<Team> teams = new ArrayList<>();;

	@Enumerated(value = EnumType.STRING)
	@Column(length = 32, name = "version_watching_type")
	@Getter
	@Setter
	private WatchingType versionWatchingType;

	@Enumerated(value = EnumType.STRING)
	@Column(length = 32, name = "delivery_watching_type")
	@Getter
	@Setter
	private WatchingType deliveryWatchingType;

	public Teammate(String FIO, String email, String jiraName) {
		this.FIO = FIO;
		this.email = email;
		this.jiraName = jiraName;
	}

	public Boolean addToTeam(Team team) {
		return teams.add(team);
	}

	@Override
	public String toString() {
		return "Teammate{" + "id=" + getId() + ", FIO='" + FIO + '\'' + ", jiraName='" + jiraName + '\'' + ", email='"
				+ email + '\'' + ", teams="
				+ (teams == null ? "NULL"
				: "[" + teams.stream().map(Team::getName).collect(Collectors.joining(",")) + "]")
				+ ", versionWatchingType=" + versionWatchingType + ", deliveryWatchingType=" + deliveryWatchingType
				+ '}';
	}

}
