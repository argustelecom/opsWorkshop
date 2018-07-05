package ru.argustelecom.box.env.order.testdata;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.order.OrderRepository;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

public class OrderTestDataUtils implements Serializable {

    private static final long serialVersionUID = 603398000813047619L;

    @Inject
    private OrderRepository orderRepository;

    public Order findOrCreateTestOrder(Employee assignee, Customer customer, Location location) {

        List<Order> allCustomerOrders = orderRepository.findOrders(customer);
        if (!allCustomerOrders.isEmpty()) {
            return allCustomerOrders.get(0);
        }

        return orderRepository.createOrder(assignee, customer, location, "");
    }
}
