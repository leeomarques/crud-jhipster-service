package com.minicurso.entregas.repository;

import com.minicurso.entregas.domain.Endereco;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Endereco entity.
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long>, JpaSpecificationExecutor<Endereco> {
    default Optional<Endereco> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Endereco> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Endereco> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct endereco from Endereco endereco left join fetch endereco.pessoa",
        countQuery = "select count(distinct endereco) from Endereco endereco"
    )
    Page<Endereco> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct endereco from Endereco endereco left join fetch endereco.pessoa")
    List<Endereco> findAllWithToOneRelationships();

    @Query("select endereco from Endereco endereco left join fetch endereco.pessoa where endereco.id =:id")
    Optional<Endereco> findOneWithToOneRelationships(@Param("id") Long id);
}
