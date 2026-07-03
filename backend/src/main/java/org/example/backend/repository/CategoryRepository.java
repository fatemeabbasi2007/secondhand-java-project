package org.example.backend.repository;

import org.example.backend.model.Advertisement;
import org.example.backend.model.Category;
import org.example.backend.util.JsonFileStorage;

import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "advertisements.json";

    public CategoryRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
    public List<Category> findAll(){
        return fileStorage.readFromFile(FILE_NAME , Category.class);

    }
    public void saveALL(List<Category> categories){
        fileStorage.saveToFile(FILE_NAME , categories);
    }
    public Optional<Category> findByID(String id ){
        return findAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }
    public void save(Category newAd){
        List<Category> categories = findAll();
        categories.removeIf(ad -> ad.getId().equals(newAd.getId()));
        categories.add(newAd);
        saveALL(categories);
    }

}
