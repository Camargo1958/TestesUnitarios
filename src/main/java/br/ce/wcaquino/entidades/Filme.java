package br.ce.wcaquino.entidades;

public class Filme {

	private String nome;
	private Integer estoque;
	private Double precoLocacao;

	public Filme() {}

	public Filme(String nome, Integer estoque, Double precoLocacao) {
		this.nome = nome;
		this.estoque = estoque;
		this.precoLocacao = precoLocacao;
	}

	public Integer getEstoque() {
		return estoque;
	}
	public String getNome() {
		return nome;
	}
	public Double getPrecoLocacao() {
		return precoLocacao;
	}
	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setPrecoLocacao(Double precoLocacao) {
		this.precoLocacao = precoLocacao;
	}
}