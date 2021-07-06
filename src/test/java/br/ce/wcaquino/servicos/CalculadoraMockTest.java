package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.junit.Assert;
import org.junit.Before;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Mock
	private EmailService email; // nao pode ser usado Spy porque a interface nao foi implementada
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy() {
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
//		Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
//		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		Mockito.doReturn(5).when(calcSpy).somar(1,2);
		Mockito.doNothing().when(calcSpy).imprime();

		System.out.println("Mock: " + calcMock.somar(1, 2));
		System.out.println("Spy: " + calcSpy.somar(1, 2));
		
		System.out.println("Mock");
		calcMock.imprime();
		
		System.out.println("Spy");
		calcSpy.imprime();

	}
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
//		Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5); // se usar um parametro matcher todos devem ser matcher
//		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5); // se usar um parametro matcher todos devem ser matcher
		Mockito.when(calc.somar(argCapt.capture(), Mockito.anyInt())).thenReturn(5); // se usar um parametro matcher todos devem ser matcher
		
//		System.out.println(calc.somar(1, 3));
		Assert.assertEquals(5, calc.somar(1, 3));
		System.out.println(argCapt.getAllValues());
	}

}
