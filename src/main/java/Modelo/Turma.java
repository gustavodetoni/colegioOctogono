package Modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Table(name = "turma")
@Entity(name = "turma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToMany
    @JoinTable(name = "turma_aluno",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id"))
    private Set<Aluno> alunos;


}
