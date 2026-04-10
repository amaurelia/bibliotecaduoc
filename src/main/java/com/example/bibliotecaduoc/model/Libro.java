package com.example.bibliotecaduoc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Libro {

    @NotNull
    private Integer id;

    @NotBlank
    private String isbn;

    @NotBlank
    private String titulo;

    @NotBlank
    private String editorial;

    @NotNull
    private Integer fechaPublicacion;

    @NotBlank
    private String autor;
}
