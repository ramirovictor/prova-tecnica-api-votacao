package br.com.sicredi.votacao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.sicredi.votacao.model.Pauta;

@Repository
public interface PautaRepository extends CrudRepository<Pauta, Long> {

}