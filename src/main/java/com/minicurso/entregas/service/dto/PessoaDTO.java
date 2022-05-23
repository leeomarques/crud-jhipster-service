package com.minicurso.entregas.service.dto;

import com.minicurso.entregas.domain.enumeration.SimNao;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.minicurso.entregas.domain.Pessoa} entity.
 */
public class PessoaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3)
    private String nome;

    @Lob
    private byte[] foto;

    private String fotoContentType;
    private LocalDate dtNascimento;

    @Pattern(regexp = "([0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2})")
    private String cpf;

    private SimNao ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getFotoContentType() {
        return fotoContentType;
    }

    public void setFotoContentType(String fotoContentType) {
        this.fotoContentType = fotoContentType;
    }

    public LocalDate getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(LocalDate dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public SimNao getAtivo() {
        return ativo;
    }

    public void setAtivo(SimNao ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaDTO)) {
            return false;
        }

        PessoaDTO pessoaDTO = (PessoaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pessoaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", foto='" + getFoto() + "'" +
            ", dtNascimento='" + getDtNascimento() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
