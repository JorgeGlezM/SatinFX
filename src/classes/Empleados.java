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
public class Empleados
{
    String nombres,aPaterno,aMaterno,fecha;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getaPaterno() {
        return aPaterno;
    }

    public void setaPaterno(String aPaterno) {
        this.aPaterno = aPaterno;
    }

    public String getaMaterno() {
        return aMaterno;
    }

    public void setaMaterno(String aMaterno) {
        this.aMaterno = aMaterno;
    }
	private String Clave;
	public String getClave()
	{
		return this.Clave;
	}
	public void setClave(String value)
	{
		this.Clave = value;
	}

	private String Nombre;
	public String getNombre()
	{
		return this.Nombre;
	}
	public void setNombre(String value)
	{
		this.Nombre = value;
	}

	private String RFC;
	public String getRFC()
	{
		return this.RFC;
	}
	public void setRFC(String value)
	{
		this.RFC = value;
	}

	private String CURP;
	public String getCURP()
	{
		return this.CURP;
	}
	public void setCURP(String value)
	{
		this.CURP = value;
	}

	private String NSS;
	public String getNSS()
	{
		return this.NSS;
	}
	public void setNSS(String value)
	{
		this.NSS = value;
	}

	private String Correo;
	public String getCorreo()
	{
		return this.Correo;
	}
	public void setCorreo(String value)
	{
		this.Correo = value;
	}

	public Empleados(String Clave_,String Nombre_,String RFC_,String CURP_,String NSS_,String Correo_)
	{
		this.Clave = Clave_;
		this.Nombre = Nombre_;
		this.RFC = RFC_;
		this.CURP = CURP_;
		this.NSS = NSS_;
		this.Correo = Correo_;
	}
        
        public Empleados(String clave,String aPaterno,String aMaterno,String nombres,String RFC,String CURP, String NSS, String fecha)
	{
		this.Clave = clave;
                this.aPaterno=aPaterno;
                this.aMaterno=aMaterno;
		this.nombres = nombres;
		this.RFC = RFC;
		this.CURP = CURP;
		this.NSS = NSS;
		this.fecha = fecha;
	}

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}