package com.minicurso.entregas.service.mapper;

import com.minicurso.entregas.domain.Endereco;
import com.minicurso.entregas.domain.Pedido;
import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.domain.Produto;
import com.minicurso.entregas.service.dto.EnderecoDTO;
import com.minicurso.entregas.service.dto.PedidoDTO;
import com.minicurso.entregas.service.dto.PessoaDTO;
import com.minicurso.entregas.service.dto.ProdutoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pedido} and its DTO {@link PedidoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PedidoMapper extends EntityMapper<PedidoDTO, Pedido> {
    @Mapping(target = "pessoa", source = "pessoa", qualifiedByName = "pessoaNome")
    @Mapping(target = "endereco", source = "endereco", qualifiedByName = "enderecoCidade")
    @Mapping(target = "produto", source = "produto", qualifiedByName = "produtoDescricao")
    PedidoDTO toDto(Pedido s);

    @Named("pessoaNome")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    PessoaDTO toDtoPessoaNome(Pessoa pessoa);

    @Named("enderecoCidade")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cidade", source = "cidade")
    EnderecoDTO toDtoEnderecoCidade(Endereco endereco);

    @Named("produtoDescricao")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "descricao", source = "descricao")
    ProdutoDTO toDtoProdutoDescricao(Produto produto);
}
