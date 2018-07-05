package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.net.util.SubnetUtils;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDto;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressRepository;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressLifecycle;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.nls.IPSubnetAppServiceMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Сервис подсетей
 *
 * @author a.wisniewski
 * @since 12.12.2017
 */
@ApplicationService
public class IPSubnetAppService {

	/**
	 * loopback subnet
	 */
	private static SubnetUtils loopbackSubnet = new SubnetUtils("127.0.0.0/8");

	/**
	 * broadcast messages subnet
	 */
	private static SubnetUtils broadcastMessagesSubnet = new SubnetUtils("0.0.0.0/8");

	static {
		loopbackSubnet.setInclusiveHostCount(true);
		broadcastMessagesSubnet.setInclusiveHostCount(true);
	}

	/**
	 * сервис адишников
	 */
	@Inject
	private IdSequenceService idService;

	/**
	 * репозиторий подсетей
	 */
	@Inject
	private IPSubnetRepository subnetRepository;

	/**
	 * сервис истории жизненного цикла
	 */
	@Inject
	private LifecycleHistoryService historyService;

	/**
	 * сервис lifecycle routing'a
	 */
	@Inject
	private LifecycleRoutingService lifecycleService;

	/**
	 * Репозиторий ip
	 */
	@Inject
	private IPAddressRepository ipRepository;

	/**
	 * транслятор ip
	 */
	@Inject
	private IPAddressDtoTranslator ipTranslator;

	/**
	 * транслятор сетей
	 */
	@Inject
	private IPSubnetDtoTranslator subnetTranslator;

	/**
	 * Создает подсеть IPv4 и обновляет связи в существующих сетях и ip-адресах
	 *
	 * @param newSubnetDto данные создаваемой сети
	 * @param isStatic     статическая нет
	 * @return сеть
	 * @throws SubnetAlreadyExistException сеть уже существует
	 */
	public IPSubnetDto createIpv4Subnet(IPSubnetDto newSubnetDto, boolean isStatic) throws SubnetAlreadyExistException {
		String subnetName = newSubnetDto.getName();
		SubnetUtils subnet = new SubnetUtils(subnetName);
		subnet.setInclusiveHostCount(true);
		SubnetUtils.SubnetInfo subnetInfo = subnet.getInfo();

		IPSubnet alreadyExistsSubnet = subnetRepository.findByName(subnetName);

		IPSubnetAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPSubnetAppServiceMessagesBundle.class);

		if (alreadyExistsSubnet != null) {
			throw new SubnetAlreadyExistException(messages.subnetExists() + " " + subnetName, alreadyExistsSubnet.getId());
		}
		checkArgument(!subnetContainsSubnet(loopbackSubnet, subnet), messages.shouldNotBeLocalhost());
		checkArgument(!subnetContainsSubnet(broadcastMessagesSubnet, subnet), messages.shouldNotBeBroadcast());

		// создаем сеть
		IPSubnet newSubnet = new IPSubnet(idService.nextValue(IPSubnet.class));
		newSubnet.setName(subnetName);
		newSubnet.setComment(newSubnetDto.getComment());
		newSubnet.setSubnetType(IPSubnet.SubnetType.IPv4);

		// заполняем родителя
		IPSubnet closestParent = subnetRepository.getClosestParent(subnetName);
		newSubnet.setParent(closestParent);

		// заполняем дочерние сети
		List<IPSubnet> childSubnets = subnetRepository.getPossibleChildSubnets(subnetName);
		childSubnets.forEach(childSubnet -> childSubnet.setParent(newSubnet));
		newSubnet.getChildSubnets().addAll(childSubnets);

		// находим все айпишники, которые отсутствуют в дочерних подсетях, но присутствуют в базе, и добавляем их себе
		List<IPAddress> childIps = subnetRepository.getIpsThatDoesntBelongToInnerSubnets(subnetName);
		childIps.forEach(childIp -> childIp.setSubnet(newSubnet));
		newSubnet.getIpAddresses().addAll(childIps);

		// если отца нет, нужно создать отсутствующие айпишники
		if (closestParent == null) {
			// находим айпишники всех дочерних подсетей
			Set<String> childSubnetsIps = flattenSubnetTree(newSubnet)
					.flatMap(sub -> sub.getIpAddresses().stream())
					.map(LogicalResource::getName)
					.collect(toSet());
			// а так же собственные дреса
			Set<String> existingIps = childIps.stream()
					.map(LogicalResource::getName)
					.collect(toSet());
			existingIps.addAll(childSubnetsIps);

			// создаем только айпишники, которых там нет
			List<IPAddress> addressesToCreate = Arrays.stream(subnetInfo.getAllAddresses())
					.filter(ip -> !existingIps.contains(ip))
					.map(ip -> createIpAddress(ip, isStatic))
					.peek(ip -> ip.setSubnet(newSubnet))
					.collect(toList());
			newSubnet.getIpAddresses().addAll(addressesToCreate);
		}

		checkSubnetAddress(newSubnet);
		checkBroadcastAddressState(subnetInfo, newSubnet.getIpAddresses());

		subnetRepository.persist(newSubnet);
		if (closestParent != null) {
			closestParent.getChildSubnets().removeAll(childSubnets);
			closestParent.getChildSubnets().add(newSubnet);
		}
		return subnetTranslator.translateLazy(newSubnet);
	}

	/**
	 * Проверить что адрес создаваемой подсети не имеет назначение "Конфигурационный" или "Зарезервирован"
	 *
	 * @param newSubnet подсеть
	 */
	public void checkSubnetAddress(IPSubnet newSubnet) {
		IPAddress subnetIP = flattenSubnetTree(newSubnet)
				.flatMap(sub -> sub.getIpAddresses().stream())
				.filter(ip -> newSubnet.getName().startsWith(ip.getName()))
				.findFirst()
				.get();

		IPSubnetAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPSubnetAppServiceMessagesBundle.class);

		if (IPAddressPurpose.CONFIGURATION.equals(subnetIP.getPurpose())) {
			throw new IllegalStateException(messages.addressIsConfigurational());
		}
		if (IPAddressPurpose.RESERVED.equals(subnetIP.getPurpose())) {
			throw new IllegalStateException(messages.addressIsReserved());
		}
		if (IPAddressState.OCCUPIED.equals(subnetIP.getState())) {
			throw new IllegalStateException(messages.addressIsOccupied());
		}
		if (subnetIP.getBookingOrder() != null) {
			throw new IllegalStateException(messages.addressIsBooked());
		}
	}

	/**
	 * Проверяка что широковещательный адрес подсети свободен
	 * см. BOX-2194
	 *
	 * @param subnetInfo информация о подсети
	 * @param childIPs   список адресов
	 */
	public void checkBroadcastAddressState(SubnetUtils.SubnetInfo subnetInfo, List<IPAddress> childIPs) {
		checkArgument(subnetInfo != null, "No subnet information");
		if (CollectionUtils.isEmpty(childIPs)) {
			return;
		}

		IPSubnetAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPSubnetAppServiceMessagesBundle.class);

		String broadcastIP = subnetInfo.getBroadcastAddress();
		for (IPAddress ip : childIPs) {
			if (broadcastIP.equals(ip.getName())) {
				if (IPAddressPurpose.CONFIGURATION.equals(ip.getPurpose())) {
					throw new IllegalStateException(messages.broadcastAddressIsConfigurational());
				}
				if (IPAddressPurpose.RESERVED.equals(ip.getPurpose())) {
					throw new IllegalStateException(messages.broadcastAddressIsReserved());
				}
				if (IPAddressState.OCCUPIED.equals(ip.getState())) {
					throw new IllegalStateException(messages.broadcastAddressIsOccupied());
				}
				if (ip.getBookingOrder() != null) {
					throw new IllegalStateException(messages.broadcastAddressIsBooked());
				}
				break;
			}
		}
	}

	/**
	 * удаляет подсеть.
	 * если родитель есть удалить только выбранную подсеть, IP-адреса и дочерние подсети не удалять из нее.
	 * если deleteAll true удалить подсеть со всеми входящими в нее подсетями и ip-адресами
	 * если deleteAll false то удалить только подсеть со всеми её ip-адресами
	 * <p>
	 * Если ip имеет историю, то вместо удаления он переносится в архив
	 *
	 * @param deleteAll маркер удаления
	 * @param subnetId  id удаляемой подсети
	 */
	public void deleteIPv4Subnet(@Nonnull Long subnetId, boolean deleteAll) {
		IPSubnet subnet = subnetRepository.findAndLoadFomBDOne(subnetId);
		checkState(subnet != null, "No subnet found with id: " + subnetId);

		IPSubnetAppServiceMessagesBundle messages = LocaleUtils.getMessages(IPSubnetAppServiceMessagesBundle.class);

		if (subnet.getParent() == null) {
			if (deleteAll) {
				List<IPAddress> flattenedSubnetIPs = flattenSubnetTree(subnet)
						.flatMap(sub -> sub.getIpAddresses().stream())
						.collect(toList());
				List<IPAddress> ownedIPs = flattenedSubnetIPs.stream()
						.filter(ip -> IPAddressState.OCCUPIED.equals(ip.getState()))
						.collect(toList());
				checkState(ownedIPs.isEmpty(), messages.deletionIsProhibitedDueUsedIps());
				// перемещаем в архив все ip с историем
				List<IPAddress> ipsToArchive = flattenedSubnetIPs.stream()
						.filter(ip -> !historyService.getHistory(ip).isEmpty())
						.collect(toList());
				ipsToArchive.forEach(this::moveIpToArchive);
				// применяем изменения и удаляем сеть и остатки айпишников (т.к. у них нет истории)
				subnetRepository.remove(subnet);

			} else {
				List<IPAddress> subnetIPs = subnet.getIpAddresses();
				List<IPAddress> ownedIPs = subnetIPs.stream()
						.filter(ip -> IPAddressState.OCCUPIED.equals(ip.getState()))
						.collect(toList());
				checkState(ownedIPs.isEmpty(), messages.deletionIsProhibitedDueUsedIps());
				// перемещаем в архив все ip с историем
				List<IPAddress> ipsToArchive = subnetIPs.stream()
						.filter(ip -> !historyService.getHistory(ip).isEmpty())
						.collect(toList());
				ipsToArchive.forEach(this::moveIpToArchive);

				//удалить только выбранную подсеть и её IP-адреса, дочерние подсети не удалять из нее.
				subnetRepository.rebaseChildrenSubnet(subnet, null);
				subnetRepository.remove(subnet);
			}
		} else {
			//удалить только выбранную подсеть, IP-адреса и дочерние подсети не удалять из нее.
			subnetRepository.rebaseChildrenSubnet(subnet, subnet.getParent());
			for (IPAddress ip : subnet.getIpAddresses()) {
				ip.setSubnet(subnet.getParent());
				subnet.getParent().getIpAddresses().add(ip);

				ipRepository.save(ip);
			}
			subnet.getIpAddresses().clear();
			subnet.getParent().getChildSubnets().remove(subnet);
			subnetRepository.remove(subnet);
		}
		subnetRepository.flush();
	}

	/**
	 * заархивировать ip
	 *
	 * @param ip ip
	 */
	private void moveIpToArchive(IPAddress ip) {
		ip.getSubnet().getIpAddresses().remove(ip); // двухстороннее удаление
		ip.setSubnet(null);
		lifecycleService.performRouting(ip, IPAddressLifecycle.Routes.DELETE);
		ipRepository.save(ip);
	}

	/**
	 * Создает ip
	 *
	 * @param ip ip
	 * @return ip
	 */
	private IPAddress createIpAddress(String ip, boolean isStatic) {
		return IPAddress.builder()
				.id(idService.nextValue(IPAddress.class))
				.name(ip)
				.isStatic(isStatic)
				.build();
	}

	/**
	 * Преобразует дерево в стрим
	 *
	 * @param rootSubnet корень дерева
	 * @return стрим всех подсетей - элементов дерева
	 */
	private Stream<IPSubnet> flattenSubnetTree(IPSubnet rootSubnet) {
		return Stream.concat(Stream.of(rootSubnet),
				rootSubnet.getChildSubnets().stream().flatMap(this::flattenSubnetTree));
	}

	/**
	 * проверяет, входит ли подсеть в подсеть
	 *
	 * @param container     подсеть - контейнер
	 * @param subnetToCheck подсеть - складируемое
	 * @return true, если входит
	 */
	private boolean subnetContainsSubnet(SubnetUtils container, SubnetUtils subnetToCheck) {
		return container.getInfo().isInRange(subnetToCheck.getInfo().getLowAddress()) &&
				container.getInfo().isInRange(subnetToCheck.getInfo().getHighAddress());
	}

	/**
	 * Лениво найти все подсети без родителя
	 *
	 * @return список подсетей
	 */
	public List<IPSubnetDto> findParentSubnets() {
		return subnetRepository.findOnlyParentSubnets().stream().map(subnetTranslator::translateLazy).collect(toList());
	}

	/**
	 * Получить IP адреса подсети по ее ID
	 *
	 * @param subnetId ID подсети
	 * @return список IP адресов подсети
	 */
	public List<IPAddressDto> getIpAddresses(Long subnetId) {
		return ipRepository.findListIpBySubnetId(subnetId).stream().map(ipTranslator::translate).collect(toList());
	}

	/**
	 * Изменить комментарий подсети
	 *
	 * @param subnetDto ДТО подсети с новым комментом
	 */
	public void changeComment(IPSubnetDto subnetDto) {
		subnetRepository.changeComment(subnetDto.getId(), subnetDto.getComment());
	}
}
