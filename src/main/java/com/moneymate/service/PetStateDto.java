package com.moneymate.service;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PetStateDto {

    private int level;
    private int xp;
    private int bones;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBoxDate;
}
