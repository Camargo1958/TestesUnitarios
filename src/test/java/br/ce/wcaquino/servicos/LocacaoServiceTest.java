package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
//import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
//import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void deveAlugarFilme() throws Exception {
	  //Cenario 
	  Usuario usuario = umUsuario().agora(); 
	  List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
	  
	  PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
	  
	  //Acao 
	  Locacao locacao = service.alugarFilme(usuario,filmes); // Modo 2 com throws Exception no metodo
	  
	  //Verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class) // Modo elegante 
	public void naoDeveAlugarFilmeSemEstoque() throws Exception { 
	  //Cenario 
	  Usuario usuario = umUsuario().agora();
	  List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora()); 
	  //Acao
	  service.alugarFilme(usuario, filmes); 
	}
	
	@Test // Modo Robusto 
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException { 
	  //Cenario 
	  List<Filme> filmes = Arrays.asList(umFilme().agora());
	  Usuario usuario = null;
	  //Acao
	  try {
		  	service.alugarFilme(usuario,filmes);
		  	Assert.fail();
	  } catch(LocadoraException e) {
		  assertThat(e.getMessage(), is("Usuario vazio")); 
	  	} 
	}
	
	@Test // Modo Novo 
	public void naoDeveAlugarSemFilme() throws FilmeSemEstoqueException, LocadoraException { 
	  //Cenario
	  List<Filme> filmes = null;
	  Usuario usuario = umUsuario().agora();
	  
	  exception.expect(LocadoraException.class);
	  exception.expectMessage("Filme vazio");
	  //Acao
	  service.alugarFilme(usuario,filmes); 
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		// Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)); 
		//Cenario 
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017)); 
		//Acao 
		Locacao locacao = service.alugarFilme(usuario,filmes); 
		//Verificacao 
		//boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY); 
		//Assert.assertTrue(ehSegunda); // Assert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY)); 
		//Assert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		Assert.assertThat(locacao.getDataRetorno(), caiNumaSegunda()); 
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
	}	
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
	  //Cenario
	  Usuario usuario = umUsuario().agora();
	  //Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
	  List<Filme> filmes = Arrays.asList(umFilme().agora());
	  
	  when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
	  
	  //Acao
	  try { 
		  	service.alugarFilme(usuario, filmes);
		  	//Verificacao
		  	Assert.fail();
	  } catch (LocadoraException e) {
		  	Assert.assertThat(e.getMessage(), is("Usuario negativado"));
	  }
	  
	  verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){ 
	  //Cenario
      Usuario usuario = umUsuario().agora();
      Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
      Usuario usuario3 = umUsuario().comNome("Outro Usuario atrasado").agora();
      List<Locacao> locacoes = Arrays.asList( 
    		  umLocacao().atrasado().comUsuario(usuario).agora(),
    		  umLocacao().comUsuario(usuario2).agora(),
    		  umLocacao().atrasado().comUsuario(usuario3).agora(),
    		  umLocacao().atrasado().comUsuario(usuario3).agora());
	  when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
	  
	  //Acao
	  service.notificarAtrasos();
	  
	  //Verificacao
	  verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
	  verify(emailService).notificarAtraso(usuario);
	  verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
	  verify(emailService, never()).notificarAtraso(usuario2);
	  verifyNoMoreInteractions(emailService);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception{ 
		//Cenario
		Usuario usuario = umUsuario().agora(); 
		List<Filme> filmes = Arrays.asList(umFilme().agora());
	  
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));
	  
		//Verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
	  
		//Acao
		service.alugarFilme(usuario, filmes);
	  
	}
	
	@Test
	public void deveProrrogarUmaLocacao(){
		//Cenario 
		Locacao locacao = umLocacao().agora();
	  
		//Acao
		service.prorrogarLocacao(locacao, 3);
	  
		//Verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
	  
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3)); 
	}
	 
	
	
	@Test
	public void devemosPagar75PctNoFilme3() throws Exception {
		//Cenario
		Usuario usuario = umUsuario().agora(); 
		Filme filme1 = new Filme("Por um punhado de dolares", 3, 4.0);
		Filme filme2 = new Filme("A volta dos que nao foram", 4, 4.0); 
		Filme filme3 = new Filme("Poeiras em alto mar", 6, 4.0);
	  
		List<Filme> filmes = Arrays.asList(filme1, filme2, filme3);
	  
		//Acao
		Locacao locacao = service.alugarFilme(usuario,filmes);
	  
		//List<Filme> filmesLocados = locacao.getFilmes();
	  
		//Verificacao
		int i;
		Double valorTotal = 0d;
		Double precoLocacaoFilme = 0d;
		for(i=0; i<filmes.size(); i++) {
			precoLocacaoFilme =
			filmes.get(i).getPrecoLocacao();
			if(i==2) precoLocacaoFilme = precoLocacaoFilme * 0.75;
			valorTotal = valorTotal + precoLocacaoFilme;
			}
	  
		error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	  
	}
	  
	/*
	 * @Test public void devemosPagar50PctNoFilme4() throws Exception { //Cenario
	 * Usuario usuario = umUsuario().agora(); Filme filme1 = new
	 * Filme("Por um punhado de dolares", 3, 4.0); Filme filme2 = new
	 * Filme("A volta dos que nao foram", 4, 4.0); Filme filme3 = new
	 * Filme("Poeiras em alto mar", 6, 4.0); Filme filme4 = new Filme("Filme 4", 2,
	 * 4.0);
	 * 
	 * List<Filme> filmes = new ArrayList<Filme>(); filmes.add(filme1);
	 * filmes.add(filme2); filmes.add(filme3); filmes.add(filme4);
	 * 
	 * //Acao Locacao locacao = service.alugarFilme(usuario,filmes);
	 * 
	 * List<Filme> filmesLocados = locacao.getFilmes();
	 * 
	 * //Verificacao int i; Double valorTotal = 0d; Double precoLocacaoFilme = 0d;
	 * for(i=0; i<filmes.size(); i++) { precoLocacaoFilme =
	 * filmes.get(i).getPrecoLocacao(); if(i==2) precoLocacaoFilme =
	 * precoLocacaoFilme * 0.75; if(i==3) precoLocacaoFilme = precoLocacaoFilme *
	 * 0.5; valorTotal = valorTotal + precoLocacaoFilme; }
	 * 
	 * error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	 * 
	 * }
	 */
	  
	/*
	 * @Test public void devemosPagar25PctNoFilme5() throws Exception { //Cenario
	 * Usuario usuario = umUsuario().agora(); Filme filme1 = new
	 * Filme("Por um punhado de dolares", 3, 4.0); Filme filme2 = new
	 * Filme("A volta dos que nao foram", 4, 4.0); Filme filme3 = new
	 * Filme("Poeiras em alto mar", 6, 4.0); Filme filme4 = new Filme("Filme 4", 2,
	 * 4.0); Filme filme5 = new Filme("Filme 5", 1, 4.0);
	 * 
	 * List<Filme> filmes = new ArrayList<Filme>(); filmes.add(filme1);
	 * filmes.add(filme2); filmes.add(filme3); filmes.add(filme4);
	 * filmes.add(filme5);
	 * 
	 * //Acao Locacao locacao = service.alugarFilme(usuario,filmes);
	 * 
	 * List<Filme> filmesLocados = locacao.getFilmes();
	 * 
	 * //Verificacao int i; Double valorTotal = 0d; Double precoLocacaoFilme = 0d;
	 * for(i=0; i<filmes.size(); i++) { precoLocacaoFilme =
	 * filmes.get(i).getPrecoLocacao(); if(i==2) precoLocacaoFilme =
	 * precoLocacaoFilme * 0.75; if(i==3) precoLocacaoFilme = precoLocacaoFilme *
	 * 0.5; if(i==4) precoLocacaoFilme = precoLocacaoFilme * 0.25; valorTotal =
	 * valorTotal + precoLocacaoFilme; }
	 * 
	 * error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	 * 
	 * }
	 */
	  
	/*
	 * @Test public void devemosPagar0PctNoFilme6() throws Exception { //Cenario
	 * Usuario usuario = umUsuario().agora(); Filme filme1 = new
	 * Filme("Por um punhado de dolares", 3, 4.0); Filme filme2 = new
	 * Filme("A volta dos que nao foram", 4, 4.0); Filme filme3 = new
	 * Filme("Poeiras em alto mar", 6, 4.0); Filme filme4 = new Filme("Filme 4", 2,
	 * 4.0); Filme filme5 = new Filme("Filme 5", 1, 4.0); Filme filme6 = new
	 * Filme("Filme 6", 7, 4.0);
	 * 
	 * List<Filme> filmes = new ArrayList<Filme>(); filmes.add(filme1);
	 * filmes.add(filme2); filmes.add(filme3); filmes.add(filme4);
	 * filmes.add(filme5); filmes.add(filme6);
	 * 
	 * //Acao Locacao locacao = service.alugarFilme(usuario,filmes);
	 * 
	 * List<Filme> filmesLocados = locacao.getFilmes();
	 * 
	 * //Verificacao int i; Double valorTotal = 0d; Double precoLocacaoFilme = 0d;
	 * for(i=0; i<filmes.size(); i++) { precoLocacaoFilme =
	 * filmes.get(i).getPrecoLocacao(); if(i==2) precoLocacaoFilme =
	 * precoLocacaoFilme * 0.75; if(i==3) precoLocacaoFilme = precoLocacaoFilme *
	 * 0.5; if(i==4) precoLocacaoFilme = precoLocacaoFilme * 0.25; if(i==5)
	 * precoLocacaoFilme = 0d; valorTotal = valorTotal + precoLocacaoFilme; }
	 * 
	 * error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	 * 
	 * }
	 */	  
	  
	/*
	 * @Test public void deveAlugarMultiplosFilmes() throws Exception {
	 * Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(),
	 * Calendar.SATURDAY));
	 * 
	 * //Cenario Usuario usuario = umUsuario().agora(); Filme filme1 =
	 * umFilme().comValor(6.0).agora(); Filme filme2 =
	 * umFilme().comValor(5.5).agora(); // Filme filme3 = new
	 * Filme("Poeiras em alto mar", 6, 4.0); // Filme filme4 = new
	 * Filme("Cisco Kid", 7, 6.3); // Filme filme5 = new Filme("Dolares na cueca",
	 * 1, 8.1);
	 * 
	 * List<Filme> filmes = new ArrayList<Filme>(); filmes.add(filme1);
	 * filmes.add(filme2); // filmes.add(filme3); // filmes.add(filme4); //
	 * filmes.add(filme5);
	 * 
	 * //Acao Locacao locacao = service.alugarFilme(usuario,filmes);
	 * 
	 * List<Filme> filmesLocados = locacao.getFilmes();
	 * 
	 * //Verificacao int i; Double valorTotal = 0d; for(i=0; i<filmes.size(); i++) {
	 * valorTotal = valorTotal + filmes.get(i).getPrecoLocacao();
	 * error.checkThat(filmes.get(i).getNome() == filmesLocados.get(i).getNome(),
	 * is(true)); error.checkThat(filmes.get(i).getEstoque() ==
	 * filmesLocados.get(i).getEstoque(), is(true));
	 * error.checkThat(filmes.get(i).getPrecoLocacao() ==
	 * filmesLocados.get(i).getPrecoLocacao(), is(true)); }
	 * 
	 * error.checkThat(locacao.getValor(), is(equalTo(valorTotal)));
	 * error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
	 * error.checkThat(isMesmaData(DataUtils.obterDataComDiferencaDias(1),
	 * locacao.getDataRetorno()), is(true));
	 * 
	 * }
	 */	  
	 
	}
