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
public class Jubilaciones {

    public String getTotalJubilacionPensionRetiro() {
        return totalJubilacionPensionRetiro;
    }

    public void setTotalJubilacionPensionRetiro(String totalJubilacionPensionRetiro) {
        this.totalJubilacionPensionRetiro = totalJubilacionPensionRetiro;
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

    public Jubilaciones(String totalJubilacionPensionRetiro, String ingresoAcumulable, String ingresoNoAcumulable) {
        this.totalJubilacionPensionRetiro = totalJubilacionPensionRetiro;
        this.ingresoAcumulable = ingresoAcumulable;
        this.ingresoNoAcumulable = ingresoNoAcumulable;
    }
    String totalJubilacionPensionRetiro,ingresoAcumulable,ingresoNoAcumulable;
}
