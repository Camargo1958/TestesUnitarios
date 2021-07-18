package br.ce.wcaquino.daos;

import java.util.List;

import br.ce.wcaquino.entidades.Locacao;

public interface LocacaoDAO {

	public List<Locacao> obterLocacoesPendentes();

	public void salvar(Locacao locacao);

}
