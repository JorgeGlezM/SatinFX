/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Jorge
 */
public class Puestos{
	private String id_puestos;
	public String getId_puestos()
	{
		return this.id_puestos;
	}
	public void setId_puestos(String value)
	{
		this.id_puestos = value;
	}

	private String descripcion;
	public String getDescripcion()
	{
		return this.descripcion;
	}
	public void setDescripcion(String value)
	{
		this.descripcion = value;
	}

	private String categoria;
	public String getCategoria()
	{
		return this.categoria;
	}
	public void setCategoria(String value)
	{
		this.categoria = value;
	}

	private String rama;
	public String getRama()
	{
		return this.rama;
	}
	public void setRama(String value)
	{
		this.rama = value;
	}


	public Puestos(String id_puestos_,String descripcion_,String categoria_,String rama_)
	{
		this.id_puestos = id_puestos_;
		this.descripcion = descripcion_;
		this.categoria = categoria_;
		this.rama = rama_;
	}
}