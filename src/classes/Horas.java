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
public class Horas {
    String dID;
    String dClave;
    String dDiasHorasDobles;
    String dHorasDobles;
    String dImporteHorasDobles;
    String dDiasHorasTriples;
    String dHorasTriples;
    //Anteriores a esto son para extracción de archivos
    //A partir de aquí, son del contructor utilizado para el xml (lista de horas extra en xml).
    String dias,tipoHoras,horasExtra,importePagado;

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getTipoHoras() {
        return tipoHoras;
    }

    public void setTipoHoras(String tipoHoras) {
        this.tipoHoras = tipoHoras;
    }

    public String getHorasExtra() {
        return horasExtra;
    }

    public void setHorasExtra(String horasExtra) {
        this.horasExtra = horasExtra;
    }

    public String getImportePagado() {
        return importePagado;
    }

    public void setImportePagado(String importePagado) {
        this.importePagado = importePagado;
    }

    public Horas(String dias, String tipoHoras, String horasExtra, String importePagado) {
        this.dias = dias;
        this.tipoHoras = tipoHoras;
        this.horasExtra = horasExtra;
        this.importePagado = importePagado;
    }
    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public String getdClave() {
        return dClave;
    }

    public void setdClave(String dClave) {
        this.dClave = dClave;
    }

    public String getdDiasHorasDobles() {
        return dDiasHorasDobles;
    }

    public void setdDiasHorasDobles(String dDiasHorasDobles) {
        this.dDiasHorasDobles = dDiasHorasDobles;
    }

    public String getdHorasDobles() {
        return dHorasDobles;
    }

    public void setdHorasDobles(String dHorasDobles) {
        this.dHorasDobles = dHorasDobles;
    }

    public String getdImporteHorasDobles() {
        return dImporteHorasDobles;
    }

    public void setdImporteHorasDobles(String dImporteHorasDobles) {
        this.dImporteHorasDobles = dImporteHorasDobles;
    }

    public String getdDiasHorasTriples() {
        return dDiasHorasTriples;
    }

    public void setdDiasHorasTriples(String dDiasHorasTriples) {
        this.dDiasHorasTriples = dDiasHorasTriples;
    }

    public String getdHorasTriples() {
        return dHorasTriples;
    }

    public void setdHorasTriples(String dHorasTriples) {
        this.dHorasTriples = dHorasTriples;
    }

    public String getdImporteHorasTriples() {
        return dImporteHorasTriples;
    }

    public void setdImporteHorasTriples(String dImporteHorasTriples) {
        this.dImporteHorasTriples = dImporteHorasTriples;
    }

    public Horas(String dID, String dClave, String dDiasHorasDobles, String dHorasDobles, String dImporteHorasDobles, String dDiasHorasTriples, String dHorasTriples, String dImporteHorasTriples) {
        this.dID = dID;
        this.dClave = dClave;
        this.dDiasHorasDobles = dDiasHorasDobles;
        this.dHorasDobles = dHorasDobles;
        this.dImporteHorasDobles = dImporteHorasDobles;
        this.dDiasHorasTriples = dDiasHorasTriples;
        this.dHorasTriples = dHorasTriples;
        this.dImporteHorasTriples = dImporteHorasTriples;
    }
    String dImporteHorasTriples;
}
