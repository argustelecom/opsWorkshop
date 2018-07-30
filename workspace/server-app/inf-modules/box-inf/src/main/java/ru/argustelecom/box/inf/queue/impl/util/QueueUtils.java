package ru.argustelecom.box.inf.queue.impl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.queue.api.QueueProducer.Priority;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueStatus;
import ru.argustelecom.system.inf.exception.ExceptionUtils;
import ru.argustelecom.system.inf.exception.SystemException;

import com.google.common.base.Throwables;

public final class QueueUtils {

	private QueueUtils() {
	}

	public static int getAttempsCount(QueueEvent event, boolean increment) {
		if (event.getLastError() != null) {
			int attemptsCount = event.getLastError().getAttemptsCount();
			return increment ? ++attemptsCount : attemptsCount;
		}
		return 1;
	}

	public static ErrorInfo getErrorInfo(Throwable exception) {
		ErrorInfo errorInfo = new ErrorInfo();
		if (exception != null) {
			Throwable busiex = ExceptionUtils.getBusinessException(exception);
			if (busiex != null) {
				errorInfo.errorText = ExceptionUtils.getBusinessExceptionMessage(busiex);
				errorInfo.errorClass = busiex.getClass().getName();
			} else {
				Throwable root = Throwables.getRootCause(exception);
				errorInfo.errorText = "";
				errorInfo.errorClass = root.getClass().getName();
				if (root.getMessage() != null && !root.getMessage().equals("")) {
					errorInfo.errorText += root.getMessage();
				} else {
					errorInfo.errorText += root.toString();
				}
			}
		}
		return errorInfo;
	}

	public static class ErrorInfo {
		public int errorType;
		public String errorClass;
		public String errorText;
	}

	/**
	 * Очень кривой помощник для парсинга результата выполнения запроса. Не хочется здесь опускаться до уровня JDBC,
	 * чтобы получить ResultSet, а аннотацию @ResultSetMapping написать негде, потому что нет сущностей
	 */
	public static final class ResultSet {

		private ResultSet() {
		}

		public static String getString(String fieldName, Object field, boolean required) {
			if (field instanceof String) {
				return (String) field;
			}
			if (required) {
				throw LocaleUtils.exception(SystemException.class, "Required field {0} is not specified", fieldName);
			}
			return null;
		}

		public static QueueStatus getStatus(String fieldName, Object field, boolean required) {
			String state = getString(fieldName, field, required);
			return QueueStatus.valueOf(state);
		}

		public static Priority getPriority(String fieldName, Object field, boolean required) {
			Long priorityValue = getLong(fieldName, field, required);
			return priorityValue != null ? Priority.valueOf(priorityValue.intValue()) : Priority.MEDIUM;
		}

		public static Long getLong(String fieldName, Object field, boolean required) {
			if (field instanceof Long) {
				return (Long) field;
			}
			if (field instanceof Integer) {
				return ((Integer) field).longValue();
			}
			if (field instanceof BigInteger) {
				return ((BigInteger) field).longValue();
			}
			if (field instanceof BigDecimal) {
				return ((BigDecimal) field).longValue();
			}
			if (required) {
				throw LocaleUtils.exception(SystemException.class, "Required field {0} is not specified", fieldName);
			}
			return null;
		}

		public static Boolean getBoolean(String fieldName, Object field, boolean required) {
			if (field instanceof Boolean) {
				return (Boolean) field;
			}
			if (field instanceof String) {
				return Boolean.valueOf((String) field);
			}
			if (required) {
				throw LocaleUtils.exception(SystemException.class, "Required field {0} is not specified", fieldName);
			}
			return null;
		}

		public static Date getDate(String fieldName, Object field, boolean required) {
			if (field instanceof Date) {
				return (Date) field;
			}
			if (required) {
				throw LocaleUtils.exception(SystemException.class, "Required field {0} is not specified", fieldName);
			}
			return null;
		}
	}

}
