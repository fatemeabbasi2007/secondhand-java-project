package org.example.backend.repository;

import org.example.backend.model.Conversation;
import org.example.backend.model.Review;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Repository
public class ReviewRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "reviews.json";
    private final Map<String , Review> cache = new ConcurrentHashMap<>();

    public ReviewRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initializeCache();
    }
    public List<Review> findBySellerId(String sellerId) {
        if (sellerId == null) return new ArrayList<>();

        return cache.values().stream()
                .filter(review -> sellerId.equals(review.getSellerId()))
                .collect(Collectors.toList());
    }
    private void initializeCache() {
        List<Review> reviews = fileStorage.readFromFile(FILE_NAME, Review.class);
        if (reviews != null) {
            for (Review review : reviews) {
                if (review.getId() != null) {
                    cache.put(review.getId(), review);
                }
            }
        }
    }
    public boolean existsById(String id){
        if ( id == null ){
            return false;
        }
        return cache.containsKey(id);
    }
    public Optional<Review> findByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cache.get(id));
    }
    public List<Review> findAll() {
        return new ArrayList<>(cache.values());
    }
    public synchronized void saveALL(List<Review> reviews) {
        if (reviews == null) return;

        cache.clear();
        for (Review r : reviews) {
            if (r.getId() != null) {
                cache.put(r.getId(), r);
            }
        }
        flushToFile();
    }


    public synchronized void save(Review newReview) {
        if (newReview == null || newReview.getId() == null) return;

        cache.put(newReview.getId(), newReview);

        flushToFile();
    }
    private void flushToFile() {
        fileStorage.saveToFile(FILE_NAME, new ArrayList<>(cache.values()));
    }

}
