package project.bookstore.repository;

import project.bookstore.domain.item.Item;
import project.bookstore.domain.item.ItemSaveForm;
import project.bookstore.domain.item.ItemSearchCond;
import project.bookstore.domain.item.ItemUpdateForm;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(Item item);

    void updateItem(Long id, ItemUpdateForm itemUpdateForm);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond cond);

    void delete(Long id);
}
