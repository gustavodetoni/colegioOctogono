package com.example.colegioOctogono.Modelo;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "materias")
@Entity(name = "materias")
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @JsonManagedReference
    @ManyToMany(mappedBy = "materias")
    private Set<Professor> professores;

    @OneToMany(mappedBy = "materias")
    private Set<Turma> turmas;

    public Set<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(Set<Turma> turmas) {
        this.turmas = turmas;
    }

    @ManyToOne
    @JoinColumn(name = "id_aluno")
    private Aluno aluno;
}