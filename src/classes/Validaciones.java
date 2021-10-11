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
public class Validaciones {
    String dID,dClue,cConcepto,dCodigoPuesto;

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public String getdClue() {
        return dClue;
    }

    public void setdClue(String dClue) {
        this.dClue = dClue;
    }

    public String getcConcepto() {
        return cConcepto;
    }

    public void setcConcepto(String cConcepto) {
        this.cConcepto = cConcepto;
    }

    public String getdCodigoPuesto() {
        return dCodigoPuesto;
    }

    public void setdCodigoPuesto(String dNoPuesto) {
        this.dCodigoPuesto = dNoPuesto;
    }

    public Validaciones(String dID, String dClue, String cConcepto, String dNoPuesto) {
        this.dID = dID;
        this.dClue = dClue;
        this.cConcepto = cConcepto;
        this.dCodigoPuesto = dNoPuesto;
    }
    
}
