package com.example.colegioOctogono.Modelo;

import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "aluno")
@Entity(name = "aluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String matricula;
    private String emailResponsavel;

    @ManyToMany(mappedBy = "alunos")
    private Set<Turma> turmas;
}
