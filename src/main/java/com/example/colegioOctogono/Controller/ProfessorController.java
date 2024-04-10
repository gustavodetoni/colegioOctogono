package com.example.colegioOctogono.Controller;

import com.example.colegioOctogono.Modelo.*;
import com.example.colegioOctogono.Repositorio.MateriaRepository;
import com.example.colegioOctogono.Repositorio.ProfessorRepository;
import com.example.colegioOctogono.Repositorio.RegistroPresencaRepository;
import com.example.colegioOctogono.Repositorio.TurmaRepository;
import com.example.colegioOctogono.Repositorio.AlunoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/professor")
public class ProfessorController {
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    //Visualizar todos os professores.
    @GetMapping(value = "/todos")
    public ResponseEntity<List<Professor>> getAllProfessores() {
        List<Professor> professores = professorRepository.findAll();
        if (!professores.isEmpty()) {
            return ResponseEntity.ok(professores);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    //Ver todos os alunos
    @GetMapping(value = "/alunos")
    public ResponseEntity<List<String>> getAllAlunoNames() {
        List<Aluno> alunos = alunoRepository.findAll();
        if (!alunos.isEmpty()) {
            List<String> nomesAlunos = alunos.stream()
                    .map(Aluno::getNome)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(nomesAlunos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Ver todas as turmas
    @GetMapping(value = "/turmas")
    public ResponseEntity<List<String>> getAllTurmaNames() {
        List<Turma> turmas = turmaRepository.findAll();
        if (!turmas.isEmpty()) {
            List<String> nomesTurmas = turmas.stream()
                    .map(Turma::getNome)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(nomesTurmas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    //Ver as matérias associadas a um professor
    @GetMapping(value = "/{professorId}/materias")
    public ResponseEntity<Set<String>> getMateriasByProfessor(@PathVariable Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Set<Materia> materias = professorOptional.get().getMaterias();
            Set<String> nomesMaterias = materias.stream()
                    .map(Materia::getNome)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(nomesMaterias);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //Ver as turmas associadas a uma matéria
    @Transactional
    @GetMapping(value = "/{professorId}/materias/{materiaId}/turmas")
    public ResponseEntity<List<String>> getTurmasByMateriaAndProfessor(@PathVariable Long professorId, @PathVariable Long materiaId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Optional<Materia> materiaOptional = materiaRepository.findById(materiaId);
            if (materiaOptional.isPresent()) {
                Set<Turma> turmas = materiaOptional.get().getTurmas();
                List<String> nomesTurmas = turmas.stream()
                        .map(Turma::getNome)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(nomesTurmas);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Ver os alunos associados a uma turma
    @GetMapping(value = "/{professorId}/materias/{materiaId}/turmas/{turmaId}/alunos")
    public ResponseEntity<Set<Aluno>> getAlunosByTurmaAndMateriaAndProfessor(@PathVariable Long professorId, @PathVariable Long materiaId, @PathVariable Long turmaId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Optional<Materia> materiaOptional = materiaRepository.findById(materiaId);
            if (materiaOptional.isPresent()) {
                Optional<Turma> turmaOptional = turmaRepository.findById(turmaId);
                if (turmaOptional.isPresent()) {
                    Set<Aluno> alunos = turmaOptional.get().getAlunos();
                    return ResponseEntity.ok(alunos);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

