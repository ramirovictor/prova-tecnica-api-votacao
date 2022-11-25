package br.com.sicredi.votacao.repository;

import br.com.sicredi.votacao.model.Associado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociadoRepository extends CrudRepository<Associado, Long> {
}
