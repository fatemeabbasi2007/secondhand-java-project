package org.example.backend.repository;

import org.example.backend.model.AttributeRule;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AttributeRulesRepository {

    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "attributes.json";

    private final Map<String, AttributeRule> cache = new ConcurrentHashMap<>();

    public AttributeRulesRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initializeCache();
    }

    private void initializeCache() {
        List<AttributeRule> attributeRules = fileStorage.readFromFile(FILE_NAME, AttributeRule.class);
        if (attributeRules != null) {
            for (AttributeRule rule : attributeRules) {
                if (rule.getId() != null) {
                    cache.put(rule.getId(), rule);
                }
            }
        }
    }

    public boolean existsById(String id) {
        if (id == null) return false;
        return cache.containsKey(id);
    }

    public Optional<AttributeRule> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cache.get(id));
    }

    public List<AttributeRule> findAll() {
        return new ArrayList<>(cache.values());
    }

    public List<AttributeRule> findByCategoryId(String categoryId) {
        if (categoryId == null) return new ArrayList<>();
        return cache.values().stream()
                .filter(rule -> categoryId.equals(rule.getCategoryId()))
                .collect(Collectors.toList());
    }

    public synchronized void save(AttributeRule attributeRule) {
        if (attributeRule == null || attributeRule.getId() == null) return;

        cache.put(attributeRule.getId(), attributeRule);

        flushToFile();
    }

    public synchronized void saveAll(List<AttributeRule> attributeRules) {
        if (attributeRules == null) return;

        cache.clear();
        for (AttributeRule rule : attributeRules) {
            if (rule.getId() != null) {
                cache.put(rule.getId(), rule);
            }
        }
        flushToFile();
    }

    private void flushToFile() {
        fileStorage.saveToFile(FILE_NAME, new ArrayList<>(cache.values()));
    }
}