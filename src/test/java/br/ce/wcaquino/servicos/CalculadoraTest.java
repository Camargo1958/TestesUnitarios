package br.ce.wcaquino.servicos;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

//@RunWith(ParallelRunner.class)
public class CalculadoraTest {

	public static StringBuffer ordem = new StringBuffer();

	@AfterClass
	public static void tearDownClass() {
		System.out.println(ordem.toString());
	}

	private Calculadora calc;

	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException{
		//cenario
		int a = 6;
		int b = 3;
		//Calculadora calc = new Calculadora();

		//acao
		int resultado = calc.dividir(a, b);

		//verificacao
		Assert.assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 0;
		//Calculadora calc = new Calculadora();

		//acao
		calc.dividir(a, b);

	}

	@Test
	public void deveSomarDoisValores(){
		//cenario
		int a = 5;
		int b = 3;
		//Calculadora calc = new Calculadora();

		//acao
		int resultado = calc.somar(a, b);

		//verificacao
		Assert.assertEquals(8, resultado);
	}

	@Test
	public void deveSubtrairDoisValores(){
		//cenario
		int a = 7;
		int b = 3;
		//Calculadora calc = new Calculadora();

		//acao
		int resultado = calc.subtrair(a, b);

		//verificacao
		Assert.assertEquals(4, resultado);
	}

	@Before
	public void setup(){
		calc = new Calculadora();
		System.out.println("iniciando...");
		ordem.append("1");
	}

	@After
	public void teardown() {
		System.out.println("finalizando...");
	}


}
