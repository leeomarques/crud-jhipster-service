package com.minicurso.entregas.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.minicurso.entregas.domain.Endereco} entity.
 */
public class EnderecoDTO implements Serializable {

    private Long id;

    @NotNull
    private String rua;

    @NotNull
    private String cidade;

    @NotNull
    private String bairro;

    private PessoaDTO pessoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public PessoaDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaDTO pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnderecoDTO)) {
            return false;
        }

        EnderecoDTO enderecoDTO = (EnderecoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enderecoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnderecoDTO{" +
            "id=" + getId() +
            ", rua='" + getRua() + "'" +
            ", cidade='" + getCidade() + "'" +
            ", bairro='" + getBairro() + "'" +
            ", pessoa=" + getPessoa() +
            "}";
    }
}
