package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Test
	public void testeLocacao() throws Exception {
		
		//Cenário
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Pedro Carlos");
		Filme filme = new Filme("O Corvo", 2, 3.50);
		
		//Ação
//	    Locacao locacao; // Modo 1 sem throws Exception no método
//		try {
//			locacao = service.alugarFilme(usuario,filme);
//			
//			//Verificação
//		    error.checkThat(locacao.getValor(), is(equalTo(3.5)));
//		    error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		    error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()), is(true));
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail("Excecao lancada por dados inconsistentes");
//		}
		
	    Locacao locacao = service.alugarFilme(usuario,filme); // Modo 2 com throws Exception no método
			
		//Verificação
	    error.checkThat(locacao.getValor(), is(equalTo(3.5)));
	    error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
	    error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()), is(true));

		//Verificação
	    //Assert.assertEquals(3.5, locacao.getValor(), 0.01);
	    //assertThat(locacao.getValor(), is(3.5));

	    //assertThat(locacao.getValor(), is(not(5.5)));
	    //assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
	    
	    //assertTrue(DataUtils.isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()));
	    
	}
	
	@Test(expected=Exception.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {
		//Cenário
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Pedro Carlos");
		Filme filme = new Filme("O Corvo", 0, 3.50);
		
		//Ação
	    service.alugarFilme(usuario,filme);

	}
}
