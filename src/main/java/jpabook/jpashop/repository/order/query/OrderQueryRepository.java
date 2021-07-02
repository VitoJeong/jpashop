package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDTO> findOrderQueryDTOs() {

        List<OrderQueryDTO> result = findOrders(); // query 1 -> N개(N+1)

        result.forEach(o -> {
            List<OrderItemQueryDTO> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    public List<OrderItemQueryDTO> findOrderItems(Long orderId) {
        return em.createQuery(
                "SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "FROM OrderItem oi " +
                        "JOIN oi.item i " +
                        "WHERE oi.order.id = :orderId", OrderItemQueryDTO.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDTO> findOrders() {
        return em.createQuery(
                "SELECT new jpabook.jpashop.repository.order.query.OrderQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "FROM Order o " +
                        "JOIN o.member m " +
                        "JOIN o.delivery d", OrderQueryDTO.class)
                .getResultList();
    }


    public List<OrderQueryDTO> findAllByDTOsOptimization() {
        List<OrderQueryDTO> orders = findOrders();

        // 1. orderIds를 통해 한번에 toMany(orderItems)를 조회
        // 2. Map을 활용해 성능향상 O(1)
        Map<Long, List<OrderItemQueryDTO>> orderItemMap = findOrderItemMap(toOrderIds(orders));

        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orders;
    }

    private List<Long> toOrderIds(List<OrderQueryDTO> orders) {
        List<Long> orderIds = orders.stream()
                .map(OrderQueryDTO::getOrderId)
                .collect(Collectors.toList());
        return orderIds;
    }

    private Map<Long, List<OrderItemQueryDTO>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDTO> orderItems = em.createQuery("SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                "FROM OrderItem oi " +
                "JOIN oi.item i " +
                "WHERE oi.order.id in :orderIds", OrderItemQueryDTO.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDTO>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDTO::getOrderId));
        return orderItemMap;
    }

    public List<OrderFlatDto> findAllByDTOsFlat() {
        return em.createQuery("SELECT " +
                "new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                "FROM Order o " +
                "JOIN o.member m " +
                "JOIN o.delivery d " +
                "JOIN o.orderItems oi " +
                "JOIN oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
