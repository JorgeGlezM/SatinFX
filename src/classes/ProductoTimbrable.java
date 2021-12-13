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
public class ProductoTimbrable {
    String producto,año,quincena,fechaDePago,total,tipoNomina;

    public ProductoTimbrable(String producto, String año, String quincena, String fechaDePago, String total, String tipoNomina) {
        this.producto = producto;
        this.año = año;
        this.quincena = quincena;
        this.fechaDePago = fechaDePago;
        this.total = total;
        this.tipoNomina = tipoNomina;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getQuincena() {
        return quincena;
    }

    public void setQuincena(String quincena) {
        this.quincena = quincena;
    }

    public String getFechaDePago() {
        return fechaDePago;
    }

    public void setFechaDePago(String fechaDePago) {
        this.fechaDePago = fechaDePago;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTipoNomina() {
        return tipoNomina;
    }

    public void setTipoNomina(String tipoNomina) {
        this.tipoNomina = tipoNomina;
    }
    
}
