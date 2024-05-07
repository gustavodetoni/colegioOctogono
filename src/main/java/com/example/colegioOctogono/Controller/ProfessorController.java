package com.example.colegioOctogono.Controller;

import com.example.colegioOctogono.Modelo.*;
import com.example.colegioOctogono.Repositorio.MateriaRepository;
import com.example.colegioOctogono.Repositorio.ProfessorRepository;
import com.example.colegioOctogono.Repositorio.RegistroPresencaRepository;
import com.example.colegioOctogono.Repositorio.TurmaRepository;
import com.example.colegioOctogono.Repositorio.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:5173")
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
    public ResponseEntity<List<Turma>> getAllTurmaNames() {
        List<Turma> turmas = turmaRepository.findAll();
        if (!turmas.isEmpty()) {
            return ResponseEntity.ok(turmas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Ver materias associadas a uma turma
    @GetMapping(value = "/{turmaId}/materias")
    public ResponseEntity<Set<Materia>> getMateriasByTurma(@PathVariable Long turmaId) {
        Optional<Turma> turmaOptional = turmaRepository.findById(turmaId);

        if (turmaOptional.isPresent()) {
            Set<Materia> materias = turmaOptional.get().getMaterias();
            if (!materias.isEmpty()) {
                return ResponseEntity.ok(materias);
            } else {
                return ResponseEntity.noContent().build();
            }
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

    //Ver os alunos associados a uma turma e materia
    @GetMapping(value = "/turma/{turmaId}/materia/{materiaId}/alunos")
    public ResponseEntity<List<Aluno>> getAlunosByTurmaEMateria(@PathVariable Long turmaId, @PathVariable Long materiaId) {
        List<Aluno> alunosFiltrados = alunoRepository.findAll().stream()
                .filter(aluno -> {
                    boolean alunoNaTurma = aluno.getTurmas().stream().anyMatch(turma -> turma.getId().equals(turmaId));
                    boolean alunoNaMateria = aluno.getMaterias().stream().anyMatch(materia -> materia.getId().equals(materiaId));
                    return alunoNaTurma && alunoNaMateria;
                })
                .collect(Collectors.toList());
        if (alunosFiltrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(alunosFiltrados);
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

    // Ver relatorio
    @GetMapping(value = "/relatorio-faltas/alunos/materia/{materiaId}")
    public ResponseEntity<?> getRelatorioFaltas(@RequestParam List<Long> alunoIds, @PathVariable Long materiaId) {
        Optional<Materia> materiaOptional = materiaRepository.findById(materiaId);

        if (!materiaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Materia materia = materiaOptional.get();
        List<Map<String, Object>> relatorios = new ArrayList<>();

        for (Long alunoId : alunoIds) {
            Optional<Aluno> alunoOptional = alunoRepository.findById(alunoId);
            if (alunoOptional.isPresent()) {
                Aluno aluno = alunoOptional.get();

                // Conta o número de faltas do aluno na matéria
                long numeroDeFaltas = registroPresencaRepository.findAll().stream()
                        .filter(registro -> registro.getAluno().equals(aluno) && registro.getMateria().equals(materia) && !registro.isPresente())
                        .count();

                // Cria um relatório para o aluno
                Map<String, Object> relatorio = new HashMap<>();
                relatorio.put("nomeAluno", aluno.getNome());
                relatorio.put("nomeMateria", materia.getNome());
                relatorio.put("faltas", numeroDeFaltas);

                relatorios.add(relatorio);
            }
        }

        if (relatorios.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(relatorios);
        }
    }



}

