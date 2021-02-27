package com.fenix.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class Staff {
    private int director; // директор
    private int administrator; // администратор
    private int beckoned; // зазывала
}
