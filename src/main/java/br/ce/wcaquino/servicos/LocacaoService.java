package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) {
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}
	
	public static void main(String[] args) {
		
	}

	@Test
	public void Teste() {
		
		//Cenário
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Pedro Carlos");
		Filme filme = new Filme("O Corvo", 2, 3.50);
		
		//Ação
	    Locacao locacao = service.alugarFilme(usuario,filme);
		
		//Verificação
	    Assert.assertTrue(locacao.getValor() ==3.5);
	    Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
	    Assert.assertTrue(DataUtils.isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()));
		
		
	}
}