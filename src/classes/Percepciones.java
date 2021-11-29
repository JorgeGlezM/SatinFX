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
class Percepciones {
    String tipo_per,clave,concepto,imp_gra,imp_exe;

    public String getTipo_per() {
        return tipo_per;
    }

    public void setTipo_per(String tipo_per) {
        this.tipo_per = tipo_per;
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

    public String getImp_gra() {
        return imp_gra;
    }

    public void setImp_gra(String imp_gra) {
        this.imp_gra = imp_gra;
    }

    public String getImp_exe() {
        return imp_exe;
    }

    public void setImp_exe(String imp_exe) {
        this.imp_exe = imp_exe;
    }

    public Percepciones(String tipo_per, String clave, String concepto, String imp_gra, String imp_exe) {
        this.tipo_per = tipo_per;
        this.clave = clave;
        this.concepto = concepto;
        this.imp_gra = imp_gra;
        this.imp_exe = imp_exe;
    }
    
}
