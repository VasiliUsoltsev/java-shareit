package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    public List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE (" +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    public List<Item> searchByText(@Param("text") String text);

    public boolean existsByOwnerId(Long ownerId);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.author WHERE i.id = :id")
    Optional<Item> findByIdWithComments(@Param("id") Long id);

}
