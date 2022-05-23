package com.minicurso.entregas.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.minicurso.entregas.domain.Pedido} entity. This class is used
 * in {@link com.minicurso.entregas.web.rest.PedidoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pedidos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PedidoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter entregue;

    private InstantFilter dtEntrega;

    private LongFilter pessoaId;

    private LongFilter enderecoId;

    private LongFilter produtoId;

    private Boolean distinct;

    public PedidoCriteria() {}

    public PedidoCriteria(PedidoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.entregue = other.entregue == null ? null : other.entregue.copy();
        this.dtEntrega = other.dtEntrega == null ? null : other.dtEntrega.copy();
        this.pessoaId = other.pessoaId == null ? null : other.pessoaId.copy();
        this.enderecoId = other.enderecoId == null ? null : other.enderecoId.copy();
        this.produtoId = other.produtoId == null ? null : other.produtoId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PedidoCriteria copy() {
        return new PedidoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BooleanFilter getEntregue() {
        return entregue;
    }

    public BooleanFilter entregue() {
        if (entregue == null) {
            entregue = new BooleanFilter();
        }
        return entregue;
    }

    public void setEntregue(BooleanFilter entregue) {
        this.entregue = entregue;
    }

    public InstantFilter getDtEntrega() {
        return dtEntrega;
    }

    public InstantFilter dtEntrega() {
        if (dtEntrega == null) {
            dtEntrega = new InstantFilter();
        }
        return dtEntrega;
    }

    public void setDtEntrega(InstantFilter dtEntrega) {
        this.dtEntrega = dtEntrega;
    }

    public LongFilter getPessoaId() {
        return pessoaId;
    }

    public LongFilter pessoaId() {
        if (pessoaId == null) {
            pessoaId = new LongFilter();
        }
        return pessoaId;
    }

    public void setPessoaId(LongFilter pessoaId) {
        this.pessoaId = pessoaId;
    }

    public LongFilter getEnderecoId() {
        return enderecoId;
    }

    public LongFilter enderecoId() {
        if (enderecoId == null) {
            enderecoId = new LongFilter();
        }
        return enderecoId;
    }

    public void setEnderecoId(LongFilter enderecoId) {
        this.enderecoId = enderecoId;
    }

    public LongFilter getProdutoId() {
        return produtoId;
    }

    public LongFilter produtoId() {
        if (produtoId == null) {
            produtoId = new LongFilter();
        }
        return produtoId;
    }

    public void setProdutoId(LongFilter produtoId) {
        this.produtoId = produtoId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PedidoCriteria that = (PedidoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(entregue, that.entregue) &&
            Objects.equals(dtEntrega, that.dtEntrega) &&
            Objects.equals(pessoaId, that.pessoaId) &&
            Objects.equals(enderecoId, that.enderecoId) &&
            Objects.equals(produtoId, that.produtoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entregue, dtEntrega, pessoaId, enderecoId, produtoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PedidoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (entregue != null ? "entregue=" + entregue + ", " : "") +
            (dtEntrega != null ? "dtEntrega=" + dtEntrega + ", " : "") +
            (pessoaId != null ? "pessoaId=" + pessoaId + ", " : "") +
            (enderecoId != null ? "enderecoId=" + enderecoId + ", " : "") +
            (produtoId != null ? "produtoId=" + produtoId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
