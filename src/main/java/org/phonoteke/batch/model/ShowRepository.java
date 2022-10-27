package org.phonoteke.batch.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ShowRepository extends MongoRepository<Show, String> {
	@Query("{type:'?0'}")
	List<Show> findByType(String type);
	
	@Query("{ $and: [{type:'?0'}, {source:'?1'}]}")
	List<Show> findByTypeSource(String type, String source);
}
