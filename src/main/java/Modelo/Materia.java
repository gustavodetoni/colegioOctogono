package Modelo;

import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "materias")
@Entity(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToMany(mappedBy = "materias")
    private Set<Professor> professores;

    @ManyToMany(mappedBy = "materias")
    private Set<Turma> turmas;

    public Set<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(Set<Turma> turmas) {
        this.turmas = turmas;
    }
}