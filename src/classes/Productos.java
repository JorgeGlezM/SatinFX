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
public class Productos {
    String pClave,pAnio,pMes,pFechaPago,pTotal;

    public Productos(String pClave, String pAnio, String pMes, String pFechaPago,  String pTotal) {
        this.pClave = pClave;
        this.pAnio = pAnio;
        this.pMes = pMes;
        this.pFechaPago = pFechaPago;
        this.pTotal = pTotal;
    }

    public String getpClave() {
        return pClave;
    }

    public void setpClave(String pClave) {
        this.pClave = pClave;
    }

    public String getpAnio() {
        return pAnio;
    }

    public void setpAnio(String pAnio) {
        this.pAnio = pAnio;
    }

    public String getpMes() {
        return pMes;
    }

    public void setpMes(String pMes) {
        this.pMes = pMes;
    }

    public String getpFechaPago() {
        return pFechaPago;
    }

    public void setpFechaPago(String pFechaPago) {
        this.pFechaPago = pFechaPago;
    }

    public String getpTotal() {
        return pTotal;
    }

    public void setpTotal(String pTotal) {
        this.pTotal = pTotal;
    }
    
}
