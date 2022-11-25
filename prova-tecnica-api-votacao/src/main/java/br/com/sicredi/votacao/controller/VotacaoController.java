package br.com.sicredi.votacao.controller;

/**
 * @author Ramiro.Alves
 */
import br.com.sicredi.votacao.model.Associado;
import br.com.sicredi.votacao.model.Pauta;
import br.com.sicredi.votacao.model.Voto;
import br.com.sicredi.votacao.repository.AssociadoRepository;
import br.com.sicredi.votacao.repository.PautaRepository;
import br.com.sicredi.votacao.repository.VotoRepository;
import br.com.sicredi.votacao.servicos.ValidarAssociado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pautas")
public class VotacaoController {
	@Autowired
	private PautaRepository pautaRepository;
	@Autowired
	private VotoRepository votoRepository;
	@Autowired
	private AssociadoRepository associadoRepository;

	// teste
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Pauta> init(@PathVariable(value = "id") Long id) {
		Optional<Pauta> pauta = pautaRepository.findById(id);

		return new ResponseEntity<Pauta>(pauta.get(), HttpStatus.OK);
	}

	// teste
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Pauta>> pauta() {

		List<Pauta> list = (List<Pauta>) pautaRepository.findAll();

		return new ResponseEntity<List<Pauta>>(list, HttpStatus.OK);
	}

	/**
	 * Cadastrar uma pauta
	 * 
	 * @param pauta
	 * @return
	 */
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Pauta> cadastrar(@RequestBody Pauta pauta) {

		Pauta pautaSalva = pautaRepository.save(pauta);

		return new ResponseEntity<Pauta>(pautaSalva, HttpStatus.OK);
	}

	/**
	 * Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar
	 * aberta por um tempo determinado na chamada de abertura ou 1 minuto por
	 * default)
	 */
	@PostMapping(value = "/{id}/start", produces = "application/json")
	public ResponseEntity<Pauta> sessaoVotacao(@PathVariable Long id,
			@RequestBody(required = false) PautaTempoInput pauta) {
		Optional<Pauta> pautaEncontrada = pautaRepository.findById(id);
		if (pautaEncontrada.isPresent()) {
			LocalDateTime dateTime = LocalDateTime.now();
			pautaEncontrada.get().setHoraDeInicioVotacao(dateTime);
			if (pauta == null) {
				pautaEncontrada.get().setFinalDaVotacao(dateTime.plusMinutes(1));
			} else {
				pautaEncontrada.get().setFinalDaVotacao(dateTime.plusSeconds(pauta.getTime()));
			}
		}
		Pauta pautaSalva = pautaRepository.save(pautaEncontrada.get());

		return new ResponseEntity<Pauta>(pautaSalva, HttpStatus.OK);
	}

	/**
	 * Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada
	 * associado é identificado por um id único e pode votar apenas uma vez por
	 * pauta)
	 */
	@PostMapping(value = "/{idpauta}/votos", produces = "application/json")
	public ResponseEntity<Voto> votacao(@RequestBody VotoInput voto, @PathVariable("idpauta") Long id) {
		Optional<Pauta> pautaLocalizada = pautaRepository.findById(id);
		if (!pautaLocalizada.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		LocalDateTime nowDT = LocalDateTime.now();
		if (LocalDateTime.now().isAfter(pautaLocalizada.get().getHoraDeInicioVotacao())
				&& LocalDateTime.now().isBefore(pautaLocalizada.get().getFinalDaVotacao())) {
			// dentro do prazo de votação
			Long idAssociado = voto.getIdAssociado();
			Optional<Associado> associadoLocalizado = associadoRepository.findById(idAssociado);
			if (associadoLocalizado.isPresent()) {
				String cpfAssociado = associadoLocalizado.get().getCpf();
				// verifica se pode votar
				if (ValidarAssociado.podeVotar(cpfAssociado)) {
					if (!pautaLocalizada.get().associadoVotou(associadoLocalizado.get())) {
						Voto novoVoto = new Voto();
						novoVoto.setVoto(voto.isVoto());
						novoVoto.setAssociado(associadoLocalizado.get());
						novoVoto.setPauta(pautaLocalizada.get());
						Voto votoSalvo = votoRepository.save(novoVoto);
						return new ResponseEntity<Voto>(votoSalvo, HttpStatus.OK);
					} else {
						// Associado já votou nesta pauta
						System.out.println("Associado já votou nesta pauta");
						return ResponseEntity.badRequest().build();
					}
				} else {
					// Associado não pode votar
					System.out.println("Associado não pode votar");
					return ResponseEntity.status(403).build();
				}
			} else {
				// Associado não encontrado
				System.out.println("Associado não encontrado");
				return ResponseEntity.badRequest().build();
			}
		} else {
			// fora do período de votação
			System.out.println("Fora do período de votação");
			System.out.println(LocalDateTime.now());
			System.out.println(LocalDateTime.now().isAfter(pautaLocalizada.get().getHoraDeInicioVotacao()));
			System.out.println(LocalDateTime.now().isBefore(pautaLocalizada.get().getFinalDaVotacao()));
			return ResponseEntity.status(403).build();
		}
	}

	/**
	 * Contabilizar os votos e dar o resultado da votação na pauta GET
	 * /pautas/<id>/result
	 */
	@GetMapping(value = "/{id}/result", produces = "application/json")
	public ResponseEntity<ResultadoPauta> resultado(@PathVariable(value = "id") Long id) {

		Optional<Pauta> pauta = pautaRepository.findById(id);
		if (!pauta.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		ResultadoPauta resultado = new ResultadoPauta();
		Pauta p = pauta.get();
		resultado.setTotal(p.getVotos().stream().count());
		resultado.setQuantidadeSim(p.getVotos().stream().filter(voto -> voto.getVoto()).count());
		resultado.setQuantidadeNao(p.getVotos().stream().filter(voto -> !voto.getVoto()).count());

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}
}

class ResultadoPauta {
	private Long total;
	private Long quantidadeSim;
	private Long quantidadeNao;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getQuantidadeSim() {
		return quantidadeSim;
	}

	public void setQuantidadeSim(Long quantidadeSim) {
		this.quantidadeSim = quantidadeSim;
	}

	public Long getQuantidadeNao() {
		return quantidadeNao;
	}

	public void setQuantidadeNao(Long quantidadeNão) {
		this.quantidadeNao = quantidadeNão;
	}
}

class VotoInput {
	private Long idAssociado;
	private boolean voto;

	public Long getIdAssociado() {
		return idAssociado;
	}

	public void setIdAssociado(Long idAssociado) {
		this.idAssociado = idAssociado;
	}

	public boolean isVoto() {
		return voto;
	}

	public void setVoto(boolean voto) {
		this.voto = voto;
	}
}

class PautaTempoInput {
	private Long time;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}