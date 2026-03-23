package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}