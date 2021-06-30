package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
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

    public List<Order> findAllWithItem() {
        // DISTINCT -> 엔티티의 식별자가 같다면 중복을 제거, 쿼리에도 키워드 생성
        return em.createQuery("SELECT DISTINCT o FROM Order o " +
                "JOIN FETCH o.member m " +
                "JOIN FETCH o.delivery d " +
                "JOIN FETCH o.orderItems oi " +
                "JOIN FETCH oi.item i", Order.class).getResultList();
        
        // 컬렉션 페치 조인 -> 페이징 X(메모리에서 페이징 - 위험), 1개만 사용(뻥튀기문제)
    }
}
