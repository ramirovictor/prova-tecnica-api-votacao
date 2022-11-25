package br.com.sicredi.votacao.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Associado implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column
  private String cpf;
  @Column
  private String nome;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "associado")
  private List<Voto> votos = new ArrayList<Voto>();
  
  @JsonBackReference
  public List<Voto> getVotos() {
    return votos;
  }
  
  public void setVotos(List<Voto> votos) {
    this.votos = votos;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getNome() {
    return nome;
  }
  
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  public String getCpf() {
    return cpf;
  }
  
  public void setCpf(String cpf) {
    this.cpf = cpf;
  }
}

