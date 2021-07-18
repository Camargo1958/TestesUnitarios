package br.ce.wcaquino.daos;

import java.util.List;

import br.ce.wcaquino.entidades.Locacao;

public class LocacaoDAOFake implements LocacaoDAO {

	@Override
	public List<Locacao> obterLocacoesPendentes() {
		return null;
	}

	@Override
	public void salvar(Locacao locacao) {
	}

}
