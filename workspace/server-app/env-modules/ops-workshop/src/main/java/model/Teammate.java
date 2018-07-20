package model;

import lombok.Getter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
public class Teammate {
	@Id
	@GeneratedValue
	@Getter @Setter
	private int id;

	@Column
	@Getter @Setter
	private String FIO;

	@Column
	@Getter @Setter
	private String jiraName;

	@Column
	@Getter @Setter
	private String email;

	@ManyToMany(cascade = CascadeType.ALL)
	@Getter @Setter
	@JoinTable(name = "team_teammate",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "teammate_id"))
	private List<Team> teams;

	@Enumerated(value = EnumType.STRING)
	@Column(length = 32)
	@Getter @Setter
	private WatchingType versionWatchingType;

	@Enumerated(value = EnumType.STRING)
	@Column(length = 32)
	@Getter @Setter
	private WatchingType deliveryWatchingType;

	public enum WatchingType {
		ALWAYS("Всегда"),
		MYTEAM("Когда участвует команда"),
		NEWER("Никогда");

		private String desc;

		WatchingType(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return desc;
		}

	}

	private Teammate() {
		teams = new ArrayList<>();
	}

	@Override public String toString() {
		return "Teammate{" +
				"id=" + id +
				", FIO='" + FIO + '\'' +
				", jiraName='" + jiraName + '\'' +
				", email='" + email + '\'' +
				", teams=" + (teams == null ? "NULL" : "["+teams.stream().map(Team::getName).collect(Collectors.joining(","))+"]") +
				", versionWatchingType=" + versionWatchingType +
				", deliveryWatchingType=" + deliveryWatchingType +
				'}';
	}

	public Teammate(String FIO, String email, String jiraName, Team team) {
		this();
		this.FIO = FIO;
		this.email = email;
		this.jiraName = jiraName;
		teams.add(team);
	}

	public Boolean addToTeam(Team team) {
		return teams.add(team);
	}

}
