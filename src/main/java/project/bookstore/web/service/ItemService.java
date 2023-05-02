package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import project.bookstore.domain.exception.ItemException;
import project.bookstore.domain.item.Item;
import project.bookstore.domain.item.ItemSaveForm;
import project.bookstore.domain.item.ItemSearchCond;
import project.bookstore.domain.item.ItemUpdateForm;
import project.bookstore.repository.ItemRepository;

import javax.sql.DataSource;
import java.util.List;

@Transactional
@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final PlatformTransactionManager transactionManager;

    public ItemService(ItemRepository itemRepository, DataSource dataSource) {
        this.itemRepository = itemRepository;
        this.transactionManager = new DataSourceTransactionManager(dataSource);
    }

    public Item addItem(Item item) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("transaction ={}", TransactionSynchronizationManager.isActualTransactionActive());

        try{
            Item savedItem = itemRepository.addItem(item);
            transactionManager.commit(status);
            return savedItem;
        }catch(DataAccessException e){
            transactionManager.rollback(status);
            throw e;
        }
    }
    @Transactional(readOnly = true)
    public Item findItem(Long id) {
        Item findItem = itemRepository.findById(id).get();

        return findItem;
    }

    public void update(Long id, ItemUpdateForm form) {
        itemRepository.updateItem(id, form);
    }

    @Transactional(readOnly = true)
    public List<Item> findAll(ItemSearchCond cond) {
        List<Item> items = itemRepository.findAll(cond);
        return items;
    }

    public void delete(Long id) {
        itemRepository.delete(id);

    }
}
