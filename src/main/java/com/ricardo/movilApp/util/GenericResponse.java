package com.ricardo.movilApp.util;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private String type;
    private int rpta;
    private String message;
    private T body;
}
