package br.com.sicredi.votacao.model;

//import com.fasterxml.jackson.annotation.JsonBackReference;
//import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pauta implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String assunto;
	private LocalDateTime horaDeInicioVotacao;
	private LocalDateTime finalDaVotacao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pauta")
	private List<Voto> votos = new ArrayList<Voto>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public LocalDateTime getHoraDeInicioVotacao() {
		return horaDeInicioVotacao;
	}

	public void setHoraDeInicioVotacao(LocalDateTime horaDeInicioVotacao) {
		this.horaDeInicioVotacao = horaDeInicioVotacao;
	}

	public LocalDateTime getFinalDaVotacao() {
		return finalDaVotacao;
	}

	public void setFinalDaVotacao(LocalDateTime finalDaVotacao) {
		this.finalDaVotacao = finalDaVotacao;
	}

	public List<Voto> getVotos() {
		return votos;
	}

	public void setVotos(List<Voto> votos) {
		this.votos = votos;
	}

	public boolean associadoVotou(Associado associado) {
		boolean associadoVotou = this.votos.stream().filter(voto -> voto.getAssociado().equals(associado)).count() != 0;

		return associadoVotou;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pauta other = (Pauta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
