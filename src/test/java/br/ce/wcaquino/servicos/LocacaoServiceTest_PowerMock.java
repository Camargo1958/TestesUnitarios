package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest_PowerMock {
	
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
		service = PowerMockito.spy(service);
	}
	
	
	@Test
	public void deveAlugarFilme() throws Exception {
	  //Cenario 
	  Usuario usuario = umUsuario().agora(); 
	  List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
	  
	  PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH,28);
//		calendar.set(Calendar.MONTH,Calendar.APRIL);
//		calendar.set(Calendar.YEAR,2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
	  //Acao 
	  Locacao locacao = service.alugarFilme(usuario,filmes); // Modo 2 com throws Exception no metodo
	  
	  //Verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//Cenario 
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017)); // adequado para metodo construtor como Date()
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH,29);
//		calendar.set(Calendar.MONTH,Calendar.APRIL);
//		calendar.set(Calendar.YEAR,2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//Acao 
		Locacao locacao = service.alugarFilme(usuario,filmes);
		
		//Verificacao 
		//boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY); 
		//Assert.assertTrue(ehSegunda); // Assert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY)); 
		//Assert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		Assert.assertThat(locacao.getDataRetorno(), caiNumaSegunda()); 
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments(); // adequado para metodos constrtures
//		PowerMockito.verifyStatic(Calendar.class, Mockito.times(2));
//		Calendar.getInstance();
		
		
	}	

	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//Acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//Verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
		
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//Cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//Acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao",filmes);
		
		//Verificacao
		Assert.assertThat(valor, is(4.0));
		
	}
	  
	}
