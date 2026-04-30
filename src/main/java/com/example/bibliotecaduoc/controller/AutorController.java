package com.example.bibliotecaduoc.controller;

import com.example.bibliotecaduoc.model.Autor;
import com.example.bibliotecaduoc.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public ResponseEntity<?> listarAutores() {
        try {
            return ResponseEntity.ok(autorService.getAutores());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener autores: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> agregarAutor(@Valid @RequestBody Autor autor) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(autorService.saveAutor(autor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar autor: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAutor(@PathVariable int id) {
        try {
            Autor autor = autorService.getAutorId(id);
            if (autor == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(autor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar autor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAutor(@PathVariable int id, @Valid @RequestBody Autor autor) {
        try {
            autor.setId(id);
            Autor actualizado = autorService.updateAutor(autor);
            if (actualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar autor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAutor(@PathVariable int id) {
        try {
            autorService.deleteAutor(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar autor: " + e.getMessage());
        }
    }
}
