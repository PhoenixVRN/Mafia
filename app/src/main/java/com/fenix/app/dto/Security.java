package com.fenix.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class Security {
    private int loser = 0; // лузер
    private int experienced = 0; // опытный
    private int professional = 0; // профи
    private int specialist = 0; //  специалист

}
