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
public class Bancos {
        private String id;
	public String getId()
	{
		return this.id;
	}
	public void setId(String value)
	{
		this.id = value;
	}

	private String nombre;
	public String getNombre()
	{
		return this.nombre;
	}
	public void setNombre(String value)
	{
		this.nombre = value;
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


	public Bancos(String id,String nombre,String descripcion)
	{
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}
    
}
