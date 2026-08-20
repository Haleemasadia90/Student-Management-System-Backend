package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String username;
    private String email;
    private List<String> courseTitles;
}