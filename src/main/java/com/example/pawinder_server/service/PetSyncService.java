package com.example.pawinder_server.service;

import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.entity_classes.PetType;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.repository_interfaces.PetTypeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PetSyncService {

    @Value("${catapi.key}")
    private String catApiKey;

    @Value("${dogapi.key}")
    private String dogApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetTypeRepository petTypeRepository;

    public void syncCats(int count) {
        syncGeneric("https://api.thecatapi.com/v1/images/search?limit=" + count + "&has_breeds=1",
                catApiKey, "Кошка");
    }

    public void syncDogs(int count) {
        syncGeneric("https://api.thedogapi.com/v1/images/search?limit=" + count + "&has_breeds=1",
                dogApiKey, "Собака");
    }

    private void syncGeneric(String url, String apiKey, String typeName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CatDogApiItem[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, CatDogApiItem[].class);

        PetType type = petTypeRepository.findByTypeName(typeName)
                .orElseThrow(() -> new RuntimeException("Тип '" + typeName + "' не найден в справочнике type"));

        for (CatDogApiItem item : response.getBody()) {
            if (petRepository.existsByApiPetIdAndType_Id(item.getId(), type.getId())) {
                continue;
            }
            Pet pet = new Pet();
            pet.setApiPetId(item.getId());
            pet.setType(type);
            pet.setPhotoUrl(item.getUrl());
            if (item.getBreeds() != null && !item.getBreeds().isEmpty()) {
                pet.setName(item.getBreeds().get(0).getName());
                pet.setDescription(item.getBreeds().get(0).getTemperament());
            } else {
                pet.setName(typeName.equals("Кошка") ? "Кот без породы" : "Пёс без породы");
            }
            petRepository.save(pet);
        }
    }
}
