package com.minicurso.entregas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.minicurso.entregas.IntegrationTest;
import com.minicurso.entregas.domain.Endereco;
import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.repository.EnderecoRepository;
import com.minicurso.entregas.service.EnderecoService;
import com.minicurso.entregas.service.criteria.EnderecoCriteria;
import com.minicurso.entregas.service.dto.EnderecoDTO;
import com.minicurso.entregas.service.mapper.EnderecoMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EnderecoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnderecoResourceIT {

    private static final String DEFAULT_RUA = "AAAAAAAAAA";
    private static final String UPDATED_RUA = "BBBBBBBBBB";

    private static final String DEFAULT_CIDADE = "AAAAAAAAAA";
    private static final String UPDATED_CIDADE = "BBBBBBBBBB";

    private static final String DEFAULT_BAIRRO = "AAAAAAAAAA";
    private static final String UPDATED_BAIRRO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/enderecos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Mock
    private EnderecoRepository enderecoRepositoryMock;

    @Autowired
    private EnderecoMapper enderecoMapper;

    @Mock
    private EnderecoService enderecoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnderecoMockMvc;

    private Endereco endereco;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createEntity(EntityManager em) {
        Endereco endereco = new Endereco().rua(DEFAULT_RUA).cidade(DEFAULT_CIDADE).bairro(DEFAULT_BAIRRO);
        // Add required entity
        Pessoa pessoa;
        if (TestUtil.findAll(em, Pessoa.class).isEmpty()) {
            pessoa = PessoaResourceIT.createEntity(em);
            em.persist(pessoa);
            em.flush();
        } else {
            pessoa = TestUtil.findAll(em, Pessoa.class).get(0);
        }
        endereco.setPessoa(pessoa);
        return endereco;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createUpdatedEntity(EntityManager em) {
        Endereco endereco = new Endereco().rua(UPDATED_RUA).cidade(UPDATED_CIDADE).bairro(UPDATED_BAIRRO);
        // Add required entity
        Pessoa pessoa;
        if (TestUtil.findAll(em, Pessoa.class).isEmpty()) {
            pessoa = PessoaResourceIT.createUpdatedEntity(em);
            em.persist(pessoa);
            em.flush();
        } else {
            pessoa = TestUtil.findAll(em, Pessoa.class).get(0);
        }
        endereco.setPessoa(pessoa);
        return endereco;
    }

    @BeforeEach
    public void initTest() {
        endereco = createEntity(em);
    }

    @Test
    @Transactional
    void createEndereco() throws Exception {
        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();
        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isCreated());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate + 1);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getRua()).isEqualTo(DEFAULT_RUA);
        assertThat(testEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
    }

    @Test
    @Transactional
    void createEnderecoWithExistingId() throws Exception {
        // Create the Endereco with an existing ID
        endereco.setId(1L);
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRuaIsRequired() throws Exception {
        int databaseSizeBeforeTest = enderecoRepository.findAll().size();
        // set the field null
        endereco.setRua(null);

        // Create the Endereco, which fails.
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isBadRequest());

        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCidadeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enderecoRepository.findAll().size();
        // set the field null
        endereco.setCidade(null);

        // Create the Endereco, which fails.
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isBadRequest());

        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBairroIsRequired() throws Exception {
        int databaseSizeBeforeTest = enderecoRepository.findAll().size();
        // set the field null
        endereco.setBairro(null);

        // Create the Endereco, which fails.
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isBadRequest());

        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEnderecos() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(endereco.getId().intValue())))
            .andExpect(jsonPath("$.[*].rua").value(hasItem(DEFAULT_RUA)))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE)))
            .andExpect(jsonPath("$.[*].bairro").value(hasItem(DEFAULT_BAIRRO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnderecosWithEagerRelationshipsIsEnabled() throws Exception {
        when(enderecoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnderecoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(enderecoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnderecosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(enderecoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnderecoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(enderecoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get the endereco
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL_ID, endereco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(endereco.getId().intValue()))
            .andExpect(jsonPath("$.rua").value(DEFAULT_RUA))
            .andExpect(jsonPath("$.cidade").value(DEFAULT_CIDADE))
            .andExpect(jsonPath("$.bairro").value(DEFAULT_BAIRRO));
    }

    @Test
    @Transactional
    void getEnderecosByIdFiltering() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        Long id = endereco.getId();

        defaultEnderecoShouldBeFound("id.equals=" + id);
        defaultEnderecoShouldNotBeFound("id.notEquals=" + id);

        defaultEnderecoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEnderecoShouldNotBeFound("id.greaterThan=" + id);

        defaultEnderecoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEnderecoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaIsEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua equals to DEFAULT_RUA
        defaultEnderecoShouldBeFound("rua.equals=" + DEFAULT_RUA);

        // Get all the enderecoList where rua equals to UPDATED_RUA
        defaultEnderecoShouldNotBeFound("rua.equals=" + UPDATED_RUA);
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua not equals to DEFAULT_RUA
        defaultEnderecoShouldNotBeFound("rua.notEquals=" + DEFAULT_RUA);

        // Get all the enderecoList where rua not equals to UPDATED_RUA
        defaultEnderecoShouldBeFound("rua.notEquals=" + UPDATED_RUA);
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaIsInShouldWork() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua in DEFAULT_RUA or UPDATED_RUA
        defaultEnderecoShouldBeFound("rua.in=" + DEFAULT_RUA + "," + UPDATED_RUA);

        // Get all the enderecoList where rua equals to UPDATED_RUA
        defaultEnderecoShouldNotBeFound("rua.in=" + UPDATED_RUA);
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaIsNullOrNotNull() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua is not null
        defaultEnderecoShouldBeFound("rua.specified=true");

        // Get all the enderecoList where rua is null
        defaultEnderecoShouldNotBeFound("rua.specified=false");
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua contains DEFAULT_RUA
        defaultEnderecoShouldBeFound("rua.contains=" + DEFAULT_RUA);

        // Get all the enderecoList where rua contains UPDATED_RUA
        defaultEnderecoShouldNotBeFound("rua.contains=" + UPDATED_RUA);
    }

    @Test
    @Transactional
    void getAllEnderecosByRuaNotContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where rua does not contain DEFAULT_RUA
        defaultEnderecoShouldNotBeFound("rua.doesNotContain=" + DEFAULT_RUA);

        // Get all the enderecoList where rua does not contain UPDATED_RUA
        defaultEnderecoShouldBeFound("rua.doesNotContain=" + UPDATED_RUA);
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeIsEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade equals to DEFAULT_CIDADE
        defaultEnderecoShouldBeFound("cidade.equals=" + DEFAULT_CIDADE);

        // Get all the enderecoList where cidade equals to UPDATED_CIDADE
        defaultEnderecoShouldNotBeFound("cidade.equals=" + UPDATED_CIDADE);
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade not equals to DEFAULT_CIDADE
        defaultEnderecoShouldNotBeFound("cidade.notEquals=" + DEFAULT_CIDADE);

        // Get all the enderecoList where cidade not equals to UPDATED_CIDADE
        defaultEnderecoShouldBeFound("cidade.notEquals=" + UPDATED_CIDADE);
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeIsInShouldWork() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade in DEFAULT_CIDADE or UPDATED_CIDADE
        defaultEnderecoShouldBeFound("cidade.in=" + DEFAULT_CIDADE + "," + UPDATED_CIDADE);

        // Get all the enderecoList where cidade equals to UPDATED_CIDADE
        defaultEnderecoShouldNotBeFound("cidade.in=" + UPDATED_CIDADE);
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeIsNullOrNotNull() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade is not null
        defaultEnderecoShouldBeFound("cidade.specified=true");

        // Get all the enderecoList where cidade is null
        defaultEnderecoShouldNotBeFound("cidade.specified=false");
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade contains DEFAULT_CIDADE
        defaultEnderecoShouldBeFound("cidade.contains=" + DEFAULT_CIDADE);

        // Get all the enderecoList where cidade contains UPDATED_CIDADE
        defaultEnderecoShouldNotBeFound("cidade.contains=" + UPDATED_CIDADE);
    }

    @Test
    @Transactional
    void getAllEnderecosByCidadeNotContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where cidade does not contain DEFAULT_CIDADE
        defaultEnderecoShouldNotBeFound("cidade.doesNotContain=" + DEFAULT_CIDADE);

        // Get all the enderecoList where cidade does not contain UPDATED_CIDADE
        defaultEnderecoShouldBeFound("cidade.doesNotContain=" + UPDATED_CIDADE);
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroIsEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro equals to DEFAULT_BAIRRO
        defaultEnderecoShouldBeFound("bairro.equals=" + DEFAULT_BAIRRO);

        // Get all the enderecoList where bairro equals to UPDATED_BAIRRO
        defaultEnderecoShouldNotBeFound("bairro.equals=" + UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroIsNotEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro not equals to DEFAULT_BAIRRO
        defaultEnderecoShouldNotBeFound("bairro.notEquals=" + DEFAULT_BAIRRO);

        // Get all the enderecoList where bairro not equals to UPDATED_BAIRRO
        defaultEnderecoShouldBeFound("bairro.notEquals=" + UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroIsInShouldWork() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro in DEFAULT_BAIRRO or UPDATED_BAIRRO
        defaultEnderecoShouldBeFound("bairro.in=" + DEFAULT_BAIRRO + "," + UPDATED_BAIRRO);

        // Get all the enderecoList where bairro equals to UPDATED_BAIRRO
        defaultEnderecoShouldNotBeFound("bairro.in=" + UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroIsNullOrNotNull() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro is not null
        defaultEnderecoShouldBeFound("bairro.specified=true");

        // Get all the enderecoList where bairro is null
        defaultEnderecoShouldNotBeFound("bairro.specified=false");
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro contains DEFAULT_BAIRRO
        defaultEnderecoShouldBeFound("bairro.contains=" + DEFAULT_BAIRRO);

        // Get all the enderecoList where bairro contains UPDATED_BAIRRO
        defaultEnderecoShouldNotBeFound("bairro.contains=" + UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void getAllEnderecosByBairroNotContainsSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        // Get all the enderecoList where bairro does not contain DEFAULT_BAIRRO
        defaultEnderecoShouldNotBeFound("bairro.doesNotContain=" + DEFAULT_BAIRRO);

        // Get all the enderecoList where bairro does not contain UPDATED_BAIRRO
        defaultEnderecoShouldBeFound("bairro.doesNotContain=" + UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void getAllEnderecosByPessoaIsEqualToSomething() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);
        Pessoa pessoa;
        if (TestUtil.findAll(em, Pessoa.class).isEmpty()) {
            pessoa = PessoaResourceIT.createEntity(em);
            em.persist(pessoa);
            em.flush();
        } else {
            pessoa = TestUtil.findAll(em, Pessoa.class).get(0);
        }
        em.persist(pessoa);
        em.flush();
        endereco.setPessoa(pessoa);
        enderecoRepository.saveAndFlush(endereco);
        Long pessoaId = pessoa.getId();

        // Get all the enderecoList where pessoa equals to pessoaId
        defaultEnderecoShouldBeFound("pessoaId.equals=" + pessoaId);

        // Get all the enderecoList where pessoa equals to (pessoaId + 1)
        defaultEnderecoShouldNotBeFound("pessoaId.equals=" + (pessoaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnderecoShouldBeFound(String filter) throws Exception {
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(endereco.getId().intValue())))
            .andExpect(jsonPath("$.[*].rua").value(hasItem(DEFAULT_RUA)))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE)))
            .andExpect(jsonPath("$.[*].bairro").value(hasItem(DEFAULT_BAIRRO)));

        // Check, that the count call also returns 1
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnderecoShouldNotBeFound(String filter) throws Exception {
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEndereco() throws Exception {
        // Get the endereco
        restEnderecoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Update the endereco
        Endereco updatedEndereco = enderecoRepository.findById(endereco.getId()).get();
        // Disconnect from session so that the updates on updatedEndereco are not directly saved in db
        em.detach(updatedEndereco);
        updatedEndereco.rua(UPDATED_RUA).cidade(UPDATED_CIDADE).bairro(UPDATED_BAIRRO);
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(updatedEndereco);

        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getRua()).isEqualTo(UPDATED_RUA);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void putNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco.bairro(UPDATED_BAIRRO);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getRua()).isEqualTo(DEFAULT_RUA);
        assertThat(testEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void fullUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco.rua(UPDATED_RUA).cidade(UPDATED_CIDADE).bairro(UPDATED_BAIRRO);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getRua()).isEqualTo(UPDATED_RUA);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
    }

    @Test
    @Transactional
    void patchNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Create the Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeDelete = enderecoRepository.findAll().size();

        // Delete the endereco
        restEnderecoMockMvc
            .perform(delete(ENTITY_API_URL_ID, endereco.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
