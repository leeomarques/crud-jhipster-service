package com.minicurso.entregas.service.mapper;

import com.minicurso.entregas.domain.Produto;
import com.minicurso.entregas.service.dto.ProdutoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produto} and its DTO {@link ProdutoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProdutoMapper extends EntityMapper<ProdutoDTO, Produto> {}
