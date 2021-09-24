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
public class DetalleConceptos {
        String dID,cConcepto,cGrabado,cNoGrabado;

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public String getcConcepto() {
        return cConcepto;
    }

    public void setcConcepto(String cConcepto) {
        this.cConcepto = cConcepto;
    }

    public String getcGrabado() {
        return cGrabado;
    }

    public void setcGrabado(String cGrabado) {
        this.cGrabado = cGrabado;
    }

    public String getcNoGrabado() {
        return cNoGrabado;
    }

    public void setcNoGrabado(String cNoGrabado) {
        this.cNoGrabado = cNoGrabado;
    }

    public DetalleConceptos(String dID, String cConcepto, String cGrabado, String cNoGrabado) {
        this.dID = dID;
        this.cConcepto = cConcepto;
        this.cGrabado = cGrabado;
        this.cNoGrabado = cNoGrabado;
    }
}
