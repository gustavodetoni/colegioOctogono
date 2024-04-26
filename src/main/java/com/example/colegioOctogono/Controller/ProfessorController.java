package com.example.colegioOctogono.Controller;

import com.example.colegioOctogono.Modelo.*;
import com.example.colegioOctogono.Repositorio.MateriaRepository;
import com.example.colegioOctogono.Repositorio.ProfessorRepository;
import com.example.colegioOctogono.Repositorio.RegistroPresencaRepository;
import com.example.colegioOctogono.Repositorio.TurmaRepository;
import com.example.colegioOctogono.Repositorio.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/sistema")
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

    //Visualizar todos os professores
    @GetMapping(value = "/professor")
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


    //Ver as materias associadas a uma turma
    @GetMapping(value = "/{professorId}/turmas/{turmaId}/materias")
    public ResponseEntity<List<String>> getMateriasByTurmaAndProfessor(@PathVariable Long professorId, @PathVariable Long turmaId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Optional<Turma> turmaOptional = turmaRepository.findById(turmaId);
            if (turmaOptional.isPresent()) {
                Set<Materia> materias = turmaOptional.get().getMaterias();
                List<String> nomesMaterias = materias.stream()
                        .map(Materia::getNome)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(nomesMaterias);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Ver os alunos associados a uma turma
    @GetMapping(value = "/{professorId}/materia/{materiaId}/turma/{turmaId}/alunos")
    public ResponseEntity<List<String>> getAlunosByMateriaETurma(@PathVariable Long professorId, @PathVariable Long materiaId, @PathVariable Long turmaId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (!professorOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Aluno> todosAlunos = alunoRepository.findAll();
        List<Aluno> alunosFiltrados = todosAlunos.stream()
                .filter(aluno -> {
                    boolean alunoNaTurma = aluno.getTurmas().stream().anyMatch(turma -> turma.getId().equals(turmaId));
                    boolean alunoNaMateria = aluno.getMaterias().stream().anyMatch(materia -> materia.getId().equals(materiaId));
                    return alunoNaTurma && alunoNaMateria;
                })
                .collect(Collectors.toList());
        if (alunosFiltrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<String> nomesAlunos = alunosFiltrados.stream()
                    .map(Aluno::getNome)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(nomesAlunos);
        }
    }

    //Dar falta
    @PostMapping(value = "/{professorId}/materia/{materiaId}/turma/{turmaId}/registrar-faltas")
    public ResponseEntity<?> registrarFaltas(@PathVariable Long professorId,
                                             @PathVariable Long materiaId,
                                             @PathVariable Long turmaId,
                                             @RequestBody List<Long> idsAlunosFaltantes) {
        Optional<Professor> professor = professorRepository.findById(professorId);
        Optional<Materia> materia = materiaRepository.findById(materiaId);
        Optional<Turma> turma = turmaRepository.findById(turmaId);

        if (!professor.isPresent() || !materia.isPresent() || !turma.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        LocalDate hoje = LocalDate.now();
        List<RegistroPresenca> registrosHoje = registroPresencaRepository.findByData(hoje);

        idsAlunosFaltantes.forEach(idAluno -> {
            Optional<Aluno> aluno = alunoRepository.findById(idAluno);
            aluno.ifPresent(a -> {
                boolean jaRegistrado = registrosHoje.stream()
                        .anyMatch(registro -> registro.getAluno().equals(a) && registro.getMateria().equals(materia.get()));
                if (!jaRegistrado) {
                    RegistroPresenca novaFalta = new RegistroPresenca();
                    novaFalta.setAluno(a);
                    novaFalta.setMateria(materia.get());
                    novaFalta.setProfessor(professor.get());
                    novaFalta.setData(hoje);
                    novaFalta.setPresente(false);
                    registroPresencaRepository.save(novaFalta);
                }
            });
        });
        return ResponseEntity.ok().build();
    }

}

