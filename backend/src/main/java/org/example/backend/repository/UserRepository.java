package org.example.backend.repository;

import org.example.backend.model.User;
import org.example.backend.util.JsonFileStorage;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "users.json";
    List<String>  list;


    public UserRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;

    }
    /// ////////////////////////////////////////////
    public List<User> findAll(){
        return fileStorage.readFromFile(FILE_NAME , User.class);
    }
    public void saveAll(List<User> users){
        fileStorage.saveToFile(FILE_NAME , users);
    }
    public Optional<User> findByID(String id){
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    public void save(User newUser ){
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(newUser.getId()));
        users.add(newUser);
        saveAll(users);
    }
}
