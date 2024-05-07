package com.example.colegioOctogono.Repositorio;

import com.example.colegioOctogono.Modelo.Aluno;
import com.example.colegioOctogono.Modelo.RegistroPresenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroPresencaRepository extends JpaRepository<RegistroPresenca, Long> {
    List<RegistroPresenca> findByData(LocalDate hoje);

    List<RegistroPresenca> findByAluno(Aluno aluno);
}
