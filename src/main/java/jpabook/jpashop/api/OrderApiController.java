package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 강제 초기화 -> Hibernate5Module 프록시 객체를 초기화해서 노출되게 만듬
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                .map(OrderDTO::new)
                .collect(toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDTO> ordersV3() {
        // 단순 fetch join -> 데이터 뻥튀기
        return orderRepository.findAllWithItem().stream()
                .map(OrderDTO::new)
                .collect(toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDTO> ordersV3_page
    (
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit
    )
    {
        // ToOne 엔티티 모두 fetch join - 쿼리수를 줄임
        return orderRepository.findAllWithMemberDelivery(offset, limit).stream()
                .map(OrderDTO::new)
                .collect(toList());

        // 컬렉션 fetch join 페이징 조회
        // default_batch_fetch_size 옵션을 통해 최적화 (in 쿼리 사용)
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDTO> ordersV4() {
        return orderQueryRepository.findOrderQueryDTOs();
    }

    @Data
    static class OrderDTO {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems;

        public OrderDTO(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(toList());
        }

    }

    @Data
    static class OrderItemDTO {

        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDTO(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
