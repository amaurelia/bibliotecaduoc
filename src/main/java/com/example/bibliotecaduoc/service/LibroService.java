package com.example.bibliotecaduoc.service;

import com.example.bibliotecaduoc.dto.LibroNacionalidadDTO;
import com.example.bibliotecaduoc.model.Libro;
import com.example.bibliotecaduoc.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> getLibros() {
        return libroRepository.findAll();
    }

    public Libro saveLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public Libro getLibroId(int id) {
        return libroRepository.findById(id).orElse(null);
    }

    public Libro updateLibro(Libro libro) {
        if (!libroRepository.existsById(libro.getId())) {
            return null;
        }
        return libroRepository.save(libro);
    }

    public void deleteLibro(int id) {
        libroRepository.deleteById(id);
    }

    public List<LibroNacionalidadDTO> getLibrosConNacionalidad() {
        return libroRepository.findAll().stream()
                .map(l -> new LibroNacionalidadDTO(
                        l.getTitulo(),
                        l.getAutor().getNacionalidad()
                ))
                .toList();
    }
}
