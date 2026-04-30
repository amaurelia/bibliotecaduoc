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
    public ResponseEntity<List<Autor>> listarAutores() {
        return ResponseEntity.ok(autorService.getAutores());
    }

    @PostMapping
    public ResponseEntity<Autor> agregarAutor(@Valid @RequestBody Autor autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.saveAutor(autor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscarAutor(@PathVariable int id) {
        Autor autor = autorService.getAutorId(id);
        if (autor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(autor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> actualizarAutor(@PathVariable int id, @Valid @RequestBody Autor autor) {
        autor.setId(id);
        Autor actualizado = autorService.updateAutor(autor);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAutor(@PathVariable int id) {
        autorService.deleteAutor(id);
        return ResponseEntity.noContent().build();
    }
}
