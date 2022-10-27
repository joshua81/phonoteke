package org.phonoteke.batch.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {
}


//public interface ItemRepository extends MongoRepository<GroceryItem, String> {
//    
//    @Query("{name:'?0'}")
//    GroceryItem findItemByName(String name);
//    
//    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
//    List<GroceryItem> findAll(String category);
//    
//    public long count();
//
//}
