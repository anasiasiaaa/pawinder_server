package com.example.pawinder_server.service.cardlogic;

import com.example.pawinder_server.service.BreedInfo;

import java.util.List;

/** Основная реализация контракта: прямой доступ к первому элементу списка. */
public class DefaultPetCardLogic implements PetCardLogicContract {

    @Override
    public String resolveName(List<BreedInfo> breeds, String typeName) {
        String fallback = "Кошка".equals(typeName) ? DEFAULT_CAT_NAME : DEFAULT_DOG_NAME;
        if (breeds == null || breeds.isEmpty()) {
            return fallback;
        }
        BreedInfo first = breeds.get(0);
        if (first == null || first.getName() == null || first.getName().isBlank()) {
            return fallback;
        }
        return first.getName().trim();
    }

    @Override
    public String resolveDescription(List<BreedInfo> breeds) {
        if (breeds == null || breeds.isEmpty()) {
            return null;
        }
        BreedInfo first = breeds.get(0);
        return first == null ? null : first.getTemperament();
    }
}
