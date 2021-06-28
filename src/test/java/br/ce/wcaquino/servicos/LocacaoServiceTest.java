package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService service;
//	private static int contador = 0;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
//		System.out.println("Before");
		service = new LocacaoService();
//		contador++;
//		System.out.println("Testes executados: "+contador);
	}
	
//	@After
//	public void teardown() {
//		System.out.println("After");
//	}
//	
//	@BeforeClass
//	public static void setupClass() {
//		System.out.println("Before Class");
//	}
//	
//	@AfterClass
//	public static void teardownClass() {
//		System.out.println("After Class");
//	}

	@Test
	public void testeLocacao() throws Exception {
		
		//Cenário
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
	
	@Test(expected=FilmeSemEstoqueException.class) // Modo elegante
	public void testeLocacao_filmeSemEstoque() throws Exception {
		//Cenário
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Pedro Carlos");
		Filme filme = new Filme("O Corvo", 0, 3.50);
		
		//Ação
	    service.alugarFilme(usuario,filme);

	}
	
//	@Test // Modo Robusto
//	public void testeLocacao_filmeSemEstoque_2() {
//		//Cenário
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Pedro Carlos");
//		Filme filme = new Filme("O Corvo", 0, 3.50);
//		
//		//Ação
//	    try {
//			service.alugarFilme(usuario,filme);
//			Assert.fail("Deveria ter lancado uma exececao");
//		} catch (Exception e) {
//			assertThat(e.getMessage(), is("Filme sem estoque"));
//		}
//
//	}
	
//	@Test // Modo Novo
//	public void testeLocacao_filmeSemEstoque_3() throws Exception {
//		//Cenário
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Pedro Carlos");
//		Filme filme = new Filme("O Corvo", 0, 3.50);
//		
//	    exception.expect(Exception.class);
//	    exception.expectMessage("Filme sem estoque");
//	    
//		//Ação
//	    service.alugarFilme(usuario,filme);
//
//	}
	
	@Test // Modo Robusto
	public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		//Cenário
		Filme filme = new Filme("Por um punhado de dolares", 3, 6.0);
		Usuario usuario = null;
		
		//Ação
		try {
			service.alugarFilme(usuario,filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}
	
	@Test // Modo Novo
	public void testLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		//Cenário
		Filme filme = null;
		Usuario usuario = new Usuario("John Doe");
		
	    exception.expect(LocadoraException.class);
	    exception.expectMessage("Filme vazio");
	    
		//Ação
		service.alugarFilme(usuario,filme);

	}
	
	@Test
	public void testeLocacaoMultipla() throws Exception {
		//Cenário
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 6.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 5.5);
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
		Filme filme4 = new Filme("Cisco Kid", 7, 6.3);
		Filme filme5 = new Filme("Dolares na cueca", 1, 8.1);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		
		//Ação
	    List<Locacao> locacoes = service.alugarFilmes(usuario,filmes);
		
	    int i;
	    for(i=0; i<locacoes.size();i++) {
		//Verificação
	    error.checkThat(locacoes.get(i).getValor(), is(equalTo(filmes.get(i).getPrecoLocacao())));
	    error.checkThat(isMesmaData(locacoes.get(i).getDataLocacao(), new Date()), is(true));
	    error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacoes.get(i).getDataRetorno()), is(true));
	    }
	}
	
}
