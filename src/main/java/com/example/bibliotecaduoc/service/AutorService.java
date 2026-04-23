package com.example.bibliotecaduoc.service;

import com.example.bibliotecaduoc.model.Autor;
import com.example.bibliotecaduoc.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> getAutores() {
        return autorRepository.findAll();
    }

    public Autor saveAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public Autor getAutorId(int id) {
        return autorRepository.findById(id).orElse(null);
    }

    public Autor updateAutor(Autor autor) {
        if (!autorRepository.existsById(autor.getId())) {
            return null;
        }
        return autorRepository.save(autor);
    }

    public void deleteAutor(int id) {
        autorRepository.deleteById(id);
    }
}
