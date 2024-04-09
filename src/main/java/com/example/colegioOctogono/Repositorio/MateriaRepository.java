package com.example.colegioOctogono.Repositorio;

import com.example.colegioOctogono.Modelo.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
}