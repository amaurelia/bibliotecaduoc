package com.example.bibliotecaduoc.controller;

import com.example.bibliotecaduoc.model.Autor;
import com.example.bibliotecaduoc.model.Libro;
import com.example.bibliotecaduoc.service.LibroService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroService libroService;

    @InjectMocks
    private LibroController libroController;

    @Test
    void crearLibro_retorna201_cuandoExisteAutor() {

        // Vamos a verificar que el método agregarLibro del controlador funciona correctamente
        // Para ello crearfemos un libro con un autor válido y simularemos el comportamiento del servicio
        Autor autor = new Autor(1, "Gabriel García Márquez", 87, "Colombiana");
        Libro libro = new Libro(1, "9780307474728", "Cien años de soledad", "Sudamericana", 1967, autor);

        // ""Simulamos""" el comportamiento del servicio (mock):
        // Así evitamos acceder a base de datos en una prueba unitaria.
        // Cuando el servicio intente guardar el libro, le decimos que devuelva el mismo libro (como si lo hubiera guardado).
        // Cuando el controlador invoque saveLibro con ese libro, Mockito devolverá ese mismo libro al instante, sin ejecutar lógica real, sin repositorio, sin DB.
        when(libroService.saveLibro(libro)).thenReturn(libro);

        // Llamamos al método del controlador que queremos probar.
        // El resultado es un ResponseEntity<Libro> con estado HTTP y cuerpo.
        var respuesta = libroController.agregarLibro(libro);

        // Para que el test sea completo, verificamos varios aspectos de la respuesta:

        // 1) La respuesta no debe ser nula.
        assertNotNull(respuesta);

        // 2) El estado HTTP esperado al crear un recurso es 201 (CREATED).
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());

        // 3) El cuerpo de la respuesta debe existir.
        var body = respuesta.getBody();
        assertNotNull(body);

        // 4) Validamos un dato clave del cuerpo para confirmar que se devolvió el libro correcto.
        assertEquals("Cien años de soledad", body.getTitulo());

    }
}
