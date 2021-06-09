package jpabook.jpashop.controller;

import lombok.Data;

@Data
public class BookForm extends ItemForm {

    private String author;
    private String isbn;

}
