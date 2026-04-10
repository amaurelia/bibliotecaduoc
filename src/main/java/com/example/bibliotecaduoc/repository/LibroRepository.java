package com.example.bibliotecaduoc.repository;

import com.example.bibliotecaduoc.model.Libro;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LibroRepository {

    private List<Libro> listaLibros = new ArrayList<>();

    public List<Libro> obtenerLibros() {
        return listaLibros;
    }

    public Libro buscarPorId(int id) {
        for (Libro libro : listaLibros) {
            if (libro.getId() == id) {
                return libro;
            }
        }
        return null;
    }

    public Libro buscarPorIsbn(String isbn) {
        for (Libro libro : listaLibros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    public Libro guardar(Libro lib) {
        if (buscarPorId(lib.getId()) != null) {
            return null;
        }
        listaLibros.add(lib);
        return lib;
    }

    public Libro actualizar(Libro lib) {
        for (int i = 0; i < listaLibros.size(); i++) {
            if (listaLibros.get(i).getId() == lib.getId()) {
                listaLibros.set(i, lib);
                return lib;
            }
        }
        return null;
    }

    public void eliminar(int id) {
        listaLibros.removeIf(x -> x.getId() == id);
    }
}
