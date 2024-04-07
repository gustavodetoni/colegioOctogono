package Modelo;

import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "professor")
@Entity(name = "professor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Professor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String  email;

    @ManyToMany
    @JoinTable(name = "professor_materia",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id"))

    private Set<Materia> materias;
}
