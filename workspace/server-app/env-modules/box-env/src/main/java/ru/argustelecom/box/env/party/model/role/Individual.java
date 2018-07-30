package ru.argustelecom.box.env.party.model.role;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.PersonName_;
import ru.argustelecom.box.env.party.model.Person_;

@Entity
@Access(AccessType.FIELD)
public class Individual extends Customer {

	private static final long serialVersionUID = -2726848717634055491L;

	protected Individual() {
	}

	public Individual(Long id) {
		super(id);
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class IndividualQuery extends CustomerQuery<Individual> {

		private Join<Individual, Person> personJoin;

		public IndividualQuery() {
			super(Individual.class);
		}

		public Predicate byFirstName(String firstName) {
			return firstName == null ? null
					: criteriaBuilder().like(
							criteriaBuilder().upper(personJoin().get(Person_.name).get(PersonName_.firstName)),
							createParam(PersonName_.firstName, contains(firstName)));
		}

		public Predicate bySecondName(String secondName) {
			return secondName == null ? null
					: criteriaBuilder().like(
							criteriaBuilder().upper(personJoin().get(Person_.name).get(PersonName_.secondName)),
							createParam(PersonName_.secondName, contains(secondName)));
		}

		public Predicate byLastName(String lastName) {
			return lastName == null ? null
					: criteriaBuilder().like(
							criteriaBuilder().upper(personJoin().get(Person_.name).get(PersonName_.lastName)),
							createParam(PersonName_.lastName, contains(lastName)));
		}

		public Predicate byFullName(String fullName) {
			return fullName == null ? null
					: criteriaBuilder().like(criteriaBuilder().upper(criteriaBuilder().function("concat", String.class,
							criteriaBuilder().coalesce(personJoin().get(Person_.name).get(PersonName_.lastName), ""),
							criteriaBuilder().coalesce(personJoin().get(Person_.name).get(PersonName_.firstName), ""),
							criteriaBuilder().coalesce(personJoin().get(Person_.name).get(PersonName_.secondName),
									""))),
							String.format("%s%%", fullName.replace(" ", "").toUpperCase()));
		}

		private Join<Individual, Person> personJoin() {
			if (personJoin == null)
				personJoin = root().join(Individual_.party.getName(), JoinType.INNER);
			return personJoin;
		}

		private String contains(String value) {
			return String.format("%%%s%%", value.toUpperCase().trim());
		}

	}

}