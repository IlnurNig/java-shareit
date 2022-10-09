package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.Set;


/**
 * // TODO .
 */

@Entity
@Table(name = "users")
@Getter
@Setter
//@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "user")
    @ToString.Exclude
    private Set<Item> items;
}
