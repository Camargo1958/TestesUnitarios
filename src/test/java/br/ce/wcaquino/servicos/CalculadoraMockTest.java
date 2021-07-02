package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
//		Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5); // se usar um parametro matcher todos devem ser matcher
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5); // se usar um parametro matcher todos devem ser matcher

		System.out.println(calc.somar(1, 3));
		
	}

}
