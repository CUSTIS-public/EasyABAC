package custis.easyabac.demo.service;

import custis.easyabac.demo.authz.DemoPermissionChecker;
import custis.easyabac.demo.model.*;
import custis.easyabac.demo.repository.BranchRepository;
import custis.easyabac.demo.repository.CustomerRepository;
import custis.easyabac.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DemoPermissionChecker demoPermissionChecker;

    @Override
    @Transactional
    public void createOrder(CustomerId customerId, BranchId branchId, BigDecimal amount) {
        // 1. Загружаем данные клиента и филиала по ID
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new BranchNotFoundException(branchId));

        // 2. Готовим объект "Заказ"
        Order order = new Order(customer, branch, amount);

        // 3. Проверяем права на создание
        demoPermissionChecker.canCreate(order);

        // 4. Сохраняем заказ
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order getOrder(OrderId id) {
        // 1. Получаем данные заказа
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        // 2. Проверяем, что пользователь может смотреть заказ
        demoPermissionChecker.сanView(order);

        // 3. Возвращаем данные заказа
        return order;
    }

    @Override
    @Transactional
    public void approveOrder(OrderId id) {
        // 1. Загружаем данные заказа
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        // 2. Проверяем права на создание
        demoPermissionChecker.canApprove(order);

        // 3. Обновляем статус заказа
        order.approve();
    }

    @Override
    @Transactional
    public void rejectOrder(OrderId id) {
        // 1. Загружаем данные заказа
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        // 2. Проверяем права на создание
        demoPermissionChecker.checkReject(order);

        // 3. Обновляем статус заказа
        order.reject();
    }
}
