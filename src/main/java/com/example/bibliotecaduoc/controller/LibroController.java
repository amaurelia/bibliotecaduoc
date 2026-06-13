package com.example.bibliotecaduoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bibliotecaduoc.dto.LibroNacionalidadDTO;
import com.example.bibliotecaduoc.model.Libro;
import com.example.bibliotecaduoc.service.LibroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<List<Libro>> listarLibros() {
        System.out.println("[LibroController] -> listarLibros");
        return ResponseEntity.ok(libroService.getLibros());
    }

    @PostMapping
    public ResponseEntity<Libro> agregarLibro(@Valid @RequestBody Libro libro) {
        System.out.println("[LibroController] -> agregarLibro");
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.saveLibro(libro));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarLibro(@PathVariable int id) {
        System.out.println("[LibroController] -> buscarLibro id=" + id);
        Libro libro = libroService.getLibroId(id);
        if (libro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(libro);
    }

    @GetMapping("/hateoas/{id}")
    public ResponseEntity<EntityModel<Libro>> buscarLibroHateoas(@PathVariable int id) {
        System.out.println("[LibroController] -> buscarLibroHateoas id=" + id);
        Libro libro = libroService.getLibroId(id);
        if (libro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toLibroModel(libro));
    }

    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Libro>>> listarLibrosHateoas() {
        System.out.println("[LibroController] -> listarLibrosHateoas");
        List<EntityModel<Libro>> libros = libroService.getLibros().stream()
                .map(this::toLibroModel)
                .toList();

        CollectionModel<EntityModel<Libro>> collection = CollectionModel.of(libros,
                linkTo(methodOn(LibroController.class).listarLibrosHateoas()).withSelfRel(),
                linkTo(methodOn(LibroController.class).listarLibros()).withRel("libros-sin-hateoas"));

        return ResponseEntity.ok(collection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable int id, @Valid @RequestBody Libro libro) {
        System.out.println("[LibroController] -> actualizarLibro id=" + id);
        libro.setId(id);
        Libro actualizado = libroService.updateLibro(libro);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable int id) {
        System.out.println("[LibroController] -> eliminarLibro id=" + id);
        libroService.deleteLibro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/con-nacionalidad")
    public ResponseEntity<List<LibroNacionalidadDTO>> librosPorNacionalidad() {
        System.out.println("[LibroController] -> librosPorNacionalidad");
        return ResponseEntity.ok(libroService.getLibrosConNacionalidad());
    }

    // Endpoint de prueba: lanza una excepción a propósito para demostrar el GlobalExceptionHandler
    @GetMapping("/test-error")
    public ResponseEntity<Libro> testError() {
        System.out.println("[LibroController] -> testError");
        throw new RuntimeException("Este es un error de prueba lanzado intencionalmente");
    }

    private EntityModel<Libro> toLibroModel(Libro libro) {
        EntityModel<Libro> model = EntityModel.of(libro,
                linkTo(methodOn(LibroController.class).buscarLibroHateoas(libro.getId())).withSelfRel(),
                linkTo(methodOn(LibroController.class).buscarLibro(libro.getId())).withRel("detalle-sin-hateoas"),
                linkTo(methodOn(LibroController.class).listarLibrosHateoas()).withRel("coleccion"));

        if (libro.getAutor() != null && libro.getAutor().getId() != null) {
            model.add(linkTo(methodOn(AutorController.class).buscarAutor(libro.getAutor().getId())).withRel("autor"));
        }

        return model;
    }
}

