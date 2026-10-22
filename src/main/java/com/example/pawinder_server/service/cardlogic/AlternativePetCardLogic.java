package com.example.pawinder_server.service.cardlogic;

import com.example.pawinder_server.service.BreedInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Альтернативная реализация тех же операций: через Optional и потоки. */
public class AlternativePetCardLogic implements PetCardLogicContract {

    @Override
    public String resolveName(List<BreedInfo> breeds, String typeName) {
        String fallback = "Кошка".equals(typeName) ? DEFAULT_CAT_NAME : DEFAULT_DOG_NAME;
        return firstBreed(breeds)
                .map(BreedInfo::getName)
                .filter(name -> !name.isBlank())
                .map(String::trim)
                .orElse(fallback);
    }

    @Override
    public String resolveDescription(List<BreedInfo> breeds) {
        return firstBreed(breeds)
                .map(BreedInfo::getTemperament)
                .orElse(null);
    }

    private static Optional<BreedInfo> firstBreed(List<BreedInfo> breeds) {
        if (breeds == null) {
            return Optional.empty();
        }
        return breeds.stream().limit(1).filter(Objects::nonNull).findFirst();
    }
}
