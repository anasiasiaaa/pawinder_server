package com.example.pawinder_server.service.cardlogic;

import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.entity_classes.PetType;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.repository_interfaces.PetTypeRepository;
import com.example.pawinder_server.service.CatDogApiItem;
import com.example.pawinder_server.service.PetSyncService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Подмена реализации: один и тот же потребитель PetSyncService и один и тот же
 * сценарий с каждой из двух реализаций. Код PetSyncService не меняется.
 */
@ExtendWith(MockitoExtension.class)
class PetSyncServiceCardLogicSubstitutionTest {

    @Mock private RestTemplate restTemplate;
    @Mock private PetRepository petRepository;
    @Mock private PetTypeRepository petTypeRepository;
    @InjectMocks private PetSyncService service;

    private final JsonMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    static Stream<PetCardLogicContract> implementations() {
        return Stream.of(new DefaultPetCardLogic(), new AlternativePetCardLogic());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void sameScenarioGivesSameCardWithAnyImplementation(PetCardLogicContract logic) throws Exception {
        ReflectionTestUtils.setField(service, "catApiKey", "k");
        ReflectionTestUtils.setField(service, "dogApiKey", "k");
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "cardLogic", logic);   // точка подмены
        when(petTypeRepository.findByTypeName("Кошка")).thenReturn(Optional.of(mock(PetType.class)));
        CatDogApiItem[] items = mapper.readValue(
                "[{\"id\":\"abc-1\",\"url\":\"http://img/1.jpg\",\"breeds\":["
                        + "{\"name\":\"Abyssinian\",\"temperament\":\"Active\"}]},"
                        + "{\"id\":\"abc-2\",\"url\":\"http://img/2.jpg\"}]",
                CatDogApiItem[].class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(CatDogApiItem[].class))).thenReturn(ResponseEntity.ok(items));

        service.syncCats(2);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals("Abyssinian", captor.getAllValues().get(0).getName());
        assertEquals("Active", captor.getAllValues().get(0).getDescription());
        assertEquals("Кот без породы", captor.getAllValues().get(1).getName());
    }
}
