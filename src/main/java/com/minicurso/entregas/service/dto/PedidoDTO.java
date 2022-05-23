package com.minicurso.entregas.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.minicurso.entregas.domain.Pedido} entity.
 */
public class PedidoDTO implements Serializable {

    private Long id;

    private Boolean entregue;

    private Instant dtEntrega;

    private PessoaDTO pessoa;

    private EnderecoDTO endereco;

    private ProdutoDTO produto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEntregue() {
        return entregue;
    }

    public void setEntregue(Boolean entregue) {
        this.entregue = entregue;
    }

    public Instant getDtEntrega() {
        return dtEntrega;
    }

    public void setDtEntrega(Instant dtEntrega) {
        this.dtEntrega = dtEntrega;
    }

    public PessoaDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaDTO pessoa) {
        this.pessoa = pessoa;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    public ProdutoDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoDTO produto) {
        this.produto = produto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PedidoDTO)) {
            return false;
        }

        PedidoDTO pedidoDTO = (PedidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pedidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PedidoDTO{" +
            "id=" + getId() +
            ", entregue='" + getEntregue() + "'" +
            ", dtEntrega='" + getDtEntrega() + "'" +
            ", pessoa=" + getPessoa() +
            ", endereco=" + getEndereco() +
            ", produto=" + getProduto() +
            "}";
    }
}
