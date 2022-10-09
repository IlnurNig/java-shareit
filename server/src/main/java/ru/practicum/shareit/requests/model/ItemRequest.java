package ru.practicum.shareit.requests.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;

    private String description;

    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private User requester;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "itemRequest")
    @ToString.Exclude
    private Set<Item> items;
}
