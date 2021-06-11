package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import lombok.Data;

@Data
public class BookForm extends ItemForm {

    private String author;
    private String isbn;

    public static BookForm createBookForm(Book book) {

        BookForm bookForm = new BookForm();
        bookForm.setId(book.getId());
        bookForm.setName(book.getName());
        bookForm.setPrice(book.getPrice());
        bookForm.setStockQuantity(book.getStockQuantity());
        bookForm.setAuthor(book.getAuthor());
        bookForm.setIsbn(book.getIsbn());

        return bookForm;
    }
}
