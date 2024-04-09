package com.example.colegioOctogono.Modelo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "falta")
@Entity(name = "falta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class RegistroPresenca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;
    private LocalDate data;
    private boolean presente;

}