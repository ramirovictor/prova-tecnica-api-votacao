package br.com.sicredi.votacao.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Voto implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@JsonBackReference
	@ManyToOne(optional = false)
	@JoinColumn(name = "associado_id", referencedColumnName = "id")
	private Associado associado;
	@JsonBackReference
	@ManyToOne(optional = false)
	@JoinColumn(name = "pauta_id", referencedColumnName = "id")
	private Pauta pauta;
	private boolean voto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Associado getAssociado() {
		return associado;
	}

	public void setAssociado(Associado associado) {
		this.associado = associado;
	}

	public Pauta getPauta() {
		return pauta;
	}

	public void setPauta(Pauta pauta) {
		this.pauta = pauta;
	}

	public boolean getVoto() {
		return voto;
	}

	public void setVoto(boolean voto) {
		this.voto = voto;
	}
}
