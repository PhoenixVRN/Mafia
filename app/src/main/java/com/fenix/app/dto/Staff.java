package com.fenix.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class Staff {
    private int director = 0; // директор
    private int administrator = 0; // администратор
    private int beckoned = 0; // зазывала
}
