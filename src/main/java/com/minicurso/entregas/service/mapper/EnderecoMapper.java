package com.minicurso.entregas.service.mapper;

import com.minicurso.entregas.domain.Endereco;
import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.service.dto.EnderecoDTO;
import com.minicurso.entregas.service.dto.PessoaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Endereco} and its DTO {@link EnderecoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnderecoMapper extends EntityMapper<EnderecoDTO, Endereco> {
    @Mapping(target = "pessoa", source = "pessoa", qualifiedByName = "pessoaNome")
    EnderecoDTO toDto(Endereco s);

    @Named("pessoaNome")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    PessoaDTO toDtoPessoaNome(Pessoa pessoa);
}
