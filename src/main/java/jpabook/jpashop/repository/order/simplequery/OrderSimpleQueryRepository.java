package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    // 특정 api에 종속적인 repository를 분리
    private final EntityManager em;

    public List<OrderSimpleQueryDTO> findOrderDtos() {
        return em.createQuery("SELECT new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                "FROM Order o " +
                "JOIN o.member m " +
                "JOIN o.delivery d", OrderSimpleQueryDTO.class).getResultList();
    }
}
