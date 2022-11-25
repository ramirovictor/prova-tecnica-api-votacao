package br.com.sicredi.votacao.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.sicredi.votacao.model.Voto;

public interface VotoRepository extends CrudRepository<Voto, Long> {

}