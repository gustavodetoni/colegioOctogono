package com.example.colegioOctogono.Repositorio;

import com.example.colegioOctogono.Modelo.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
}
