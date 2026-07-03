package org.example.backend.repository;

import org.example.backend.model.Conversation;
import org.example.backend.model.Review;
import org.example.backend.util.JsonFileStorage;

import java.util.List;
import java.util.Optional;

public class ReviewRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "advertisements.json";

    public ReviewRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
    public List<Review> findAll(){
        return fileStorage.readFromFile(FILE_NAME , Review.class);

    }
    public void saveALL(List<Review> reviews){
        fileStorage.saveToFile(FILE_NAME , reviews);
    }
    public Optional<Review> findByID(String id ){
        return findAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }
    public void save(Review newAd){
        List<Review> reviews = findAll();
        reviews.removeIf(ad -> ad.getId().equals(newAd.getId()));
        reviews.add(newAd);
        saveALL(reviews);
    }

}
