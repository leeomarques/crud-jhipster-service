package com.minicurso.entregas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.minicurso.entregas.IntegrationTest;
import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.domain.enumeration.SimNao;
import com.minicurso.entregas.repository.PessoaRepository;
import com.minicurso.entregas.service.criteria.PessoaCriteria;
import com.minicurso.entregas.service.dto.PessoaDTO;
import com.minicurso.entregas.service.mapper.PessoaMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PessoaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PessoaResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FOTO_CONTENT_TYPE = "image/png";

    private static final LocalDate DEFAULT_DT_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DT_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DT_NASCIMENTO = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CPF = "749.894394-15";
    private static final String UPDATED_CPF = "522289.270-11";

    private static final SimNao DEFAULT_ATIVO = SimNao.SIM;
    private static final SimNao UPDATED_ATIVO = SimNao.NAO;

    private static final String ENTITY_API_URL = "/api/pessoas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaMapper pessoaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPessoaMockMvc;

    private Pessoa pessoa;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .nome(DEFAULT_NOME)
            .foto(DEFAULT_FOTO)
            .fotoContentType(DEFAULT_FOTO_CONTENT_TYPE)
            .dtNascimento(DEFAULT_DT_NASCIMENTO)
            .cpf(DEFAULT_CPF)
            .ativo(DEFAULT_ATIVO);
        return pessoa;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createUpdatedEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .nome(UPDATED_NOME)
            .foto(UPDATED_FOTO)
            .fotoContentType(UPDATED_FOTO_CONTENT_TYPE)
            .dtNascimento(UPDATED_DT_NASCIMENTO)
            .cpf(UPDATED_CPF)
            .ativo(UPDATED_ATIVO);
        return pessoa;
    }

    @BeforeEach
    public void initTest() {
        pessoa = createEntity(em);
    }

    @Test
    @Transactional
    void createPessoa() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();
        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isCreated());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate + 1);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoa.getFoto()).isEqualTo(DEFAULT_FOTO);
        assertThat(testPessoa.getFotoContentType()).isEqualTo(DEFAULT_FOTO_CONTENT_TYPE);
        assertThat(testPessoa.getDtNascimento()).isEqualTo(DEFAULT_DT_NASCIMENTO);
        assertThat(testPessoa.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoa.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    @Transactional
    void createPessoaWithExistingId() throws Exception {
        // Create the Pessoa with an existing ID
        pessoa.setId(1L);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaRepository.findAll().size();
        // set the field null
        pessoa.setNome(null);

        // Create the Pessoa, which fails.
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPessoas() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].fotoContentType").value(hasItem(DEFAULT_FOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].foto").value(hasItem(Base64Utils.encodeToString(DEFAULT_FOTO))))
            .andExpect(jsonPath("$.[*].dtNascimento").value(hasItem(DEFAULT_DT_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].ativo").value(hasItem(DEFAULT_ATIVO.toString())));
    }

    @Test
    @Transactional
    void getPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get the pessoa
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL_ID, pessoa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pessoa.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.fotoContentType").value(DEFAULT_FOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.foto").value(Base64Utils.encodeToString(DEFAULT_FOTO)))
            .andExpect(jsonPath("$.dtNascimento").value(DEFAULT_DT_NASCIMENTO.toString()))
            .andExpect(jsonPath("$.cpf").value(DEFAULT_CPF))
            .andExpect(jsonPath("$.ativo").value(DEFAULT_ATIVO.toString()));
    }

    @Test
    @Transactional
    void getPessoasByIdFiltering() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        Long id = pessoa.getId();

        defaultPessoaShouldBeFound("id.equals=" + id);
        defaultPessoaShouldNotBeFound("id.notEquals=" + id);

        defaultPessoaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPessoaShouldNotBeFound("id.greaterThan=" + id);

        defaultPessoaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPessoaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome equals to DEFAULT_NOME
        defaultPessoaShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Get all the pessoaList where nome equals to UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome not equals to DEFAULT_NOME
        defaultPessoaShouldNotBeFound("nome.notEquals=" + DEFAULT_NOME);

        // Get all the pessoaList where nome not equals to UPDATED_NOME
        defaultPessoaShouldBeFound("nome.notEquals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome in DEFAULT_NOME or UPDATED_NOME
        defaultPessoaShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Get all the pessoaList where nome equals to UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome is not null
        defaultPessoaShouldBeFound("nome.specified=true");

        // Get all the pessoaList where nome is null
        defaultPessoaShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    void getAllPessoasByNomeContainsSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome contains DEFAULT_NOME
        defaultPessoaShouldBeFound("nome.contains=" + DEFAULT_NOME);

        // Get all the pessoaList where nome contains UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where nome does not contain DEFAULT_NOME
        defaultPessoaShouldNotBeFound("nome.doesNotContain=" + DEFAULT_NOME);

        // Get all the pessoaList where nome does not contain UPDATED_NOME
        defaultPessoaShouldBeFound("nome.doesNotContain=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento equals to DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.equals=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento equals to UPDATED_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.equals=" + UPDATED_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento not equals to DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.notEquals=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento not equals to UPDATED_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.notEquals=" + UPDATED_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento in DEFAULT_DT_NASCIMENTO or UPDATED_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.in=" + DEFAULT_DT_NASCIMENTO + "," + UPDATED_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento equals to UPDATED_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.in=" + UPDATED_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento is not null
        defaultPessoaShouldBeFound("dtNascimento.specified=true");

        // Get all the pessoaList where dtNascimento is null
        defaultPessoaShouldNotBeFound("dtNascimento.specified=false");
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento is greater than or equal to DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.greaterThanOrEqual=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento is greater than or equal to UPDATED_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.greaterThanOrEqual=" + UPDATED_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento is less than or equal to DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.lessThanOrEqual=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento is less than or equal to SMALLER_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.lessThanOrEqual=" + SMALLER_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsLessThanSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento is less than DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.lessThan=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento is less than UPDATED_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.lessThan=" + UPDATED_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDtNascimentoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where dtNascimento is greater than DEFAULT_DT_NASCIMENTO
        defaultPessoaShouldNotBeFound("dtNascimento.greaterThan=" + DEFAULT_DT_NASCIMENTO);

        // Get all the pessoaList where dtNascimento is greater than SMALLER_DT_NASCIMENTO
        defaultPessoaShouldBeFound("dtNascimento.greaterThan=" + SMALLER_DT_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByCpfIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf equals to DEFAULT_CPF
        defaultPessoaShouldBeFound("cpf.equals=" + DEFAULT_CPF);

        // Get all the pessoaList where cpf equals to UPDATED_CPF
        defaultPessoaShouldNotBeFound("cpf.equals=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllPessoasByCpfIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf not equals to DEFAULT_CPF
        defaultPessoaShouldNotBeFound("cpf.notEquals=" + DEFAULT_CPF);

        // Get all the pessoaList where cpf not equals to UPDATED_CPF
        defaultPessoaShouldBeFound("cpf.notEquals=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllPessoasByCpfIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf in DEFAULT_CPF or UPDATED_CPF
        defaultPessoaShouldBeFound("cpf.in=" + DEFAULT_CPF + "," + UPDATED_CPF);

        // Get all the pessoaList where cpf equals to UPDATED_CPF
        defaultPessoaShouldNotBeFound("cpf.in=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllPessoasByCpfIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf is not null
        defaultPessoaShouldBeFound("cpf.specified=true");

        // Get all the pessoaList where cpf is null
        defaultPessoaShouldNotBeFound("cpf.specified=false");
    }

    @Test
    @Transactional
    void getAllPessoasByCpfContainsSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf contains DEFAULT_CPF
        defaultPessoaShouldBeFound("cpf.contains=" + DEFAULT_CPF);

        // Get all the pessoaList where cpf contains UPDATED_CPF
        defaultPessoaShouldNotBeFound("cpf.contains=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllPessoasByCpfNotContainsSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where cpf does not contain DEFAULT_CPF
        defaultPessoaShouldNotBeFound("cpf.doesNotContain=" + DEFAULT_CPF);

        // Get all the pessoaList where cpf does not contain UPDATED_CPF
        defaultPessoaShouldBeFound("cpf.doesNotContain=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllPessoasByAtivoIsEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where ativo equals to DEFAULT_ATIVO
        defaultPessoaShouldBeFound("ativo.equals=" + DEFAULT_ATIVO);

        // Get all the pessoaList where ativo equals to UPDATED_ATIVO
        defaultPessoaShouldNotBeFound("ativo.equals=" + UPDATED_ATIVO);
    }

    @Test
    @Transactional
    void getAllPessoasByAtivoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where ativo not equals to DEFAULT_ATIVO
        defaultPessoaShouldNotBeFound("ativo.notEquals=" + DEFAULT_ATIVO);

        // Get all the pessoaList where ativo not equals to UPDATED_ATIVO
        defaultPessoaShouldBeFound("ativo.notEquals=" + UPDATED_ATIVO);
    }

    @Test
    @Transactional
    void getAllPessoasByAtivoIsInShouldWork() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where ativo in DEFAULT_ATIVO or UPDATED_ATIVO
        defaultPessoaShouldBeFound("ativo.in=" + DEFAULT_ATIVO + "," + UPDATED_ATIVO);

        // Get all the pessoaList where ativo equals to UPDATED_ATIVO
        defaultPessoaShouldNotBeFound("ativo.in=" + UPDATED_ATIVO);
    }

    @Test
    @Transactional
    void getAllPessoasByAtivoIsNullOrNotNull() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList where ativo is not null
        defaultPessoaShouldBeFound("ativo.specified=true");

        // Get all the pessoaList where ativo is null
        defaultPessoaShouldNotBeFound("ativo.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPessoaShouldBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].fotoContentType").value(hasItem(DEFAULT_FOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].foto").value(hasItem(Base64Utils.encodeToString(DEFAULT_FOTO))))
            .andExpect(jsonPath("$.[*].dtNascimento").value(hasItem(DEFAULT_DT_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].ativo").value(hasItem(DEFAULT_ATIVO.toString())));

        // Check, that the count call also returns 1
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPessoaShouldNotBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPessoa() throws Exception {
        // Get the pessoa
        restPessoaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa
        Pessoa updatedPessoa = pessoaRepository.findById(pessoa.getId()).get();
        // Disconnect from session so that the updates on updatedPessoa are not directly saved in db
        em.detach(updatedPessoa);
        updatedPessoa
            .nome(UPDATED_NOME)
            .foto(UPDATED_FOTO)
            .fotoContentType(UPDATED_FOTO_CONTENT_TYPE)
            .dtNascimento(UPDATED_DT_NASCIMENTO)
            .cpf(UPDATED_CPF)
            .ativo(UPDATED_ATIVO);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(updatedPessoa);

        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getFoto()).isEqualTo(UPDATED_FOTO);
        assertThat(testPessoa.getFotoContentType()).isEqualTo(UPDATED_FOTO_CONTENT_TYPE);
        assertThat(testPessoa.getDtNascimento()).isEqualTo(UPDATED_DT_NASCIMENTO);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    @Transactional
    void putNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa
            .foto(UPDATED_FOTO)
            .fotoContentType(UPDATED_FOTO_CONTENT_TYPE)
            .dtNascimento(UPDATED_DT_NASCIMENTO)
            .cpf(UPDATED_CPF);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoa.getFoto()).isEqualTo(UPDATED_FOTO);
        assertThat(testPessoa.getFotoContentType()).isEqualTo(UPDATED_FOTO_CONTENT_TYPE);
        assertThat(testPessoa.getDtNascimento()).isEqualTo(UPDATED_DT_NASCIMENTO);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    @Transactional
    void fullUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa
            .nome(UPDATED_NOME)
            .foto(UPDATED_FOTO)
            .fotoContentType(UPDATED_FOTO_CONTENT_TYPE)
            .dtNascimento(UPDATED_DT_NASCIMENTO)
            .cpf(UPDATED_CPF)
            .ativo(UPDATED_ATIVO);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getFoto()).isEqualTo(UPDATED_FOTO);
        assertThat(testPessoa.getFotoContentType()).isEqualTo(UPDATED_FOTO_CONTENT_TYPE);
        assertThat(testPessoa.getDtNascimento()).isEqualTo(UPDATED_DT_NASCIMENTO);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    @Transactional
    void patchNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeDelete = pessoaRepository.findAll().size();

        // Delete the pessoa
        restPessoaMockMvc
            .perform(delete(ENTITY_API_URL_ID, pessoa.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
