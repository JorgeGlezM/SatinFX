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
public class Empleados2
{
    //Respaldo de la clase Empleados con todos los datos de la tabla en lugar de los necesarios para la tabla
	private String clave;
	public String getClave()
	{
		return this.clave;
	}
	public void setClave(String value)
	{
		this.clave = value;
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

	private String apaterno;
	public String getApaterno()
	{
		return this.apaterno;
	}
	public void setApaterno(String value)
	{
		this.apaterno = value;
	}

	private String amaterno;
	public String getAmaterno()
	{
		return this.amaterno;
	}
	public void setAmaterno(String value)
	{
		this.amaterno = value;
	}

	private String rfc;
	public String getRfc()
	{
		return this.rfc;
	}
	public void setRfc(String value)
	{
		this.rfc = value;
	}

	private String curp;
	public String getcurp()
	{
		return this.curp;
	}
	public void setCurp(String value)
	{
		this.curp = value;
	}

	private String nss;
	public String getNss()
	{
		return this.nss;
	}
	public void setNss(String value)
	{
		this.nss = value;
	}

	private String fecha_ingreso;
	public String getFecha_ingreso()
	{
		return this.fecha_ingreso;
	}
	public void setfecha_ingreso(String value)
	{
		this.fecha_ingreso = value;
	}

	private String email;
	public String getEmail()
	{
		return this.email;
	}
	public void setEmail(String value)
	{
		this.email = value;
	}

	private String activo;
	public String getActivo()
	{
		return this.activo;
	}
	public void setActivo(String value)
	{
		this.activo = value;
	}

	private String pass;
	public String getPass()
	{
		return this.pass;
	}
	public void setPass(String value)
	{
		this.pass = value;
	}

	private String repss;
	public String getRepss()
	{
		return this.repss;
	}
	public void setRepss(String value)
	{
		this.repss = value;
	}

	private String jornada;
	public String getJornada()
	{
		return this.jornada;
	}
	public void setJornada(String value)
	{
		this.jornada = value;
	}

	private String anio;
	public String getAnio()
	{
		return this.anio;
	}
	public void setAnio(String value)
	{
		this.anio = value;
	}

	private String ultimo_sueldo;
	public String getultimo_sueldo()
	{
		return this.ultimo_sueldo;
	}
	public void setultimo_sueldo(String value)
	{
		this.ultimo_sueldo = value;
	}

	private String ingreso_acumulable;
	public String getIngreso_acumulable()
	{
		return this.ingreso_acumulable;
	}
	public void setingreso_acumulable(String value)
	{
		this.ingreso_acumulable = value;
	}

	private String ingreso_no_acumulable;
	public String getingreso_no_acumulable()
	{
		return this.ingreso_no_acumulable;
	}
	public void setIngreso_no_acumulable(String value)
	{
		this.ingreso_no_acumulable = value;
	}

	private String turno;
	public String getTurno()
	{
		return this.turno;
	}
	public void setTurno(String value)
	{
		this.turno = value;
	}

	private String cedula;
	public String getCedula()
	{
		return this.cedula;
	}
	public void setCedula(String value)
	{
		this.cedula = value;
	}

	private String tipo_cedula;
	public String getTipo_cedula()
	{
		return this.tipo_cedula;
	}
	public void setTipo_cedula(String value)
	{
		this.tipo_cedula = value;
	}

	private String entidad_cedula;
	public String getEntidad_cedula()
	{
		return this.entidad_cedula;
	}
	public void setEntidad_cedula(String value)
	{
		this.entidad_cedula = value;
	}

	private String especialidad;
	public String getEspecialidad()
	{
		return this.especialidad;
	}
	public void setEspecialidad(String value)
	{
		this.especialidad = value;
	}


	public Empleados2(String clave_,String nombre_,String apaterno_,String amaterno_,String rfc_,String curp_,String nss_,String fecha_ingreso_,String email_,String activo_,String pass_,String repss_,String jornada_,String anio_,String ultimo_sueldo_,String ingreso_acumulable_,String ingreso_no_acumulable_,String turno_,String cedula_,String tipo_cedula_,String entidad_cedula_,String especialidad_)
	{
		this.clave = clave_;
		this.nombre = nombre_;
		this.apaterno = apaterno_;
		this.amaterno = amaterno_;
		this.rfc = rfc_;
		this.curp = curp_;
		this.nss = nss_;
		this.fecha_ingreso = fecha_ingreso_;
		this.email = email_;
		this.activo = activo_;
		this.pass = pass_;
		this.repss = repss_;
		this.jornada = jornada_;
		this.anio = anio_;
		this.ultimo_sueldo = ultimo_sueldo_;
		this.ingreso_acumulable = ingreso_acumulable_;
		this.ingreso_no_acumulable = ingreso_no_acumulable_;
		this.turno = turno_;
		this.cedula = cedula_;
		this.tipo_cedula = tipo_cedula_;
		this.entidad_cedula = entidad_cedula_;
		this.especialidad = especialidad_;
	}
}