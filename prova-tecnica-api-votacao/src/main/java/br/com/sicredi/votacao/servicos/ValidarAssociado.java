package br.com.sicredi.votacao.servicos;

/**
 * @author Ramiro.Alves
 */
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

public class ValidarAssociado {

	static RestTemplate restTemplate = new RestTemplate();

	public static boolean podeVotar(String cpf) {
		String uriTemplate = "https://user-info.herokuapp.com/users/%s";
		ResponseValidacao data = restTemplate.getForObject(String.format(uriTemplate, cpf), ResponseValidacao.class);
		return data.getStatus().equals("ABLE_TO_VOTE");
	}
}

class ResponseValidacao implements Serializable {
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "{" + "Status='" + status + '\'' + '}';
	}
}
