package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getComment_id())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .build();
    }

    public static Set<CommentDto> toDto(Set<Comment> comments) {
        if (comments == null) return null;
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toSet());
    }

    public static Comment toEntity(CommentDto commentDto,
                                   Item item,
                                   User author) {
        return Comment.builder()
                .comment_id(commentDto.getId())
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .build();
    }
}
