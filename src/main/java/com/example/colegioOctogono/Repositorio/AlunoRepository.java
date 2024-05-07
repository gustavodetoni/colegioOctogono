package com.example.colegioOctogono.Repositorio;

import com.example.colegioOctogono.Modelo.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
