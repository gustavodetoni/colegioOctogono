package Modelo;

import jakarta.persistence.*;
import lombok.Getter;


@Getter
@Entity(name = "professor")
public class Professor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String  email;

}
