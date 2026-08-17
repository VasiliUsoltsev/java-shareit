package ru.practicum.shareit.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    @BeforeEach
    void setUp() {
        owner1 = new User();
        owner1.setName("Анна Смирнова");
        owner1.setEmail("anna@mail.ru");
        owner1 = userRepository.save(owner1);

        owner2 = new User();
        owner2.setName("Иван Петров");
        owner2.setEmail("ivan@mail.ru");
        owner2 = userRepository.save(owner2);

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Аккумуляторная дрель");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Крестовая отвертка");
        item2.setAvailable(true);
        item2.setOwner(owner1);
        item2 = itemRepository.save(item2);

        item3 = new Item();
        item3.setName("Молоток");
        item3.setDescription("Ударный молоток");
        item3.setAvailable(false);
        item3.setOwner(owner2);
        item3 = itemRepository.save(item3);

        item4 = new Item();
        item4.setName("Пила");
        item4.setDescription("Электрическая пила");
        item4.setAvailable(true);
        item4.setOwner(owner2);
        item4 = itemRepository.save(item4);
    }

    @Test
    void save_shouldSaveItem() {
        Item newItem = new Item();
        newItem.setName("Гайковерт");
        newItem.setDescription("Аккумуляторный гайковерт");
        newItem.setAvailable(true);
        newItem.setOwner(owner1);

        Item saved = itemRepository.save(newItem);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Гайковерт");
        assertThat(saved.getDescription()).isEqualTo("Аккумуляторный гайковерт");
        assertThat(saved.getAvailable()).isTrue();
        assertThat(saved.getOwner().getId()).isEqualTo(owner1.getId());
    }

    @Test
    void findById_shouldReturnItem_whenExists() {
        Optional<Item> found = itemRepository.findById(item1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Дрель");
        assertThat(found.get().getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(found.get().getAvailable()).isTrue();
        assertThat(found.get().getOwner().getId()).isEqualTo(owner1.getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenItemDoesNotExist() {
        Optional<Item> found = itemRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByOwnerId_shouldReturnAllItemsForOwner() {
        List<Item> items = itemRepository.findByOwnerId(owner1.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName)
                .containsExactlyInAnyOrder("Дрель", "Отвертка");
    }

    @Test
    void findByOwnerId_shouldReturnEmptyList_whenOwnerHasNoItems() {
        User newUser = new User();
        newUser.setName("Петр Сидоров");
        newUser.setEmail("petr@mail.ru");
        newUser = userRepository.save(newUser);

        List<Item> items = itemRepository.findByOwnerId(newUser.getId());

        assertThat(items).isEmpty();
    }

    @Test
    void searchByText_shouldReturnItemsMatchingName() {
        List<Item> items = itemRepository.searchByText("дрель");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
        assertThat(items.get(0).getAvailable()).isTrue();
    }

    @Test
    void searchByText_shouldReturnItemsMatchingDescription() {
        List<Item> items = itemRepository.searchByText("крестовая");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Отвертка");
        assertThat(items.get(0).getAvailable()).isTrue();
    }

    @Test
    void searchByText_shouldReturnItemsMatchingPartialText() {
        List<Item> items = itemRepository.searchByText("аккумуляторная");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchByText_shouldBeCaseInsensitive() {
        List<Item> items = itemRepository.searchByText("АККУМУЛЯТОРНАЯ");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchByText_shouldOnlyReturnAvailableItems() {
        // item3 (Молоток) имеет available = false, не должен попасть в результат
        List<Item> items = itemRepository.searchByText("молоток");

        assertThat(items).isEmpty();
    }

    @Test
    void searchByText_shouldReturnEmptyList_whenNoMatch() {
        List<Item> items = itemRepository.searchByText("несуществующий текст");

        assertThat(items).isEmpty();
    }

    @Test
    void existsByOwnerId_shouldReturnTrue_whenOwnerHasItems() {
        boolean exists = itemRepository.existsByOwnerId(owner1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByOwnerId_shouldReturnFalse_whenOwnerHasNoItems() {
        User newUser = new User();
        newUser.setName("Петр Сидоров");
        newUser.setEmail("petr@mail.ru");
        newUser = userRepository.save(newUser);

        boolean exists = itemRepository.existsByOwnerId(newUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void findByIdWithComments_shouldReturnItemWithComments() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(owner2);
        comment.setItem(item1);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        Comment comment2 = new Comment();
        comment2.setText("Очень мощная");
        comment2.setAuthor(owner1);
        comment2.setItem(item1);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByItemId(item1.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText)
                .containsExactlyInAnyOrder("Отличная дрель!", "Очень мощная");
    }

    @Test
    void findByIdWithComments_shouldReturnItemWithoutComments() {
        Optional<Item> found = itemRepository.findByIdWithComments(item2.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getComments()).isEmpty();
    }

    @Test
    void findByIdWithComments_shouldReturnEmpty_whenItemDoesNotExist() {
        Optional<Item> found = itemRepository.findByIdWithComments(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldUpdateItemFields() {
        item1.setName("Обновленная дрель");
        item1.setDescription("Обновленное описание");
        item1.setAvailable(false);

        Item updated = itemRepository.save(item1);

        assertThat(updated.getName()).isEqualTo("Обновленная дрель");
        assertThat(updated.getDescription()).isEqualTo("Обновленное описание");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void delete_shouldDeleteItem() {
        itemRepository.delete(item1);

        Optional<Item> found = itemRepository.findById(item1.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        long count = itemRepository.count();

        assertThat(count).isEqualTo(4);
    }

    @Test
    void existsById_shouldReturnTrue_whenItemExists() {
        boolean exists = itemRepository.existsById(item1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenItemDoesNotExist() {
        boolean exists = itemRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void searchByText_shouldReturnMultipleItems() {
        // Создаем еще одну дрель
        Item anotherDrill = new Item();
        anotherDrill.setName("Дрель ударная");
        anotherDrill.setDescription("Ударная дрель");
        anotherDrill.setAvailable(true);
        anotherDrill.setOwner(owner2);
        itemRepository.save(anotherDrill);

        List<Item> items = itemRepository.searchByText("дрель");

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName)
                .containsExactlyInAnyOrder("Дрель", "Дрель ударная");
    }
}