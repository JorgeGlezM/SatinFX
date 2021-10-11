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
public class Faltas {
            String dID,dClave,dFaltas,dDiaIncapacidad,dTipoIncapacidad;

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

    public String getdFaltas() {
        return dFaltas;
    }

    public void setdFaltas(String dFaltas) {
        this.dFaltas = dFaltas;
    }

    public String getdDiaIncapacidad() {
        return dDiaIncapacidad;
    }

    public void setdDiaIncapacidad(String dDiaIncapacidad) {
        this.dDiaIncapacidad = dDiaIncapacidad;
    }

    public String getdTipoIncapacidad() {
        return dTipoIncapacidad;
    }

    public void setdTipoIncapacidad(String dTipoIncapacidad) {
        this.dTipoIncapacidad = dTipoIncapacidad;
    }

    public Faltas(String dID, String dClave, String dFaltas, String dDiaIncapacidad, String dTipoIncapacidad) {
        this.dID = dID;
        this.dClave = dClave;
        this.dFaltas = dFaltas;
        this.dDiaIncapacidad = dDiaIncapacidad;
        this.dTipoIncapacidad = dTipoIncapacidad;
    }
           
    
}
