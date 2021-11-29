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
public class Deducciones {
    String tipoDeduccion,clave,concepto,importe;

    public String getTipoDeduccion() {
        return tipoDeduccion;
    }

    public void setTipoDeduccion(String tipoDeduccion) {
        this.tipoDeduccion = tipoDeduccion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public Deducciones(String tipoDeduccion, String clave, String concepto, String importe) {
        this.tipoDeduccion = tipoDeduccion;
        this.clave = clave;
        this.concepto = concepto;
        this.importe = importe;
    }
}
