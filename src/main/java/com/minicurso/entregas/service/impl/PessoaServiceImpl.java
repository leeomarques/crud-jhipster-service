package com.minicurso.entregas.service.impl;

import com.minicurso.entregas.domain.Pessoa;
import com.minicurso.entregas.repository.PessoaRepository;
import com.minicurso.entregas.service.PessoaService;
import com.minicurso.entregas.service.dto.PessoaDTO;
import com.minicurso.entregas.service.mapper.PessoaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Pessoa}.
 */
@Service
@Transactional
public class PessoaServiceImpl implements PessoaService {

    private final Logger log = LoggerFactory.getLogger(PessoaServiceImpl.class);

    private final PessoaRepository pessoaRepository;

    private final PessoaMapper pessoaMapper;

    public PessoaServiceImpl(PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    @Override
    public PessoaDTO save(PessoaDTO pessoaDTO) {
        log.debug("Request to save Pessoa : {}", pessoaDTO);
        Pessoa pessoa = pessoaMapper.toEntity(pessoaDTO);
        pessoa = pessoaRepository.save(pessoa);
        return pessoaMapper.toDto(pessoa);
    }

    @Override
    public PessoaDTO update(PessoaDTO pessoaDTO) {
        log.debug("Request to save Pessoa : {}", pessoaDTO);
        Pessoa pessoa = pessoaMapper.toEntity(pessoaDTO);
        pessoa = pessoaRepository.save(pessoa);
        return pessoaMapper.toDto(pessoa);
    }

    @Override
    public Optional<PessoaDTO> partialUpdate(PessoaDTO pessoaDTO) {
        log.debug("Request to partially update Pessoa : {}", pessoaDTO);

        return pessoaRepository
            .findById(pessoaDTO.getId())
            .map(existingPessoa -> {
                pessoaMapper.partialUpdate(existingPessoa, pessoaDTO);

                return existingPessoa;
            })
            .map(pessoaRepository::save)
            .map(pessoaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PessoaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pessoas");
        return pessoaRepository.findAll(pageable).map(pessoaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PessoaDTO> findOne(Long id) {
        log.debug("Request to get Pessoa : {}", id);
        return pessoaRepository.findById(id).map(pessoaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Pessoa : {}", id);
        pessoaRepository.deleteById(id);
    }
}
