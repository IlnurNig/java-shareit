package ru.practicum.shareit.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAlLByRequester_IdOrderByCreatedDesc(long requesterId);

}
