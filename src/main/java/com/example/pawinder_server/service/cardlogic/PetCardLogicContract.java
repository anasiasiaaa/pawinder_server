package com.example.pawinder_server.service.cardlogic;

import com.example.pawinder_server.service.BreedInfo;

import java.util.List;

/**
 * Модуль PetCardLogic: правила подготовки данных карточки животного,
 * полученных от TheCatAPI / TheDogAPI, к сохранению в таблицу {@code pet}.
 *
 * <p>Назначение: отделить правила выбора названия и описания карточки от
 * HTTP-запросов, разбора JSON и работы с репозиториями (они остаются
 * в {@code PetSyncService}).
 *
 * <p>Общие гарантии контракта:
 * <ul>
 *   <li>входные списки и объекты не изменяются;</li>
 *   <li>исключения не выбрасываются ни при каких входных значениях;</li>
 *   <li>побочных эффектов нет: нет сетевых запросов, обращений к базе
 *       данных и вывода в журнал;</li>
 *   <li>результаты - неизменяемые строки (или {@code null}, где это указано).</li>
 * </ul>
 *
 * <p>Пример использования:
 * <pre>{@code
 * PetCardLogicContract logic = PetCardLogicProvider.get();
 * pet.setName(logic.resolveName(item.getBreeds(), "Кошка"));
 * pet.setDescription(logic.resolveDescription(item.getBreeds()));
 * }</pre>
 */
public interface PetCardLogicContract {

    /** Название карточки для кошки без указанной породы. */
    String DEFAULT_CAT_NAME = "Кот без породы";

    /** Название карточки для собаки без указанной породы. */
    String DEFAULT_DOG_NAME = "Пёс без породы";

    /**
     * Определяет название карточки.
     *
     * <p>Порядок проверки: (1) список пород равен {@code null} или пуст;
     * (2) первая порода равна {@code null}; (3) название первой породы
     * равно {@code null} или состоит из пробелов. При нарушении любого из
     * пунктов возвращается название по умолчанию. Иначе возвращается
     * название первой породы без пробелов по краям; остальные породы
     * не учитываются.
     *
     * @param breeds   породы из ответа API; может быть {@code null} или пустым
     * @param typeName название типа животного; {@code "Кошка"} даёт
     *                 {@link #DEFAULT_CAT_NAME}, любое другое значение
     *                 (в том числе {@code null}) - {@link #DEFAULT_DOG_NAME}
     * @return название карточки; никогда не {@code null} и не пустая строка
     */
    String resolveName(List<BreedInfo> breeds, String typeName);

    /**
     * Определяет описание карточки.
     *
     * <p>Описанием служит темперамент первой породы. Если список пород
     * {@code null} или пуст, либо первая порода равна {@code null},
     * возвращается {@code null}. Темперамент возвращается как есть
     * (может быть {@code null}, если API его не передал).
     *
     * @param breeds породы из ответа API; может быть {@code null} или пустым
     * @return описание карточки или {@code null}
     */
    String resolveDescription(List<BreedInfo> breeds);
}
