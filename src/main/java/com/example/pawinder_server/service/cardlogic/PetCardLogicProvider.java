package com.example.pawinder_server.service.cardlogic;

/**
 * Точка сборки модуля PetCardLogic: единственное место, где выбирается
 * конкретная реализация контракта. Потребители получают реализацию
 * только через {@link #get()}.
 */
public final class PetCardLogicProvider {

    private static final PetCardLogicContract DEFAULT = new DefaultPetCardLogic();

    private PetCardLogicProvider() {
    }

    /** Возвращает реализацию по умолчанию ({@link DefaultPetCardLogic}). */
    public static PetCardLogicContract get() {
        return DEFAULT;
    }
}
