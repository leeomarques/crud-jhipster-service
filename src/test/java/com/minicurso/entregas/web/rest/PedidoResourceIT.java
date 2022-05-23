package com.minicurso.entregas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.minicurso.entregas.IntegrationTest;
import com.minicurso.entregas.domain.Endereco;
import com.minicurso.entregas.domain.Pedido;
import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.domain.Produto;
import com.minicurso.entregas.repository.PedidoRepository;
import com.minicurso.entregas.service.PedidoService;
import com.minicurso.entregas.service.criteria.PedidoCriteria;
import com.minicurso.entregas.service.dto.PedidoDTO;
import com.minicurso.entregas.service.mapper.PedidoMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PedidoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PedidoResourceIT {

    private static final Boolean DEFAULT_ENTREGUE = false;
    private static final Boolean UPDATED_ENTREGUE = true;

    private static final Instant DEFAULT_DT_ENTREGA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DT_ENTREGA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/pedidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoRepository pedidoRepositoryMock;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Mock
    private PedidoService pedidoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPedidoMockMvc;

    private Pedido pedido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createEntity(EntityManager em) {
        Pedido pedido = new Pedido().entregue(DEFAULT_ENTREGUE).dtEntrega(DEFAULT_DT_ENTREGA);
        // Add required entity
        Pessoa pessoa;
        if (TestUtil.findAll(em, Pessoa.class).isEmpty()) {
            pessoa = PessoaResourceIT.createEntity(em);
            em.persist(pessoa);
            em.flush();
        } else {
            pessoa = TestUtil.findAll(em, Pessoa.class).get(0);
        }
        pedido.setPessoa(pessoa);
        // Add required entity
        Endereco endereco;
        if (TestUtil.findAll(em, Endereco.class).isEmpty()) {
            endereco = EnderecoResourceIT.createEntity(em);
            em.persist(endereco);
            em.flush();
        } else {
            endereco = TestUtil.findAll(em, Endereco.class).get(0);
        }
        pedido.setEndereco(endereco);
        // Add required entity
        Produto produto;
        if (TestUtil.findAll(em, Produto.class).isEmpty()) {
            produto = ProdutoResourceIT.createEntity(em);
            em.persist(produto);
            em.flush();
        } else {
            produto = TestUtil.findAll(em, Produto.class).get(0);
        }
        pedido.setProduto(produto);
        return pedido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createUpdatedEntity(EntityManager em) {
        Pedido pedido = new Pedido().entregue(UPDATED_ENTREGUE).dtEntrega(UPDATED_DT_ENTREGA);
        // Add required entity
        Pessoa pessoa;
        if (TestUtil.findAll(em, Pessoa.class).isEmpty()) {
            pessoa = PessoaResourceIT.createUpdatedEntity(em);
            em.persist(pessoa);
            em.flush();
        } else {
            pessoa = TestUtil.findAll(em, Pessoa.class).get(0);
        }
        pedido.setPessoa(pessoa);
        // Add required entity
        Endereco endereco;
        if (TestUtil.findAll(em, Endereco.class).isEmpty()) {
            endereco = EnderecoResourceIT.createUpdatedEntity(em);
            em.persist(endereco);
            em.flush();
        } else {
            endereco = TestUtil.findAll(em, Endereco.class).get(0);
        }
        pedido.setEndereco(endereco);
        // Add required entity
        Produto produto;
        if (TestUtil.findAll(em, Produto.class).isEmpty()) {
            produto = ProdutoResourceIT.createUpdatedEntity(em);
            em.persist(produto);
            em.flush();
        } else {
            produto = TestUtil.findAll(em, Produto.class).get(0);
        }
        pedido.setProduto(produto);
        return pedido;
    }

    @BeforeEach
    public void initTest() {
        pedido = createEntity(em);
    }

    @Test
    @Transactional
    void createPedido() throws Exception {
        int databaseSizeBeforeCreate = pedidoRepository.findAll().size();
        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isCreated());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeCreate + 1);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getEntregue()).isEqualTo(DEFAULT_ENTREGUE);
        assertThat(testPedido.getDtEntrega()).isEqualTo(DEFAULT_DT_ENTREGA);
    }

    @Test
    @Transactional
    void createPedidoWithExistingId() throws Exception {
        // Create the Pedido with an existing ID
        pedido.setId(1L);
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        int databaseSizeBeforeCreate = pedidoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPedidos() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].entregue").value(hasItem(DEFAULT_ENTREGUE.booleanValue())))
            .andExpect(jsonPath("$.[*].dtEntrega").value(hasItem(DEFAULT_DT_ENTREGA.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPedidosWithEagerRelationshipsIsEnabled() throws Exception {
        when(pedidoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPedidoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pedidoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPedidosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pedidoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPedidoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pedidoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get the pedido
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL_ID, pedido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pedido.getId().intValue()))
            .andExpect(jsonPath("$.entregue").value(DEFAULT_ENTREGUE.booleanValue()))
            .andExpect(jsonPath("$.dtEntrega").value(DEFAULT_DT_ENTREGA.toString()));
    }

    @Test
    @Transactional
    void getPedidosByIdFiltering() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        Long id = pedido.getId();

        defaultPedidoShouldBeFound("id.equals=" + id);
        defaultPedidoShouldNotBeFound("id.notEquals=" + id);

        defaultPedidoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPedidoShouldNotBeFound("id.greaterThan=" + id);

        defaultPedidoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPedidoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPedidosByEntregueIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where entregue equals to DEFAULT_ENTREGUE
        defaultPedidoShouldBeFound("entregue.equals=" + DEFAULT_ENTREGUE);

        // Get all the pedidoList where entregue equals to UPDATED_ENTREGUE
        defaultPedidoShouldNotBeFound("entregue.equals=" + UPDATED_ENTREGUE);
    }

    @Test
    @Transactional
    void getAllPedidosByEntregueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where entregue not equals to DEFAULT_ENTREGUE
        defaultPedidoShouldNotBeFound("entregue.notEquals=" + DEFAULT_ENTREGUE);

        // Get all the pedidoList where entregue not equals to UPDATED_ENTREGUE
        defaultPedidoShouldBeFound("entregue.notEquals=" + UPDATED_ENTREGUE);
    }

    @Test
    @Transactional
    void getAllPedidosByEntregueIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where entregue in DEFAULT_ENTREGUE or UPDATED_ENTREGUE
        defaultPedidoShouldBeFound("entregue.in=" + DEFAULT_ENTREGUE + "," + UPDATED_ENTREGUE);

        // Get all the pedidoList where entregue equals to UPDATED_ENTREGUE
        defaultPedidoShouldNotBeFound("entregue.in=" + UPDATED_ENTREGUE);
    }

    @Test
    @Transactional
    void getAllPedidosByEntregueIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where entregue is not null
        defaultPedidoShouldBeFound("entregue.specified=true");

        // Get all the pedidoList where entregue is null
        defaultPedidoShouldNotBeFound("entregue.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByDtEntregaIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where dtEntrega equals to DEFAULT_DT_ENTREGA
        defaultPedidoShouldBeFound("dtEntrega.equals=" + DEFAULT_DT_ENTREGA);

        // Get all the pedidoList where dtEntrega equals to UPDATED_DT_ENTREGA
        defaultPedidoShouldNotBeFound("dtEntrega.equals=" + UPDATED_DT_ENTREGA);
    }

    @Test
    @Transactional
    void getAllPedidosByDtEntregaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where dtEntrega not equals to DEFAULT_DT_ENTREGA
        defaultPedidoShouldNotBeFound("dtEntrega.notEquals=" + DEFAULT_DT_ENTREGA);

        // Get all the pedidoList where dtEntrega not equals to UPDATED_DT_ENTREGA
        defaultPedidoShouldBeFound("dtEntrega.notEquals=" + UPDATED_DT_ENTREGA);
    }

    @Test
    @Transactional
    void getAllPedidosByDtEntregaIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where dtEntrega in DEFAULT_DT_ENTREGA or UPDATED_DT_ENTREGA
        defaultPedidoShouldBeFound("dtEntrega.in=" + DEFAULT_DT_ENTREGA + "," + UPDATED_DT_ENTREGA);

        // Get all the pedidoList where dtEntrega equals to UPDATED_DT_ENTREGA
        defaultPedidoShouldNotBeFound("dtEntrega.in=" + UPDATED_DT_ENTREGA);
    }

    @Test
    @Transactional
    void getAllPedidosByDtEntregaIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where dtEntrega is not null
        defaultPedidoShouldBeFound("dtEntrega.specified=true");

        // Get all the pedidoList where dtEntrega is null
        defaultPedidoShouldNotBeFound("dtEntrega.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByPessoaIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);
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
        pedido.setPessoa(pessoa);
        pedidoRepository.saveAndFlush(pedido);
        Long pessoaId = pessoa.getId();

        // Get all the pedidoList where pessoa equals to pessoaId
        defaultPedidoShouldBeFound("pessoaId.equals=" + pessoaId);

        // Get all the pedidoList where pessoa equals to (pessoaId + 1)
        defaultPedidoShouldNotBeFound("pessoaId.equals=" + (pessoaId + 1));
    }

    @Test
    @Transactional
    void getAllPedidosByEnderecoIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);
        Endereco endereco;
        if (TestUtil.findAll(em, Endereco.class).isEmpty()) {
            endereco = EnderecoResourceIT.createEntity(em);
            em.persist(endereco);
            em.flush();
        } else {
            endereco = TestUtil.findAll(em, Endereco.class).get(0);
        }
        em.persist(endereco);
        em.flush();
        pedido.setEndereco(endereco);
        pedidoRepository.saveAndFlush(pedido);
        Long enderecoId = endereco.getId();

        // Get all the pedidoList where endereco equals to enderecoId
        defaultPedidoShouldBeFound("enderecoId.equals=" + enderecoId);

        // Get all the pedidoList where endereco equals to (enderecoId + 1)
        defaultPedidoShouldNotBeFound("enderecoId.equals=" + (enderecoId + 1));
    }

    @Test
    @Transactional
    void getAllPedidosByProdutoIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);
        Produto produto;
        if (TestUtil.findAll(em, Produto.class).isEmpty()) {
            produto = ProdutoResourceIT.createEntity(em);
            em.persist(produto);
            em.flush();
        } else {
            produto = TestUtil.findAll(em, Produto.class).get(0);
        }
        em.persist(produto);
        em.flush();
        pedido.setProduto(produto);
        pedidoRepository.saveAndFlush(pedido);
        Long produtoId = produto.getId();

        // Get all the pedidoList where produto equals to produtoId
        defaultPedidoShouldBeFound("produtoId.equals=" + produtoId);

        // Get all the pedidoList where produto equals to (produtoId + 1)
        defaultPedidoShouldNotBeFound("produtoId.equals=" + (produtoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPedidoShouldBeFound(String filter) throws Exception {
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].entregue").value(hasItem(DEFAULT_ENTREGUE.booleanValue())))
            .andExpect(jsonPath("$.[*].dtEntrega").value(hasItem(DEFAULT_DT_ENTREGA.toString())));

        // Check, that the count call also returns 1
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPedidoShouldNotBeFound(String filter) throws Exception {
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPedido() throws Exception {
        // Get the pedido
        restPedidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido
        Pedido updatedPedido = pedidoRepository.findById(pedido.getId()).get();
        // Disconnect from session so that the updates on updatedPedido are not directly saved in db
        em.detach(updatedPedido);
        updatedPedido.entregue(UPDATED_ENTREGUE).dtEntrega(UPDATED_DT_ENTREGA);
        PedidoDTO pedidoDTO = pedidoMapper.toDto(updatedPedido);

        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getEntregue()).isEqualTo(UPDATED_ENTREGUE);
        assertThat(testPedido.getDtEntrega()).isEqualTo(UPDATED_DT_ENTREGA);
    }

    @Test
    @Transactional
    void putNonExistingPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido.entregue(UPDATED_ENTREGUE);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getEntregue()).isEqualTo(UPDATED_ENTREGUE);
        assertThat(testPedido.getDtEntrega()).isEqualTo(DEFAULT_DT_ENTREGA);
    }

    @Test
    @Transactional
    void fullUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido.entregue(UPDATED_ENTREGUE).dtEntrega(UPDATED_DT_ENTREGA);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getEntregue()).isEqualTo(UPDATED_ENTREGUE);
        assertThat(testPedido.getDtEntrega()).isEqualTo(UPDATED_DT_ENTREGA);
    }

    @Test
    @Transactional
    void patchNonExistingPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeDelete = pedidoRepository.findAll().size();

        // Delete the pedido
        restPedidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, pedido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
