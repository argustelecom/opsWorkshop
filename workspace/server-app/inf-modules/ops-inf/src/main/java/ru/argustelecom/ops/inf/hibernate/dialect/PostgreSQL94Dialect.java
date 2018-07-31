package ru.argustelecom.ops.inf.hibernate.dialect;

import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.spatial.SpatialDialect;
import org.hibernate.type.StandardBasicTypes;

import ru.argustelecom.ops.inf.hibernate.func.PostgreSQLJsQueryMatchingPseudoFunction;
public class PostgreSQL94Dialect extends org.hibernate.spatial.dialect.postgis.PostgisDialect {


	private final ViolatedConstraintNameExtracter constraintNameExtracter;

	public PostgreSQL94Dialect() {
		registerColumnType(Types.JAVA_OBJECT, "jsonb");
		registerColumnType(Types.ARRAY, "int-array");
		registerColumnType(Types.ARRAY, "bigint-array");
		registerColumnType(Types.ARRAY, "string-array");

		registerFunction("jsq", new PostgreSQLJsQueryMatchingPseudoFunction());

		constraintNameExtracter = new PreffixBasedConstraintNameExtracter(super.getViolatedConstraintNameExtracter());

		// функция проверяещая находится ли точка 1 от точки 2 на заданном в метрах параметром 3 расстоянии
		// ST_DWithin использует для проверки индексы, созданные в БД
		// http://postgis.net/docs/ST_DWithin.html
		registerFunction("within_meters", new SQLFunctionTemplate(StandardBasicTypes.STRING, "ST_DWithin(?1, ?2, ?3)"));

		// сортирует обхекты по расстоянию между точками 1 и 2 по возрастанию
		// оператор <-> использует индексы для рассчёта расстояния. рекомендуется использовать совместно с функцией
		// within_meters, для ограничения границ поиска обхектов
		// http://postgis.net/docs/geometry_distance_knn.html
		registerFunction("nearest_to", new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1 <-> ?2::geometry"));
	}

	@Override
	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return constraintNameExtracter;
	}

	private static class PreffixBasedConstraintNameExtracter implements ViolatedConstraintNameExtracter {

		private final Pattern pattern;
		private final ViolatedConstraintNameExtracter nextExtracter;

		public PreffixBasedConstraintNameExtracter(ViolatedConstraintNameExtracter nextExtracter) {
			this.pattern = Pattern.compile("\"([^\"]*)\"");
			this.nextExtracter = nextExtracter;
		}

		@Override
		public String extractConstraintName(SQLException sqle) {
			SQLException currentSqle = sqle;
			try {
				String constraintName = null;
				do {
					constraintName = doExtractConstraintName(currentSqle);
					if (currentSqle.getNextException() == null || currentSqle.getNextException() == currentSqle) {
						break;
					} else {
						currentSqle = currentSqle.getNextException();
					}
				} while (constraintName == null);

				if (constraintName == null && nextExtracter != null) {
					constraintName = nextExtracter.extractConstraintName(sqle);
				}
				return constraintName;
			} catch (NumberFormatException nfe) {
				return null;
			}
		}

		public String doExtractConstraintName(SQLException sqle) {
			final int sqlState = Integer.valueOf(JdbcExceptionHelper.extractSqlState(sqle));
			switch (sqlState) {
			case 23514:
			case 23505:
			case 23503:
			case 23502:
			case 23001:
				return doExtractConstraintNameWithPreffixBasedMatching(sqle.getMessage());
			default:
				return null;
			}
		}

		private String doExtractConstraintNameWithPreffixBasedMatching(String message) {
			Matcher m = pattern.matcher(message);
			while (m.find()) {
				String constraintName = m.group(1).toLowerCase();
				if (isConstraint(constraintName)) {
					return constraintName;
				}
			}
			return null;
		}

		private boolean isConstraint(String constraintName) {
			//@formatter:off
			return constraintName.startsWith("pk_")   // PRIMARY KEY
				|| constraintName.startsWith("fk_")   // FOREIGN KEY
				|| constraintName.startsWith("uc_")   // UNIQUE CONSTRAINT
				|| constraintName.startsWith("cc_")   // CHECK CONSTRAINT
				|| constraintName.startsWith("tc_")   // CONSTRAINT TRIGGER
				|| constraintName.startsWith("xc_");  // EXCLUSION CONSTRAINT
			//@formatter:on
		}
	}
}
