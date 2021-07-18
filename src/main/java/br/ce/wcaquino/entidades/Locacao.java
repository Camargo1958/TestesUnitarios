package br.ce.wcaquino.entidades;

import java.util.Date;
import java.util.List;

public class Locacao {

	private Usuario usuario;
	private List<Filme> filmes;
	private Date dataLocacao;
	private Date dataRetorno;
	private Double valor;

	public Date getDataLocacao() {
		return dataLocacao;
	}
	public Date getDataRetorno() {
		return dataRetorno;
	}
	public List<Filme> getFilmes() {
		return filmes;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public Double getValor() {
		return valor;
	}
	public void setDataLocacao(Date dataLocacao) {
		this.dataLocacao = dataLocacao;
	}
	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}
	public void setFilmes(List<Filme> filmes) {
		this.filmes = filmes;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}

}