package ru.argustelecom.box.env.address;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.inf.service.ApplicationService;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toCollection;

/**
 * Сервис адресов
 * @author a.wisniewski
 * @since 29.09.2017
 */
@ApplicationService
public class LocationAppService {

	/**
	 * Репозиторий адресов
	 */
	@Inject
	private LocationRepository locationRepository;

	/**
	 * Получает все адреса, похожие на заданный
	 * @param path адрес (не обязательно полный)
	 * @param maxResCount максимальное количество результатов
	 * @return список адресов
	 */
	public List<Location> getLocationsLike(String path, int maxResCount) {
		Deque<String> locationPath = getLocationPath(path);
		return locationRepository.searchLocationsLike(locationPath, maxResCount);
	}

	/**
	 * разбивает string'овый адрес на очередь кусочков
	 * @param location адрес
	 * @return очередь элементов адреса
	 */
	private Deque<String> getLocationPath(String location) {
		if (StringUtils.isEmpty(location))
			return new ArrayDeque<>();
		return Pattern.compile(",").splitAsStream(location)
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.collect(toCollection(ArrayDeque::new));
	}
}
