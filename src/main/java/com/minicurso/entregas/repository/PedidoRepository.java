package com.minicurso.entregas.repository;

import com.minicurso.entregas.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pedido entity.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {
    default Optional<Pedido> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Pedido> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Pedido> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct pedido from Pedido pedido left join fetch pedido.pessoa left join fetch pedido.endereco left join fetch pedido.produto",
        countQuery = "select count(distinct pedido) from Pedido pedido"
    )
    Page<Pedido> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct pedido from Pedido pedido left join fetch pedido.pessoa left join fetch pedido.endereco left join fetch pedido.produto"
    )
    List<Pedido> findAllWithToOneRelationships();

    @Query(
        "select pedido from Pedido pedido left join fetch pedido.pessoa left join fetch pedido.endereco left join fetch pedido.produto where pedido.id =:id"
    )
    Optional<Pedido> findOneWithToOneRelationships(@Param("id") Long id);
}
