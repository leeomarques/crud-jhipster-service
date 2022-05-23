package com.minicurso.entregas.service.criteria;

import com.minicurso.entregas.domain.enumeration.SimNao;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.minicurso.entregas.domain.Pessoa} entity. This class is used
 * in {@link com.minicurso.entregas.web.rest.PessoaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pessoas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PessoaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SimNao
     */
    public static class SimNaoFilter extends Filter<SimNao> {

        public SimNaoFilter() {}

        public SimNaoFilter(SimNaoFilter filter) {
            super(filter);
        }

        @Override
        public SimNaoFilter copy() {
            return new SimNaoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private LocalDateFilter dtNascimento;

    private StringFilter cpf;

    private SimNaoFilter ativo;

    private Boolean distinct;

    public PessoaCriteria() {}

    public PessoaCriteria(PessoaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.dtNascimento = other.dtNascimento == null ? null : other.dtNascimento.copy();
        this.cpf = other.cpf == null ? null : other.cpf.copy();
        this.ativo = other.ativo == null ? null : other.ativo.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PessoaCriteria copy() {
        return new PessoaCriteria(this);
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

    public StringFilter getNome() {
        return nome;
    }

    public StringFilter nome() {
        if (nome == null) {
            nome = new StringFilter();
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public LocalDateFilter getDtNascimento() {
        return dtNascimento;
    }

    public LocalDateFilter dtNascimento() {
        if (dtNascimento == null) {
            dtNascimento = new LocalDateFilter();
        }
        return dtNascimento;
    }

    public void setDtNascimento(LocalDateFilter dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public StringFilter getCpf() {
        return cpf;
    }

    public StringFilter cpf() {
        if (cpf == null) {
            cpf = new StringFilter();
        }
        return cpf;
    }

    public void setCpf(StringFilter cpf) {
        this.cpf = cpf;
    }

    public SimNaoFilter getAtivo() {
        return ativo;
    }

    public SimNaoFilter ativo() {
        if (ativo == null) {
            ativo = new SimNaoFilter();
        }
        return ativo;
    }

    public void setAtivo(SimNaoFilter ativo) {
        this.ativo = ativo;
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
        final PessoaCriteria that = (PessoaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(dtNascimento, that.dtNascimento) &&
            Objects.equals(cpf, that.cpf) &&
            Objects.equals(ativo, that.ativo) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, dtNascimento, cpf, ativo, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nome != null ? "nome=" + nome + ", " : "") +
            (dtNascimento != null ? "dtNascimento=" + dtNascimento + ", " : "") +
            (cpf != null ? "cpf=" + cpf + ", " : "") +
            (ativo != null ? "ativo=" + ativo + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
