package org.example.backend.repository;

import org.example.backend.model.Advertisement;
import org.example.backend.model.Category;
import org.example.backend.model.Conversation;
import org.example.backend.util.JsonFileStorage;

import java.util.List;
import java.util.Optional;

public class ConversationRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "advertisements.json";

    public ConversationRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
    public List<Conversation> findAll(){
        return fileStorage.readFromFile(FILE_NAME , Conversation.class);

    }
    public void saveALL(List<Conversation> conversations){
        fileStorage.saveToFile(FILE_NAME , conversations);
    }
    public Optional<Conversation> findByID(String id ){
        return findAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }
    public void save(Conversation newAd){
        List<Conversation> conversations = findAll();
        conversations.removeIf(ad -> ad.getId().equals(newAd.getId()));
        conversations.add(newAd);
        saveALL(conversations);
    }

}
