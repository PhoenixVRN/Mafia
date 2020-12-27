package com.fenix.app.data;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Название, уровень, описание
 */
@Data
@NoArgsConstructor
public class GeneralInfo {
    @NotNull
    private String name; // Название
    private int level; // Уровень
    private String description; // Описание
}
