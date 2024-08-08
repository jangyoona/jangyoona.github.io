package com.groupware.dto;

import lombok.Data;
import lombok.Setter;

@Data
public class BoardAttachDto {

    private int boardNo;
    private int boardAttachNo;
    private String boardUserFileName;
    private String boardSavedFileName;

}
