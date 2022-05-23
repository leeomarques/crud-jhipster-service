package com.minicurso.entregas.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.minicurso.entregas.domain.Endereco} entity. This class is used
 * in {@link com.minicurso.entregas.web.rest.EnderecoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /enderecos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class EnderecoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter rua;

    private StringFilter cidade;

    private StringFilter bairro;

    private LongFilter pessoaId;

    private Boolean distinct;

    public EnderecoCriteria() {}

    public EnderecoCriteria(EnderecoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.rua = other.rua == null ? null : other.rua.copy();
        this.cidade = other.cidade == null ? null : other.cidade.copy();
        this.bairro = other.bairro == null ? null : other.bairro.copy();
        this.pessoaId = other.pessoaId == null ? null : other.pessoaId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EnderecoCriteria copy() {
        return new EnderecoCriteria(this);
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

    public StringFilter getRua() {
        return rua;
    }

    public StringFilter rua() {
        if (rua == null) {
            rua = new StringFilter();
        }
        return rua;
    }

    public void setRua(StringFilter rua) {
        this.rua = rua;
    }

    public StringFilter getCidade() {
        return cidade;
    }

    public StringFilter cidade() {
        if (cidade == null) {
            cidade = new StringFilter();
        }
        return cidade;
    }

    public void setCidade(StringFilter cidade) {
        this.cidade = cidade;
    }

    public StringFilter getBairro() {
        return bairro;
    }

    public StringFilter bairro() {
        if (bairro == null) {
            bairro = new StringFilter();
        }
        return bairro;
    }

    public void setBairro(StringFilter bairro) {
        this.bairro = bairro;
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
        final EnderecoCriteria that = (EnderecoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(rua, that.rua) &&
            Objects.equals(cidade, that.cidade) &&
            Objects.equals(bairro, that.bairro) &&
            Objects.equals(pessoaId, that.pessoaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rua, cidade, bairro, pessoaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnderecoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (rua != null ? "rua=" + rua + ", " : "") +
            (cidade != null ? "cidade=" + cidade + ", " : "") +
            (bairro != null ? "bairro=" + bairro + ", " : "") +
            (pessoaId != null ? "pessoaId=" + pessoaId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
