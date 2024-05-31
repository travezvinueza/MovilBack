package com.ricardo.movilApp.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponse<T> {
    private String type;
    private int rpta;
    private String message;
    private T body;
}
