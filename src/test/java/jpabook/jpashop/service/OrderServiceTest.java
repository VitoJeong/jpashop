package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    private EntityManager em;
    private OrderService orderService;
    private OrderRepository orderRepository;

    @Autowired
    public OrderServiceTest(EntityManager em, OrderService orderService, OrderRepository orderRepository) {
        this.em = em;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @Test
    @DisplayName("상품주문")
    void order() throws Exception {

        // GIVEN
        Member member = createMember();

        Book book = createBook("Hello JPA", 10000, 10);

        int orderCount = 2;

        // WHEN
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // THEN
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "ORDER Status");
        assertEquals(1, getOrder.getOrderItems().size(), "Number of kinds");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "Total price");
        assertEquals(8, book.getStockQuantity(), "Remaining stocks");

    }


    @Test
    @DisplayName("상품주문_재고수량 초과")
    void orderWithOverStocks() throws Exception {

        // GIVEN
        Member member = createMember();
        Book book = createBook("Hello JPA", 10000, 10);

        int orderCount = 20;

        // WHEN, THEN
        Assert.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));

    }

    @Test
    @DisplayName("주문취소")
    void cancelOrder() throws Exception {

        // GIVEN
        Member member = createMember();
        Book book = createBook("Hello JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // WHEN
        orderService.cancelOrder(orderId);

        // THEN
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "ORDER Status");
        assertEquals(10, book.getStockQuantity(), "Remaining stocks");

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "아차산로", "12323"));
        em.persist(member);
        return member;
    }
}