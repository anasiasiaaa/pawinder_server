package com.example.pawinder_server.service;

import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.entity_classes.PetType;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.repository_interfaces.PetTypeRepository;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetSyncServiceTest {

    @Mock private RestTemplate restTemplate;
    @Mock private PetRepository petRepository;
    @Mock private PetTypeRepository petTypeRepository;
    @InjectMocks private PetSyncService service;

    private final JsonMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private PetType catType;
    private PetType dogType;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "catApiKey", "test-cat-key");
        ReflectionTestUtils.setField(service, "dogApiKey", "test-dog-key");
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        catType = mock(PetType.class);
        dogType = mock(PetType.class);
        lenient().when(petTypeRepository.findByTypeName("Кошка")).thenReturn(Optional.of(catType));
        lenient().when(petTypeRepository.findByTypeName("Собака")).thenReturn(Optional.of(dogType));
    }

    private void apiReturns(String json) throws Exception {
        CatDogApiItem[] items = mapper.readValue(json, CatDogApiItem[].class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(CatDogApiItem[].class))).thenReturn(ResponseEntity.ok(items));
    }

    private Pet savedPet() {
        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());
        return captor.getValue();
    }

    // T1: П1, П3 - новая запись с двумя породами, берётся первая
    @Test
    void t1_newCatWithBreeds_savedWithFirstBreed() throws Exception {
        apiReturns("[{\"id\":\"abc-1\",\"url\":\"http://img/1.jpg\",\"breeds\":["
                + "{\"name\":\"Abyssinian\",\"temperament\":\"Active, Energetic\"},"
                + "{\"name\":\"Aegean\",\"temperament\":\"Affectionate\"}]}]");

        service.syncCats(1);

        Pet pet = savedPet();
        assertEquals("abc-1", pet.getApiPetId());
        assertEquals("http://img/1.jpg", pet.getPhotoUrl());
        assertSame(catType, pet.getType());
        assertEquals("Abyssinian", pet.getName());
        assertEquals("Active, Energetic", pet.getDescription());
    }

    // T2: П4 - breeds отсутствует (null), кошка
    @Test
    void t2_catWithoutBreedsField_getsDefaultName() throws Exception {
        apiReturns("[{\"id\":\"abc-2\",\"url\":\"http://img/2.jpg\"}]");

        service.syncCats(1);

        Pet pet = savedPet();
        assertEquals("Кот без породы", pet.getName());
        assertNull(pet.getDescription());
    }

    // T3: П4 - пустой список пород, собака
    @Test
    void t3_dogWithEmptyBreedsList_getsDefaultName() throws Exception {
        apiReturns("[{\"id\":\"dog-1\",\"url\":\"http://img/3.jpg\",\"breeds\":[]}]");

        service.syncDogs(1);

        Pet pet = savedPet();
        assertEquals("Пёс без породы", pet.getName());
        assertNull(pet.getDescription());
        assertSame(dogType, pet.getType());
    }

    // T4: П2 - питомец уже есть в БД
    @Test
    void t4_existingPet_isNotSavedAgain() throws Exception {
        apiReturns("[{\"id\":\"abc-1\",\"url\":\"http://img/1.jpg\",\"breeds\":["
                + "{\"name\":\"Abyssinian\",\"temperament\":\"Active\"}]}]");
        when(petRepository.existsByApiPetIdAndType_Id(eq("abc-1"), any())).thenReturn(true);

        service.syncCats(1);

        verify(petRepository, never()).save(any(Pet.class));
    }

    // T5: П1, П2 - первая запись дубль, вторая новая
    @Test
    void t5_duplicateThenNew_savesOnlyNew() throws Exception {
        apiReturns("[{\"id\":\"dup\",\"url\":\"http://img/a.jpg\"},"
                + "{\"id\":\"new\",\"url\":\"http://img/b.jpg\"}]");
        when(petRepository.existsByApiPetIdAndType_Id(eq("dup"), any())).thenReturn(true);

        service.syncCats(2);

        verify(petRepository, times(1)).save(any(Pet.class));
        assertEquals("new", savedPet().getApiPetId());
    }

    // T6: П7 - пустой ответ API
    @Test
    void t6_emptyApiResponse_savesNothing() throws Exception {
        apiReturns("[]");

        assertDoesNotThrow(() -> service.syncDogs(5));

        verify(petRepository, never()).save(any(Pet.class));
    }

    // T7: П5 - тип не найден в справочнике
    @Test
    void t7_typeMissing_throwsAndSavesNothing() throws Exception {
        when(petTypeRepository.findByTypeName("Кошка")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.syncCats(1));

        assertTrue(ex.getMessage().contains("Кошка"));
        verify(petRepository, never()).save(any(Pet.class));
    }

    // T8: П6 - адрес и ключ для кошек
    @Test
    @SuppressWarnings("rawtypes")
    void t8_syncCats_usesCatApiUrlAndKey() throws Exception {
        apiReturns("[]");

        service.syncCats(5);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), entity.capture(),
                eq(CatDogApiItem[].class));
        assertEquals("https://api.thecatapi.com/v1/images/search?limit=5&has_breeds=1", url.getValue());
        assertEquals("test-cat-key", entity.getValue().getHeaders().getFirst("x-api-key"));
    }

    // T9: П6 - адрес и ключ для собак
    @Test
    @SuppressWarnings("rawtypes")
    void t9_syncDogs_usesDogApiUrlAndKey() throws Exception {
        apiReturns("[]");

        service.syncDogs(3);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), entity.capture(),
                eq(CatDogApiItem[].class));
        assertEquals("https://api.thedogapi.com/v1/images/search?limit=3&has_breeds=1", url.getValue());
        assertEquals("test-dog-key", entity.getValue().getHeaders().getFirst("x-api-key"));
    }
}
