package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        if(orderSearch.getMemberName() != null && !orderSearch.getMemberName().isEmpty()) orderSearch.setMemberName("%" + orderSearch.getMemberName() + "%");
        else orderSearch.setMemberName(null);
        return em.createQuery("SELECT o FROM Order o JOIN o.member m " +
                "WHERE (:status IS NULL OR o.status = :status)" +
                "   AND (:name IS NULL OR m.name LIKE :name)", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name",orderSearch.getMemberName())
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        // FETCH JOIN
        return em.createQuery("SELECT o FROM Order o " +
                "JOIN FETCH o.member m " +
                "JOIN FETCH o.delivery d", Order.class).getResultList();
    }

}
