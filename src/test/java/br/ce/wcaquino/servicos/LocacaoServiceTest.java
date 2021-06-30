package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService service;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	

	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//Cen�rio
		Usuario usuario = new Usuario("Pedro Carlos");
		List<Filme> filmes = Arrays.asList(new Filme("O Corvo", 2, 3.50));
		
		//A��o
	    Locacao locacao = service.alugarFilme(usuario,filmes); // Modo 2 com throws Exception no m�todo
			
		//Verifica��o
	    error.checkThat(locacao.getValor(), is(equalTo(3.5)));
	    error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
	    error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()), is(true));
    
	}
	
	@Test(expected=FilmeSemEstoqueException.class) // Modo elegante
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		//Cen�rio
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Pedro Carlos");
		List<Filme> filmes = Arrays.asList(new Filme("O Corvo", 0, 3.50));
		
		//A��o
	    service.alugarFilme(usuario,filmes);

	}
	
	@Test // Modo Robusto
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//Cen�rio
		List<Filme> filmes = Arrays.asList(new Filme("Por um punhado de dolares", 3, 6.0));
		Usuario usuario = null;
		
		//A��o
		try {
			service.alugarFilme(usuario,filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}
	
	@Test // Modo Novo
	public void naoDeveAlugarSemFilmeDefinido() throws FilmeSemEstoqueException, LocadoraException {
		//Cen�rio
		List<Filme> filmes = null;
		Usuario usuario = new Usuario("John Doe");
		
	    exception.expect(LocadoraException.class);
	    exception.expectMessage("Filme vazio");
	    
		//A��o
		service.alugarFilme(usuario,filmes);

	}
	
	@Test
	public void deveAlugarMultiplosFilmes() throws Exception {
		//Cen�rio
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 6.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 5.5);
//		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
//		Filme filme4 = new Filme("Cisco Kid", 7, 6.3);
//		Filme filme5 = new Filme("Dolares na cueca", 1, 8.1);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
//		filmes.add(filme3);
//		filmes.add(filme4);
//		filmes.add(filme5);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		List<Filme> filmesLocados = locacao.getFilmes();
		
		//Verifica��o
		int i;
	    Double valorTotal = 0d;
	    for(i=0; i<filmes.size(); i++) {
	    	valorTotal = valorTotal + filmes.get(i).getPrecoLocacao();
	    	error.checkThat(filmes.get(i).getNome() == filmesLocados.get(i).getNome(), is(true));
	    	error.checkThat(filmes.get(i).getEstoque() == filmesLocados.get(i).getEstoque(), is(true));
	    	error.checkThat(filmes.get(i).getPrecoLocacao() == filmesLocados.get(i).getPrecoLocacao(), is(true));
	    }
	    
	    error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	    error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
	    error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1), locacao.getDataRetorno()), is(true));
	    
	}
	
	@Test
	public void devemosPagar75PctNoFilme3() throws Exception {
		//Cen�rio
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 4.0);
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		List<Filme> filmesLocados = locacao.getFilmes();
		
		//Verifica��o
		int i;
	    Double valorTotal = 0d;
	    Double precoLocacaoFilme = 0d;
	    for(i=0; i<filmes.size(); i++) {
	    	precoLocacaoFilme = filmes.get(i).getPrecoLocacao();
	    	if(i==2) precoLocacaoFilme = precoLocacaoFilme * 0.75;
	    	valorTotal = valorTotal + precoLocacaoFilme;
	    }
	    
	    error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	    
	}
	
	@Test
	public void devemosPagar50PctNoFilme4() throws Exception {
		//Cen�rio
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 4.0);
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
		Filme filme4 = new Filme("Filme 4", 2, 4.0);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		List<Filme> filmesLocados = locacao.getFilmes();
		
		//Verifica��o
		int i;
	    Double valorTotal = 0d;
	    Double precoLocacaoFilme = 0d;
	    for(i=0; i<filmes.size(); i++) {
	    	precoLocacaoFilme = filmes.get(i).getPrecoLocacao();
	    	if(i==2) precoLocacaoFilme = precoLocacaoFilme * 0.75;
	    	if(i==3) precoLocacaoFilme = precoLocacaoFilme * 0.5;
	    	valorTotal = valorTotal + precoLocacaoFilme;
	    }
	    
	    error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	    
	}
	
	@Test
	public void devemosPagar25PctNoFilme5() throws Exception {
		//Cen�rio
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 4.0);
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
		Filme filme4 = new Filme("Filme 4", 2, 4.0);
		Filme filme5 = new Filme("Filme 5", 1, 4.0);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		List<Filme> filmesLocados = locacao.getFilmes();
		
		//Verifica��o
		int i;
	    Double valorTotal = 0d;
	    Double precoLocacaoFilme = 0d;
	    for(i=0; i<filmes.size(); i++) {
	    	precoLocacaoFilme = filmes.get(i).getPrecoLocacao();
	    	if(i==2) precoLocacaoFilme = precoLocacaoFilme * 0.75;
	    	if(i==3) precoLocacaoFilme = precoLocacaoFilme * 0.5;
	    	if(i==4) precoLocacaoFilme = precoLocacaoFilme * 0.25;
	    	valorTotal = valorTotal + precoLocacaoFilme;
	    }
	    
	    error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	    
	}
	
	@Test
	public void devemosPagar0PctNoFilme6() throws Exception {
		//Cen�rio
		Usuario usuario = new Usuario("John Wayne");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 4.0);
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
		Filme filme4 = new Filme("Filme 4", 2, 4.0);
		Filme filme5 = new Filme("Filme 5", 1, 4.0);
		Filme filme6 = new Filme("Filme 6", 7, 4.0);
		
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);
		filmes.add(filme4);
		filmes.add(filme5);
		filmes.add(filme6);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		List<Filme> filmesLocados = locacao.getFilmes();
		
		//Verifica��o
		int i;
	    Double valorTotal = 0d;
	    Double precoLocacaoFilme = 0d;
	    for(i=0; i<filmes.size(); i++) {
	    	precoLocacaoFilme = filmes.get(i).getPrecoLocacao();
	    	if(i==2) precoLocacaoFilme = precoLocacaoFilme * 0.75;
	    	if(i==3) precoLocacaoFilme = precoLocacaoFilme * 0.5;
	    	if(i==4) precoLocacaoFilme = precoLocacaoFilme * 0.25;
	    	if(i==5) precoLocacaoFilme = 0d;
	    	valorTotal = valorTotal + precoLocacaoFilme;
	    }
	    
	    error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	    
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//Cen�rio
		Usuario usuario = new Usuario("John Rutherford");
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(filme1);
		
		//A��o
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		//Verifica��o
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
	}
}
