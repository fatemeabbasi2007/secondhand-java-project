package org.example.backend.repository;

import org.example.backend.model.AdStatus;
import org.example.backend.model.Advertisement;
import org.example.backend.util.JsonFileStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AdvertisementRepository {
    private final JsonFileStorage fileStorage;
    private static final String FILE_NAME = "advertisements.json";

    private final Map<String, Advertisement> cache = new ConcurrentHashMap<>();

    public AdvertisementRepository(JsonFileStorage fileStorage) {
        this.fileStorage = fileStorage;
        initializeCache();
    }

    private void initializeCache() {
        List<Advertisement> advertisements = fileStorage.readFromFile(FILE_NAME, Advertisement.class);
        if (advertisements != null) {
            for (Advertisement ad : advertisements) {
                if (ad.getId() != null) {
                    cache.put(ad.getId(), ad);
                }
            }
        }
    }

    public boolean existsById(String id) {
        if (id == null) return false;
        return cache.containsKey(id);
    }

    public Optional<Advertisement> findByID(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cache.get(id));
    }


    public List<Advertisement> findAll() {
        return new ArrayList<>(cache.values());
    }

    public List<Advertisement> findByStatus(AdStatus status) {
        if (status == null) return new ArrayList<>();
        return cache.values().stream()
                .filter(ad -> ad.getStatus() == status)
                .collect(Collectors.toList());
    }

    public synchronized void save(Advertisement newAd) {
        if (newAd == null || newAd.getId() == null) return;

        cache.put(newAd.getId(), newAd);
        flushToFile();
    }

    public synchronized void saveALL(List<Advertisement> advertisements) {
        if (advertisements == null) return;

        cache.clear();
        for (Advertisement ad : advertisements) {
            if (ad.getId() != null) {
                cache.put(ad.getId(), ad);
            }
        }
        flushToFile();
    }


    public synchronized boolean deleteById(String id) {
        if (id == null) return false;

        if (cache.remove(id) != null) {
            flushToFile();
            return true;
        }
        return false;
    }

    private void flushToFile() {
        List<Advertisement> ads = new ArrayList<>(cache.values());

        try {
            System.out.println("===== BEFORE WRITING =====");

            for (Advertisement ad : ads) {
                System.out.println(ad.getTitle());
                System.out.println(ad.getSpecificAttributes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        fileStorage.saveToFile(FILE_NAME, new ArrayList<>(cache.values()));
    }
}