package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "itemrequests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor", nullable = false)
    private User requestor;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();
}