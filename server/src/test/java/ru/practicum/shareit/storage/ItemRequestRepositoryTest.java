package ru.practicum.shareit.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor1;
    private User requestor2;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void setUp() {
        requestor1 = new User();
        requestor1.setName("Анна Смирнова");
        requestor1.setEmail("anna@mail.ru");
        requestor1 = userRepository.save(requestor1);

        requestor2 = new User();
        requestor2.setName("Иван Петров");
        requestor2.setEmail("ivan@mail.ru");
        requestor2 = userRepository.save(requestor2);

        request1 = new ItemRequest();
        request1.setDescription("Нужна аккумуляторная дрель");
        request1.setRequestor(requestor1);
        request1 = itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Нужна отвертка");
        request2.setRequestor(requestor1);
        request2 = itemRequestRepository.save(request2);

        request3 = new ItemRequest();
        request3.setDescription("Нужен молоток");
        request3.setRequestor(requestor2);
        request3 = itemRequestRepository.save(request3);
    }

    @Test
    void save_shouldSaveItemRequest() {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("Нужна пила");
        newRequest.setRequestor(requestor1);

        ItemRequest saved = itemRequestRepository.save(newRequest);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Нужна пила");
        assertThat(saved.getRequestor().getId()).isEqualTo(requestor1.getId());
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void findById_shouldReturnItemRequest_whenExists() {
        ItemRequest found = itemRequestRepository.findById(request1.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(request1.getId());
        assertThat(found.getDescription()).isEqualTo("Нужна аккумуляторная дрель");
        assertThat(found.getRequestor().getId()).isEqualTo(requestor1.getId());
    }

    @Test
    void findAll_shouldReturnAllItemRequests() {
        List<ItemRequest> requests = itemRequestRepository.findAll();

        assertThat(requests).hasSize(3);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .containsExactlyInAnyOrder(
                        "Нужна аккумуляторная дрель",
                        "Нужна отвертка",
                        "Нужен молоток"
                );
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_shouldReturnRequestsOrderedByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(requestor1.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Нужна отвертка");
        assertThat(requests.get(1).getDescription()).isEqualTo("Нужна аккумуляторная дрель");
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_shouldReturnEmptyList_whenNoRequests() {
        User newUser = new User();
        newUser.setName("Петр Сидоров");
        newUser.setEmail("petr@mail.ru");
        newUser = userRepository.save(newUser);

        List<ItemRequest> requests = itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(newUser.getId());

        assertThat(requests).isEmpty();
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc_shouldReturnRequestsFromOtherUsers() {
        List<ItemRequest> requests = itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(requestor1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Нужен молоток");
        assertThat(requests.get(0).getRequestor().getId()).isEqualTo(requestor2.getId());
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc_shouldReturnAllRequests_whenUserHasNoRequests() {
        User newUser = new User();
        newUser.setName("Петр Сидоров");
        newUser.setEmail("petr@mail.ru");
        newUser = userRepository.save(newUser);

        List<ItemRequest> requests = itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(newUser.getId());

        assertThat(requests).hasSize(3);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .containsExactlyInAnyOrder(
                        "Нужна аккумуляторная дрель",
                        "Нужна отвертка",
                        "Нужен молоток"
                );
    }

    @Test
    void delete_shouldDeleteItemRequest() {
        itemRequestRepository.delete(request1);

        List<ItemRequest> requests = itemRequestRepository.findAll();
        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .doesNotContain("Нужна аккумуляторная дрель");
    }

    @Test
    void deleteById_shouldDeleteItemRequest() {
        itemRequestRepository.deleteById(request2.getId());

        List<ItemRequest> requests = itemRequestRepository.findAll();
        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription)
                .doesNotContain("Нужна отвертка");
    }

    @Test
    void count_shouldReturnCorrectCount() {
        long count = itemRequestRepository.count();

        assertThat(count).isEqualTo(3);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        boolean exists = itemRequestRepository.existsById(request1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenDoesNotExist() {
        boolean exists = itemRequestRepository.existsById(999L);

        assertThat(exists).isFalse();
    }
}