package ru.practicum.shareit.item.model;

import lombok.*;
import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;

/**
 * // TODO .
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long itemId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    private String name;

    private String description;

    private Boolean available;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "item")
    @ToString.Exclude
    private Set<Booking> bookings;

    @Transient
    @Lazy
    private Booking lastBooking;

    @Transient
    @Lazy
    private Booking nextBooking;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "item")
    @ToString.Exclude
    private Set<Comment> comments;

}
