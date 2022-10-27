package org.phonoteke.batch.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mongodb.client.model.Filters;

public interface DocRepository extends MongoRepository<Doc, String> {
	@Query(value="{ $and: [{'source':'?0'}, {'url':'?1'}]}")
	Doc findBySourceUrl(String source, String url);

	//	MongoCursor<org.bson.Document> i = docs.find(Filters.and(
	//			Filters.eq("type", TYPE.podcast.name()), 
	//			Filters.or(Filters.exists("tracks.youtube", false),Filters.eq("tracks.youtube", null)))).
	//			sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).iterator();
	@Query(value="{ $and: [{'type':'?0'}, $or: [{ $exists: {'tracks.youtube':false}}, {'tracks.youtube':null}]]}",
			sort = "{'date': 1}")
	List<Doc> findByType(String type);

	//	MongoCursor<Document> i = docs.find(Filters.or(
	//			Filters.and(Filters.ne("type", "podcast"), Filters.eq("spartistid", null)), 
	//			Filters.and(Filters.eq("type", "podcast"), Filters.eq("tracks.spotify", null)))).iterator();
	@Query(value="{ $or: [ "
			+ "$and: [{'type':'podcast'}, {'spartistid': null}], "
			+ "$and: [{'type':'podcast'}, {'tracks.spotify':null}]]}",
			sort = "{'date': 1}")
	List<Doc> findSpotify();
	
	// Filters.and(Filters.eq("type", "podcast"), Filters.ne("dirty", false)
	@Query(value="{ $and: [{'type':'podcast'}, {$ne: {'dirty':false}}]}")
	List<Doc> findDirtyPodcast();
}
