package org.example.backend.repository;

import org.example.backend.model.Category;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CategoryRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "categories.json";

    // کِش برای دسترسی سریع به دسته‌بندی‌ها
    private final Map<String, Category> cache = new ConcurrentHashMap<>();

    public CategoryRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initializeCache();
    }

    private void initializeCache() {
        List<Category> categories = fileStorage.readFromFile(FILE_NAME, Category.class);
        if (categories != null) {
            for (Category category : categories) {
                if (category.getId() != null) {
                    cache.put(category.getId(), category);
                }
            }
        }
    }

    public List<Category> findAll() {
        return new ArrayList<>(cache.values());
    }

    public Optional<Category> findByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cache.get(id));
    }

    public synchronized void save(Category category) {
        if (category == null || category.getId() == null) return;

        cache.put(category.getId(), category);
        flushToFile();
    }

    public synchronized void saveALL(List<Category> categories) {
        if (categories == null) return;

        cache.clear();
        for (Category category : categories) {
            if (category.getId() != null) {
                cache.put(category.getId(), category);
            }
        }
        flushToFile();
    }

    private void flushToFile() {
        fileStorage.saveToFile(FILE_NAME, new ArrayList<>(cache.values()));
    }
}