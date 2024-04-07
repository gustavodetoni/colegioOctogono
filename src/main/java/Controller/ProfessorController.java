package Controller;

import Modelo.*;
import Repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    @GetMapping("/{professorId}/materias")
    public ResponseEntity<Set<Materia>> getMateriasByProfessor(@PathVariable Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isPresent()) {
            Set<Materia> materias = professorOptional.get().getMaterias();
            return ResponseEntity.ok(materias);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{professorId}/turmas")
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

    @PostMapping("/dar-falta")
    public ResponseEntity<?> darFalta(@RequestBody RegistroPresenca registroPresenca) {
        registroPresenca.setData(LocalDate.now());
        registroPresencaRepository.save(registroPresenca);
        return ResponseEntity.ok().build();
    }
}

