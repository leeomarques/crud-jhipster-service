package com.minicurso.entregas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Pedido.
 */
@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "entregue")
    private Boolean entregue;

    @Column(name = "dt_entrega")
    private Instant dtEntrega;

    @ManyToOne(optional = false)
    @NotNull
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "pessoa" }, allowSetters = true)
    private Endereco endereco;

    @ManyToOne(optional = false)
    @NotNull
    private Produto produto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pedido id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEntregue() {
        return this.entregue;
    }

    public Pedido entregue(Boolean entregue) {
        this.setEntregue(entregue);
        return this;
    }

    public void setEntregue(Boolean entregue) {
        this.entregue = entregue;
    }

    public Instant getDtEntrega() {
        return this.dtEntrega;
    }

    public Pedido dtEntrega(Instant dtEntrega) {
        this.setDtEntrega(dtEntrega);
        return this;
    }

    public void setDtEntrega(Instant dtEntrega) {
        this.dtEntrega = dtEntrega;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pedido pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        return this;
    }

    public Endereco getEndereco() {
        return this.endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Pedido endereco(Endereco endereco) {
        this.setEndereco(endereco);
        return this;
    }

    public Produto getProduto() {
        return this.produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Pedido produto(Produto produto) {
        this.setProduto(produto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido)) {
            return false;
        }
        return id != null && id.equals(((Pedido) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pedido{" +
            "id=" + getId() +
            ", entregue='" + getEntregue() + "'" +
            ", dtEntrega='" + getDtEntrega() + "'" +
            "}";
    }
}
