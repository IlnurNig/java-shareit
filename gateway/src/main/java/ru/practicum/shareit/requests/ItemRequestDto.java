package ru.practicum.shareit.requests;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {
    private long id;

    @NotBlank
    private String description;

    private LocalDateTime created;

    private Set<ItemDto> items;
}
