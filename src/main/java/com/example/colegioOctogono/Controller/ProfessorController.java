package com.example.colegioOctogono.Controller;

import com.example.colegioOctogono.Modelo.Materia;
import com.example.colegioOctogono.Modelo.Professor;
import com.example.colegioOctogono.Modelo.RegistroPresenca;
import com.example.colegioOctogono.Modelo.Turma;
import com.example.colegioOctogono.Repositorio.MateriaRepository;
import com.example.colegioOctogono.Repositorio.ProfessorRepository;
import com.example.colegioOctogono.Repositorio.RegistroPresencaRepository;
import com.example.colegioOctogono.Repositorio.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/professor")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    @GetMapping(value = "/{professorId}/materias")
    public ResponseEntity<Set<Materia>> getMateriasByProfessor(@PathVariable Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Set<Materia> materias = professorOptional.get().getMaterias();
            return ResponseEntity.ok(materias);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{professorId}/turmas")
    public ResponseEntity<Set<Turma>> getTurmasByProfessor(@PathVariable Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Set<Turma> turmas = new HashSet<>();
            for (Materia materia : professorOptional.get().getMaterias()) {
                turmas.addAll(materia.getTurmas());
            }
            return ResponseEntity.ok(turmas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/dar-falta") //PRECISADOS ID NECESSARIOS
    public ResponseEntity<?> darFalta(@RequestBody RegistroPresenca registroPresenca) {
        registroPresenca.setData(LocalDate.now());
        registroPresencaRepository.save(registroPresenca);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/todos")
    public ResponseEntity<List<Professor>> getAllProfessores() {
        List<Professor> professores = professorRepository.findAll();
        if (!professores.isEmpty()) {
            return ResponseEntity.ok(professores);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}

