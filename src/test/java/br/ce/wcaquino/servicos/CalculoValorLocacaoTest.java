package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = new Filme("Filme2",2,4.0);
	private static Filme filme3 = new Filme("Filme3",2,4.0);

	private static Filme filme4 = new Filme("Filme4",2,4.0);
	private static Filme filme5 = new Filme("Filme5",2,4.0);
	private static Filme filme6 = new Filme("Filme6",2,4.0);

	private static Filme filme7 = new Filme("Filme7",2,4.0);

	@Parameters(name="Cenario = {2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2), 8.0, "2 Filmes: sem desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: sem desconto"}
		});
	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}

	@InjectMocks
	private LocacaoService service;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Parameter
	public List<Filme> filmes;
	@Parameter(value=1)
	public Double valorLocacao;
	@Parameter(value=2)
	public String cenario;
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException, InterruptedException{
		//Cenario
		Usuario usuario = umUsuario().agora();

		Thread.sleep(2000);  // para melhor visualizacao dos testes em paralelo

		//Acao
		Locacao locacao = service.alugarFilme(usuario,filmes);

		//Verificacao

	    assertThat(locacao.getValor(), is(valorLocacao));

	}

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 3...");
		CalculadoraTest.ordem.append(3);
	}

	@After
	public void teardown() {
		System.out.println("finalizando 3...");
	}

}
