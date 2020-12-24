package com.fenix.app.data;
import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class GenInf {
    private String name; // Название
    private int level; //Уровень
    private String description; // Описание
}
