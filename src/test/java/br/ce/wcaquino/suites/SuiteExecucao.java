package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
//	CalculadoraTest.class,
//	CalculadoraMockTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})

public class SuiteExecucao {
	//Remova se puder

	@AfterClass
	public static void after() {
		System.out.println("After");
	}

	@BeforeClass
	public static void before() {
		System.out.println("Before");
	}
}
