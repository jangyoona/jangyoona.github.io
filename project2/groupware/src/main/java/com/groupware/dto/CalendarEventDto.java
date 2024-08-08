package com.groupware.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class CalendarEventDto {
    private Integer id;
    private Integer eventId;
    private String title;
//    private Date start;
//    private Date end;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Timezone=Asia") // pattern = "y-M-d H:m", timezone = "UTC"
    private Timestamp start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Timezone=Asia")
    private Timestamp end;
    private boolean allDay;
    private String backgroundColor;
    private String borderColor;
    private String url;
    private int calendarNo;
    private String content;


}