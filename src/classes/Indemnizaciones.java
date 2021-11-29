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
public class Indemnizaciones {
    String totalPagado,aniosServicio,ultimoSueldoMensOrd,ingresoAcumulable,ingresoNoAcumulable;

    public Indemnizaciones(String totalPagado, String aniosServicio, String ultimoSueldoMensOrd, String ingresoAcumulable, String ingresoNoAcumulable) {
        this.totalPagado = totalPagado;
        this.aniosServicio = aniosServicio;
        this.ultimoSueldoMensOrd = ultimoSueldoMensOrd;
        this.ingresoAcumulable = ingresoAcumulable;
        this.ingresoNoAcumulable = ingresoNoAcumulable;
    }

    public String getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(String totalPagado) {
        this.totalPagado = totalPagado;
    }

    public String getAniosServicio() {
        return aniosServicio;
    }

    public void setAniosServicio(String aniosServicio) {
        this.aniosServicio = aniosServicio;
    }

    public String getUltimoSueldoMensOrd() {
        return ultimoSueldoMensOrd;
    }

    public void setUltimoSueldoMensOrd(String ultimoSueldoMensOrd) {
        this.ultimoSueldoMensOrd = ultimoSueldoMensOrd;
    }

    public String getIngresoAcumulable() {
        return ingresoAcumulable;
    }

    public void setIngresoAcumulable(String ingresoAcumulable) {
        this.ingresoAcumulable = ingresoAcumulable;
    }

    public String getIngresoNoAcumulable() {
        return ingresoNoAcumulable;
    }

    public void setIngresoNoAcumulable(String ingresoNoAcumulable) {
        this.ingresoNoAcumulable = ingresoNoAcumulable;
    }
}
