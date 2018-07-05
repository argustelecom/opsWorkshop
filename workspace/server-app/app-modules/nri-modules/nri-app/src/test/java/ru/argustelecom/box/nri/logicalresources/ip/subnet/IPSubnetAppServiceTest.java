package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import com.google.common.collect.ImmutableList;
import org.apache.commons.net.util.SubnetUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressRepository;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author a.wisniewski
 * @since 13.12.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class IPSubnetAppServiceTest {

	@Mock
	private IPSubnetRepository subnetRepository;

	@Mock
	private IdSequenceService idSequenceService;

	@Mock
	private LifecycleHistoryService historyService;

	@Mock
	private IPAddressRepository ipRepository;

	@Mock
	private LifecycleRoutingService lifecycleService;

	@Mock
	private IPSubnetDtoTranslator subnetDtoTranslator;

	@InjectMocks
	private IPSubnetAppService subnetService;

	@Test
	public void createIPv4Subnet_withoutParent_withoutChildSubnets()throws SubnetAlreadyExistException {
		String subnet = "192.168.101.0/30";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();

		when(subnetRepository.findByName(subnet)).thenReturn(null); // такой подсети еще нет в базе
		when(subnetRepository.getClosestParent(subnet)).thenReturn(null); // в базе нет подсети, содержащей добавляемую
		when(subnetRepository.getPossibleChildSubnets(subnet)).thenReturn(emptyList()); // в базе нет подсетей, содержащихся в добавляемой
		when(subnetRepository.getIpsThatDoesntBelongToInnerSubnets(subnet)).thenReturn(emptyList()); // в базе нет ip, которые нужно добавлять в новую подсеть

		subnetService.createIpv4Subnet(subnetDto, false);

		// ловим сеть, улетающую в репозиторий и проверяем, что все что нужно в ней появилось
		ArgumentCaptor<IPSubnet> filledSubnet = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).persist(filledSubnet.capture());
		List<String> addedIps = filledSubnet.getValue().getIpAddresses().stream()
				.map(LogicalResource::getName)
				.collect(toList());
		assertTrue(addedIps.containsAll(
				ImmutableList.of("192.168.101.0", "192.168.101.1", "192.168.101.2", "192.168.101.3")));
	}

	@Test
	public void createIPv4Subnet_withoutParent_withChildSubnets()throws SubnetAlreadyExistException {
		String subnet = "192.168.101.0/30";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();

		when(subnetRepository.findByName(subnet)).thenReturn(null); // такой подсети еще нет в базе
		when(subnetRepository.getClosestParent(subnet)).thenReturn(null); // в базе нет подсети, содержащей добавляемую
		IPSubnet childSubnet = fullSubnet("192.168.101.0/31");
		when(subnetRepository.getPossibleChildSubnets(subnet)).thenReturn(asList(childSubnet)); // в базе есть подсетка /31
		when(subnetRepository.getIpsThatDoesntBelongToInnerSubnets(subnet)).thenReturn(emptyList()); // в базе нет ip, которые нужно добавлять в новую подсеть

		subnetService.createIpv4Subnet(subnetDto, false);

		// ловим сеть, улетающую в репозиторий и проверяем, что все что нужно в ней появилось
		ArgumentCaptor<IPSubnet> filledSubnet = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).persist(filledSubnet.capture());

		assertTrue(filledSubnet.getValue().getChildSubnets().contains(childSubnet));
	}

	@Test
	public void createIPv4Subnet_withParent_withoutChuldSubnets()throws SubnetAlreadyExistException {
		String subnet = "192.168.101.0/30";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();

		when(subnetRepository.findByName(subnet)).thenReturn(null); // такой подсети еще нет в базе
		IPSubnet closestParent = fullSubnet("192.168.101.0/29");
		when(subnetRepository.getClosestParent(subnet)).thenReturn(closestParent); // в базе есть родитель /29
		when(subnetRepository.getPossibleChildSubnets(subnet)).thenReturn(emptyList()); // в базе нет подсетей, содержащихся в добавляемой
		List<IPAddress> ips = asList(ip("192.168.101.0"), ip("192.168.101.1"), ip("192.168.101.2"), ip("192.168.101.3"));
		when(subnetRepository.getIpsThatDoesntBelongToInnerSubnets(subnet)).thenReturn(ips); // в базе есть 4 ip-прямые дети нашей сетки /30

		subnetService.createIpv4Subnet(subnetDto, false);

		// ловим сеть, улетающую в репозиторий и проверяем, что все что нужно в ней появилось
		ArgumentCaptor<IPSubnet> filledSubnet = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).persist(filledSubnet.capture());

		assertEquals("192.168.101.0/29", filledSubnet.getValue().getParent().getName());
		List<String> addedIps = filledSubnet.getValue().getIpAddresses().stream()
				.map(LogicalResource::getName)
				.collect(toList());
		assertTrue(addedIps.containsAll(
				ImmutableList.of("192.168.101.0", "192.168.101.1", "192.168.101.2", "192.168.101.3")));
	}

	@Test
	public void createIPv4Subnet_withParent_withChuldSubnets()throws SubnetAlreadyExistException {
		String subnet = "192.168.101.0/30";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();

		when(subnetRepository.findByName(subnet)).thenReturn(null); // такой подсети еще нет в базе
		IPSubnet closestParent = fullSubnet("192.168.101.0/29");
		when(subnetRepository.getClosestParent(subnet)).thenReturn(closestParent); // в базе есть родитель /29
		IPSubnet childSubnet = fullSubnet("192.168.101.0/31");
		when(subnetRepository.getPossibleChildSubnets(subnet)).thenReturn(asList(childSubnet)); // в базе есть подсетка /31
		when(subnetRepository.getIpsThatDoesntBelongToInnerSubnets(subnet)).thenReturn(asList(ip("192.168.101.2"), ip("192.168.101.3"))); // в базе нет ip, которые нужно добавлять в новую подсеть

		subnetService.createIpv4Subnet(subnetDto, false);

		// ловим сеть, улетающую в репозиторий и проверяем, что все что нужно в ней появилось
		ArgumentCaptor<IPSubnet> filledSubnet = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).persist(filledSubnet.capture());

		assertEquals("192.168.101.0/29", filledSubnet.getValue().getParent().getName());
		assertEquals("192.168.101.0/31", filledSubnet.getValue().getChildSubnets().get(0).getName());
		List<String> addedIps = filledSubnet.getValue().getIpAddresses().stream()
				.map(LogicalResource::getName)
				.collect(toList());
		assertEquals(2, addedIps.size());
		assertTrue(addedIps.containsAll(ImmutableList.of("192.168.101.2", "192.168.101.3")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createIPv4Subnet_inLoopbackSubnet() throws SubnetAlreadyExistException{
		String subnet = "127.0.0.0/24";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();
		subnetService.createIpv4Subnet(subnetDto, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createIPv4Subnet_inBroadcastSubnet()throws SubnetAlreadyExistException {
		String subnet = "0.0.0.0/24";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();
		subnetService.createIpv4Subnet(subnetDto, false);
	}

	@Test(expected = SubnetAlreadyExistException.class)
	public void createIPv4Subnet_alreadyExists()throws SubnetAlreadyExistException {
		String subnet = "192.168.101.0/24";
		IPSubnetDto subnetDto = IPSubnetDto.builder().name(subnet).build();

		when(subnetRepository.findByName(subnet)).thenReturn(fullSubnet("192.168.101.0/24"));

		subnetService.createIpv4Subnet(subnetDto, false);
	}

	/**
	 * у нас есть сеть /29 и в ней две /30.
	 * мы хотим удалить одну /30 из /29, соответственно, четыре айдишника, которые там лежат, должны
	 * перекочевать в /29
	 */
	@Test
	public void deleteIPv4Subnet_withParent() {
		IPSubnet fullSubnet29 = fullSubnet29();
		IPSubnet fullSubnet30 = fullSubnet29.getChildSubnets().get(0);

		when(subnetRepository.findAndLoadFomBDOne(1L)).thenReturn(fullSubnet30);

		subnetService.deleteIPv4Subnet(1L,false);

		// ловим сеть, улетающую в репозиторий и проверяем, что все что нужно в ней появилось
		ArgumentCaptor<IPSubnet> refreshedParent = ArgumentCaptor.forClass(IPSubnet.class);
		ArgumentCaptor<IPSubnet> refreshed = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).rebaseChildrenSubnet(refreshed.capture(),refreshedParent.capture());

	}

	/**
	 * у нас есть сеть /29 , в ней - подсети, ip 192.168.101.0 имеет историю
	 * мы хотим ее удалить. т.к. верхних сетей у нас нет, должны удалиться все ее подсети и все их айпишки, кроме
	 * 192.168.101.0. Он должен улететь в архив
	 */
	@Test
	public void deleteIPv4Subnet_withoutParent_withIpArchivation() {
		IPSubnet fullSubnet29 = fullSubnet29();

		// найдем 192.168.101.0
		IPAddress ip101_0 = flatten(fullSubnet29)
				.flatMap(subnet -> subnet.getIpAddresses().stream())
				.filter(ip -> "192.168.101.0".equals(ip.getName()))
				.findFirst().orElse(null);

		// ip101_0 будет у нас иметь историю
		when(historyService.getHistory(ip101_0)).thenReturn(asList(LifecycleHistoryItem.builder().build()));
		when(subnetRepository.findAndLoadFomBDOne(1L)).thenReturn(fullSubnet29);

		subnetService.deleteIPv4Subnet(1L,true);

		// ловим удаляемую сеть
		ArgumentCaptor<IPSubnet> subnetToDelete = ArgumentCaptor.forClass(IPSubnet.class);
		verify(subnetRepository).remove(subnetToDelete.capture());

		// проверяем, что в ней есть 7 айпишек, и нет архивируемой айпишки
		List<IPAddress> ipAddressesToDelete = flatten(subnetToDelete.getValue())
				.flatMap(subnet -> subnet.getIpAddresses().stream())
				.collect(toList());
		assertEquals(7, ipAddressesToDelete.size());
		assertTrue(!ipAddressesToDelete.contains(ip101_0));

		// проверим, что наша айпишка улетела в архив
		ArgumentCaptor<IPAddress> ipToArchive = ArgumentCaptor.forClass(IPAddress.class);
		verify(ipRepository).save(ipToArchive.capture());
		assertEquals("192.168.101.0", ipToArchive.getValue().getName());
	}

	@Test(expected = IllegalStateException.class)
	public void deleteIPv4Subnet_cannotFind() {
		subnetService.deleteIPv4Subnet(2L,true);
	}

	@Test(expected = IllegalStateException.class)
	public void deleteIPv4Subnet_cannotDeleteWhenOwnedIPsExist() {
		IPSubnet fullSubnet29 = fullSubnet29();

		// найдем 192.168.101.0
		IPAddress ip101_0 = flatten(fullSubnet29)
				.flatMap(subnet -> subnet.getIpAddresses().stream())
				.filter(ip -> "192.168.101.0".equals(ip.getName()))
				.findFirst().orElse(null);
		ip101_0.setState(IPAddressState.OCCUPIED);

		// ip101_0 будет у нас иметь историю
		when(historyService.getHistory(ip101_0)).thenReturn(asList(LifecycleHistoryItem.builder().build()));
		when(subnetRepository.findOne(1L)).thenReturn(fullSubnet29);

		subnetService.deleteIPv4Subnet(1L,true);
	}

	private IPSubnet fullSubnet(String name) {
		IPSubnet subnet = new IPSubnet();
		subnet.setName(name);
		SubnetUtils subnetUtils = new SubnetUtils(name);
		subnetUtils.setInclusiveHostCount(true);

		List<IPAddress> ips = Arrays.stream(subnetUtils.getInfo().getAllAddresses())
				.map(this::ip)
				.collect(toList());
		subnet.getIpAddresses().addAll(ips);
		ips.forEach(ip -> ip.setSubnet(subnet));
		return subnet;
	}

	private IPSubnet fullSubnet29() {
		IPSubnet fullSubnet31_0 = fullSubnet("192.168.101.0/31");
		IPSubnet fullSubnet31_2 = fullSubnet("192.168.101.2/31");
		IPSubnet fullSubnet31_4 = fullSubnet("192.168.101.0/31");
		IPSubnet fullSubnet31_6 = fullSubnet("192.168.101.2/31");
		IPSubnet fullSubnet30_0 = new IPSubnet();
		IPSubnet fullSubnet30_4 = new IPSubnet();
		IPSubnet fullSubnet29 = new IPSubnet();
		fullSubnet30_0.setName("192.168.101.0/30");
		fullSubnet30_4.setName("192.168.101.4/30");
		fullSubnet29.setName("192.168.101.0/29");
		fullSubnet30_0.getChildSubnets().addAll(asList(fullSubnet31_0, fullSubnet31_2));
		fullSubnet30_4.getChildSubnets().addAll(asList(fullSubnet31_4, fullSubnet31_6));
		fullSubnet29.getChildSubnets().addAll(asList(fullSubnet30_0, fullSubnet30_4));
		Stream.of(fullSubnet31_0, fullSubnet31_2).forEach(subnet -> subnet.setParent(fullSubnet30_0));
		Stream.of(fullSubnet31_4, fullSubnet31_6).forEach(subnet -> subnet.setParent(fullSubnet30_4));
		Stream.of(fullSubnet30_0, fullSubnet30_4).forEach(subnet -> subnet.setParent(fullSubnet29));
		return fullSubnet29;
	}

	private IPAddress ip(String name) {
		return IPAddress.builder().name(name).build();
	}

	private Stream<IPSubnet> flatten(IPSubnet subnet) {
		return Stream.concat(Stream.of(subnet), subnet.getChildSubnets().stream().flatMap(this::flatten));

	}

}