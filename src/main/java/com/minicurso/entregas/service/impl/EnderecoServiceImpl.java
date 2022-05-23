package com.minicurso.entregas.service.impl;

import com.minicurso.entregas.domain.Endereco;
import com.minicurso.entregas.repository.EnderecoRepository;
import com.minicurso.entregas.service.EnderecoService;
import com.minicurso.entregas.service.dto.EnderecoDTO;
import com.minicurso.entregas.service.mapper.EnderecoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Endereco}.
 */
@Service
@Transactional
public class EnderecoServiceImpl implements EnderecoService {

    private final Logger log = LoggerFactory.getLogger(EnderecoServiceImpl.class);

    private final EnderecoRepository enderecoRepository;

    private final EnderecoMapper enderecoMapper;

    public EnderecoServiceImpl(EnderecoRepository enderecoRepository, EnderecoMapper enderecoMapper) {
        this.enderecoRepository = enderecoRepository;
        this.enderecoMapper = enderecoMapper;
    }

    @Override
    public EnderecoDTO save(EnderecoDTO enderecoDTO) {
        log.debug("Request to save Endereco : {}", enderecoDTO);
        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public EnderecoDTO update(EnderecoDTO enderecoDTO) {
        log.debug("Request to save Endereco : {}", enderecoDTO);
        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public Optional<EnderecoDTO> partialUpdate(EnderecoDTO enderecoDTO) {
        log.debug("Request to partially update Endereco : {}", enderecoDTO);

        return enderecoRepository
            .findById(enderecoDTO.getId())
            .map(existingEndereco -> {
                enderecoMapper.partialUpdate(existingEndereco, enderecoDTO);

                return existingEndereco;
            })
            .map(enderecoRepository::save)
            .map(enderecoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnderecoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Enderecos");
        return enderecoRepository.findAll(pageable).map(enderecoMapper::toDto);
    }

    public Page<EnderecoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return enderecoRepository.findAllWithEagerRelationships(pageable).map(enderecoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnderecoDTO> findOne(Long id) {
        log.debug("Request to get Endereco : {}", id);
        return enderecoRepository.findOneWithEagerRelationships(id).map(enderecoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Endereco : {}", id);
        enderecoRepository.deleteById(id);
    }
}
