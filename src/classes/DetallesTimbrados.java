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
public class DetallesTimbrados {
    String id,rfc,nombre,percepciones,deducciones,fechapago;

    public DetallesTimbrados(String id, String rfc, String nombre, String percepciones, String deducciones, String pagado) {
        this.id = id;
        this.rfc = rfc;
        this.nombre = nombre;
        this.percepciones = percepciones;
        this.deducciones = deducciones;
        this.fechapago = pagado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPercepciones() {
        return percepciones;
    }

    public void setPercepciones(String percepciones) {
        this.percepciones = percepciones;
    }

    public String getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(String deducciones) {
        this.deducciones = deducciones;
    }

    public String getFechapago() {
        return fechapago;
    }

    public void setFechapago(String fechapago) {
        this.fechapago = fechapago;
    }


}
