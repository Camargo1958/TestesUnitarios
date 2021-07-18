import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void Test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);

		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(5), i2);
		Assert.assertEquals(i, i2.intValue());

		Assert.assertEquals("bola", "bola");
		Assert.assertNotEquals("bola", "casa");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));

		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Usuario u3 = u2;
		Usuario u4 = null;

		Assert.assertEquals(u1, u2); // usando metodo equals da classe Usuario
		Assert.assertSame(u2, u3); // mesma instancia?
		Assert.assertNotSame(u1, u2);
		Assert.assertTrue(u4 == null);
		Assert.assertNull(u4);
		Assert.assertNotNull(u1);

		//Assert.assertEquals("Erro de cmparacao: ", 1, 2);

	}

}
