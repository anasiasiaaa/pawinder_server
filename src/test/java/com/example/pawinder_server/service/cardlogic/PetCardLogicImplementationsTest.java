package com.example.pawinder_server.service.cardlogic;

import com.example.pawinder_server.service.BreedInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Один набор проверок контракта PetCardLogicContract для обеих реализаций. */
class PetCardLogicImplementationsTest {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    static Stream<PetCardLogicContract> implementations() {
        return Stream.of(new DefaultPetCardLogic(), new AlternativePetCardLogic());
    }

    private static BreedInfo breed(String name, String temperament) {
        StringBuilder json = new StringBuilder("{");
        if (name != null) {
            json.append("\"name\":\"").append(name).append("\"");
        }
        if (temperament != null) {
            if (name != null) json.append(",");
            json.append("\"temperament\":\"").append(temperament).append("\"");
        }
        return MAPPER.readValue(json.append("}").toString(), BreedInfo.class);
    }

    // штатный случай: берётся первая порода
    @ParameterizedTest
    @MethodSource("implementations")
    void firstBreedIsUsed(PetCardLogicContract logic) {
        List<BreedInfo> breeds = List.of(breed("Abyssinian", "Active"), breed("Aegean", "Calm"));
        assertEquals("Abyssinian", logic.resolveName(breeds, "Кошка"));
        assertEquals("Active", logic.resolveDescription(breeds));
    }

    // граничное значение: пробелы по краям названия
    @ParameterizedTest
    @MethodSource("implementations")
    void nameIsTrimmed(PetCardLogicContract logic) {
        assertEquals("Beagle", logic.resolveName(List.of(breed("  Beagle ", "Merry")), "Собака"));
    }

    // граничное значение: пустой список и null вместо списка
    @ParameterizedTest
    @MethodSource("implementations")
    void emptyOrMissingBreedsGiveDefaults(PetCardLogicContract logic) {
        assertEquals("Кот без породы", logic.resolveName(null, "Кошка"));
        assertEquals("Пёс без породы", logic.resolveName(new ArrayList<>(), "Собака"));
        assertNull(logic.resolveDescription(null));
        assertNull(logic.resolveDescription(new ArrayList<>()));
    }

    // ошибочный вход: первая порода null, название отсутствует или из пробелов
    @ParameterizedTest
    @MethodSource("implementations")
    void malformedBreedGivesDefaultName(PetCardLogicContract logic) {
        List<BreedInfo> nullFirst = new ArrayList<>();
        nullFirst.add(null);
        assertEquals("Кот без породы", logic.resolveName(nullFirst, "Кошка"));
        assertNull(logic.resolveDescription(nullFirst));
        assertEquals("Пёс без породы", logic.resolveName(List.of(breed(null, "Calm")), "Собака"));
        assertEquals("Пёс без породы", logic.resolveName(List.of(breed("   ", "Calm")), "Собака"));
    }

    // ошибочный вход: неизвестный или отсутствующий тип животного
    @ParameterizedTest
    @MethodSource("implementations")
    void unknownTypeNameGivesDogDefault(PetCardLogicContract logic) {
        assertEquals("Пёс без породы", logic.resolveName(null, null));
        assertEquals("Пёс без породы", logic.resolveName(null, "Хомяк"));
    }

    // темперамент не задан API - возвращается null, исключения нет
    @ParameterizedTest
    @MethodSource("implementations")
    void missingTemperamentGivesNull(PetCardLogicContract logic) {
        assertNull(logic.resolveDescription(List.of(breed("Beagle", null))));
    }

    // вход не изменяется
    @ParameterizedTest
    @MethodSource("implementations")
    void inputIsNotModified(PetCardLogicContract logic) {
        List<BreedInfo> breeds = new ArrayList<>(List.of(breed("A", "x"), breed("B", "y")));
        List<BreedInfo> copy = new ArrayList<>(breeds);
        logic.resolveName(breeds, "Кошка");
        logic.resolveDescription(breeds);
        assertEquals(copy, breeds);
    }
}
