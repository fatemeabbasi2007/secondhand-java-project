package org.example.backend.repository;

import org.example.backend.model.User;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "users.json";

    private final Map<String, User> cache = new ConcurrentHashMap<>();

    public UserRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initializeCache();
    }

    private void initializeCache() {
        List<User> users = fileStorage.readFromFile(FILE_NAME, User.class);
        if (users != null) {
            for (User user : users) {
                if (user.getId() != null) {
                    cache.put(user.getId(), user);
                }
            }
        }
    }

    public boolean existsById(String id) {
        if (id == null) return false;
        return cache.containsKey(id);
    }

    public Optional<User> findByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cache.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(cache.values());
    }

    public synchronized void save(User newUser) {
        if (newUser == null || newUser.getId() == null) return;

        cache.put(newUser.getId(), newUser);

        flushToFile();
    }

    public synchronized void saveAll(List<User> users) {
        if (users == null) return;

        cache.clear();
        for (User user : users) {
            if (user.getId() != null) {
                cache.put(user.getId(), user);
            }
        }
        flushToFile();
    }

    private void flushToFile() {
        fileStorage.saveToFile(FILE_NAME, new ArrayList<>(cache.values()));
    }
}