package org.example.backend.repository;

import org.example.backend.model.Advertisement;
import org.example.backend.util.JsonFileStorage;

import java.util.List;
import java.util.Optional;

public class AdvertisementRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "advertisements.json";

    public AdvertisementRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
    public List<Advertisement> findAll(){
        return fileStorage.readFromFile(FILE_NAME , Advertisement.class);

    }
    public void saveALL(List<Advertisement> advertisements){
        fileStorage.saveToFile(FILE_NAME , advertisements);
    }
    public Optional<Advertisement> findByID(String id ){
        return findAll().stream().filter(ad -> ad.getId().equals(id)).findFirst();
    }
    public void save(Advertisement newAd){
        List<Advertisement> advertisments = findAll();
        advertisments.removeIf(ad -> ad.getId().equals(newAd.getId()));
        advertisments.add(newAd);
        saveALL(advertisments);
    }

}
