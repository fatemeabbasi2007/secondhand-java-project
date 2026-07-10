package org.example.backend.repository;

import org.example.backend.model.Advertisement;
import org.example.backend.model.Category;
import org.example.backend.model.Conversation;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Repository
public class ConversationRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "conversations.json";
    private final Map<String, Conversation> conversationCache = new ConcurrentHashMap<>();

    public ConversationRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initCache();
    }
    public boolean existsById(String id) {
        if (id == null) return false;
        return conversationCache.containsKey(id);
    }
    private synchronized void initCache(){
        List<Conversation> list = fileStorage.readFromFile(FILE_NAME, Conversation.class);
        if ( list != null){
            for (Conversation conv : list){
                conversationCache.put(conv.getId() , conv);
            }
        }
    }
    public List<Conversation> findAll(){
        return new ArrayList<>(conversationCache.values());
    }
    public void saveALL(List<Conversation> conversations){
        fileStorage.saveToFile(FILE_NAME , conversations);
    }
    public Optional<Conversation> findByID(String id ){
        return Optional.ofNullable(conversationCache.get(id));
    }
    public List<Conversation> findConversationsByUserId(String userId) {
        return conversationCache.values().stream()
                .filter(c -> userId.equals(c.getBuyerId()) || userId.equals(c.getSellerId()))
                .collect(Collectors.toList());
    }
    public synchronized void save(Conversation conversation){
        conversationCache.put(conversation.getId(), conversation);
        fileStorage.saveToFile(FILE_NAME , new ArrayList<>(conversationCache.values()));
    }

}
