package com.example.colegioOctogono.Config;

import com.example.colegioOctogono.Modelo.Materia;
import com.example.colegioOctogono.Modelo.Professor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new Hibernate5Module());
        objectMapper.addMixIn(Professor.class, ProfessorMixin.class);
        objectMapper.addMixIn(Materia.class, MateriaMixin.class);
        return objectMapper;
    }

    abstract class ProfessorMixin {
        @JsonIgnore
        abstract Set<Materia> getMaterias();
    }

    abstract class MateriaMixin {
        @JsonIgnore
        abstract Set<Professor> getProfessores();
    }
}
