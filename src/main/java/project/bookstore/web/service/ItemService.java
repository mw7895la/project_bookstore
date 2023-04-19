package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import project.bookstore.domain.item.Item;
import project.bookstore.domain.item.ItemSaveForm;
import project.bookstore.repository.ItemRepository;

import javax.sql.DataSource;

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
        }catch(Exception e){
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    public Item findItem(Long id) {
        Item findItem = itemRepository.findById(id).get();

        return findItem;
    }


}
