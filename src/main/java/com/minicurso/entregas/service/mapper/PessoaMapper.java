package com.minicurso.entregas.service.mapper;

import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.service.dto.PessoaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pessoa} and its DTO {@link PessoaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PessoaMapper extends EntityMapper<PessoaDTO, Pessoa> {}
