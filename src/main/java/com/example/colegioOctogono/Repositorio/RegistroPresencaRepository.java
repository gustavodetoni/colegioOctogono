package com.example.colegioOctogono.Repositorio;

import com.example.colegioOctogono.Modelo.RegistroPresenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroPresencaRepository extends JpaRepository<RegistroPresenca, Long> {
}
