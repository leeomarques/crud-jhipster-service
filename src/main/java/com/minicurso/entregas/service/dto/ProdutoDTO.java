package com.minicurso.entregas.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.minicurso.entregas.domain.Produto} entity.
 */
public class ProdutoDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] imagem;

    private String imagemContentType;

    @NotNull
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getImagemContentType() {
        return imagemContentType;
    }

    public void setImagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProdutoDTO)) {
            return false;
        }

        ProdutoDTO produtoDTO = (ProdutoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, produtoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProdutoDTO{" +
            "id=" + getId() +
            ", imagem='" + getImagem() + "'" +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
