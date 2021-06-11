package jpabook.jpashop.domain.item;

import jpabook.jpashop.controller.ItemForm;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * ==== 비즈니스 로직 =====
     * 데이터를 가지고있는 객체에서 비즈니스 메서드를 만드는게 좋다
     * 객체지향적, 응집도향상
     * */

    // 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

//    감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("Stock is not enough!");
        }
        this.stockQuantity = restStock;
    }

    public Item updateItem(ItemForm form) {
        this.setName(form.getName());
        this.setPrice(form.getPrice());
        this.setStockQuantity(form.getStockQuantity());

        return this;
    }

}
