/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Jorge
 */
public class Timbrado {
    String percepciones,deducciones,rfc,nombre,fechai,fechaf,fechapago,sindicato,puesto,fecha_ingreso,nss,clave,curp,jornada,contrato,producto,id,movimiento,descripcionPuesto,unidad,actividad,banco,cuentaBancaria,actividad2,proyecto,partida,clavePago,clue;
    double totalImpuestos,totalDeducciones,totalOtros,subsidio,subsidio2,totalImpuestos2,impuestosSF,totalOtro1,totalOtro2,totalOtro1NO,totalRetenciones,totalTraslados;
    String txtSubsidio,devolucion,claveOtro,UUIDRelacionado;
    List<Conceptos> conceptos=new ArrayList<Conceptos>();

    public Timbrado(String percepciones, String deducciones, String rfc, String nombre, String fechai, String fechaf, String fechapago, String sindicato, String puesto, String fecha_ingreso, String nss, String clave, String curp, String jornada, String contrato,String producto,String id,String movimiento,String descripcionPuesto,String unidad,String actividad,String banco,String cuentaBancaria,String actividad2,String proyecto,String partida,String clavePago,String clue) {
        this.percepciones = percepciones;
        this.deducciones = deducciones;
        this.rfc = rfc;
        this.nombre = nombre;
        this.fechai = fechai;
        this.fechaf = fechaf;
        this.fechapago = fechapago;
        this.sindicato = sindicato;
        this.puesto = puesto;
        this.fecha_ingreso = fecha_ingreso;
        this.nss = nss;
        this.clave = clave;
        this.curp = curp;
        this.jornada = jornada;
        this.contrato = contrato;
        this.producto=producto;
        this.id=id;
        this.movimiento=movimiento;
        this.descripcionPuesto=descripcionPuesto;
        this.unidad=unidad;
        this.actividad=actividad;
        this.banco=banco;
        this.cuentaBancaria=cuentaBancaria;
        this.actividad2=actividad2;
        this.proyecto=proyecto;
        this.partida=partida;
        this.clavePago=clavePago;
        this.clue=clue;
        System.out.println(clue);
        
        //método calcular_subsidios
        //Se modificó para tomar la descripcion_sat de la tabla conceptos_sat ya que ese campo se borró de conceptos
        String select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion,c.activo " +
        "from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and" +
        " ((c.activo=0 and dc.importe+dc.importe_ng<0) or (c.activo=1 and dc.importe+dc.importe_ng>0)) and cs.clave_sat=c.clave_sat and" +
        " dn.producto='" + producto + "' and dn.rfc='" + rfc + "' and dn.id='" + id+"'";
        System.out.println(select);
        MySQL mysql= new MySQL();
        mysql.conectar();
        ResultSet rs= mysql.select(select);
        totalImpuestos=0;
        totalDeducciones=0;
        try {
            while(rs.next()){
                //Calculamos impuestos y deducciones
                if(Integer.parseInt(rs.getString(7))==1){ //deducciones
                    String pad=StringUtils.leftPad(rs.getString(3), 3, '0');
                    if(pad.equals("002") && !rs.getString(5).equals("201SF") && !rs.getString(5).equals("201CA")){
                         totalImpuestos=totalImpuestos+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }else{
                        totalDeducciones=totalDeducciones+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }
                }else{
                    totalDeducciones=totalDeducciones+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Timbrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        subsidio=0;
        subsidio2=0;
        totalImpuestos2=99999;
        txtSubsidio="";
        devolucion=" OR (c.activo=1 and dc.importe+dc.importe_ng<0 and (dc.id_concepto='P00' or mid(dc.id_concepto,1,3)='201'))";
        claveOtro="001";
        select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion "+
        "from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and cs.clave_sat=c.clave_sat and "+
        " ((c.activo=0 and (c.clave_sat=17 or c.clave_sat=99)  and (dc.importe+dc.importe_ng>0)) "+devolucion+
        ") and dn.producto='" +producto+ "' and dn.rfc='" + rfc + "' and dn.id=" +"'"+id+"'";
        System.out.println(select);
        
        totalOtros=0;
        impuestosSF=0;
        totalOtro1=0;
        totalOtro2=0;
        totalOtro1NO=0;
        try{ 
            String temp=rs.getString(5);
            temp=temp.substring(0,3);
            while(rs.next()){
                if(temp.equals("201")){
                    if(rs.getString(5).equals("201SF")){
                        claveOtro="005";
                        impuestosSF=impuestosSF+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }
                    else{
                        claveOtro="001";
                    }
                }else if(temp.equals("1SE")){
                    claveOtro="002";
                }
                switch(claveOtro){
                    case "001": totalOtro1=totalOtro1+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));break;
                    case "002": totalOtro2=totalOtro2+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2)); break;
                    case "005": totalOtro1=totalOtro1+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));break;
                }
                //Aquí va parte de totalotro1no que está pendiente ver si se programa o no
                
                if(totalImpuestos<totalOtro2&&totalOtro2>0){//Subsidio mayor que ISR
                    totalOtros=totalOtro2-totalImpuestos;
                    totalImpuestos2=0;
                    totalImpuestos=0;
                }else if(totalImpuestos>totalOtro2&&totalOtro2>0){
                    subsidio=totalOtro2;
                    totalImpuestos2=totalImpuestos-totalOtro2;
                    totalImpuestos=totalImpuestos-totalOtro2;
                    totalOtros=0.01;
                    subsidio2=0;
                    
                }
            }
        }catch(Exception e){
           
        }
        //Metodo Cargar datos recibo (la mayoría lo hace la consulta principal y el constructor)
        
        //Método CrearCFDI
        UUIDRelacionado="";
        if(sindicato.equals("Sí")||sindicato.equals("SI")||sindicato.equals("SÍ")||sindicato.equals("Si")){
            this.sindicato="Sí";
        }else{this.sindicato="No";}
        totalRetenciones=0;
        totalTraslados=0;
        select="select ifnull(sum(d.importe),0)from detalle_conceptos d,conceptos c "+
        "where d.id_concepto=c.id_concepto and c.clave_sat=2 and d.importe>0 and d.id_detalle_nomina='"+id+"'";
        System.out.println(select);
        try{
            rs.next();
            totalRetenciones=Float.parseFloat(rs.getString(1));
        }catch(Exception e){

        }
        select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion,c.activo from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs"+
        " where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and cs.clave_sat=c.clave_sat and ((c.activo=1 and dc.importe+dc.importe_ng>0) OR (c.activo=0 and dc.importe+dc.importe_ng<0))"+
        " and dn.producto='" +producto+"' and dn.rfc='" +rfc+"' and dn.id='"+id+"'";
        rs=mysql.select(select);
        try {
            while(rs.next()){
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Timbrado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public double getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(double totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public double getTotalDeducciones() {
        return totalDeducciones;
    }

    public void setTotalDeducciones(double totalDeducciones) {
        this.totalDeducciones = totalDeducciones;
    }

    public double getTotalOtros() {
        return totalOtros;
    }

    public void setTotalOtros(double totalOtros) {
        this.totalOtros = totalOtros;
    }

    public double getSubsidio() {
        return subsidio;
    }

    public void setSubsidio(double subsidio) {
        this.subsidio = subsidio;
    }

    public double getSubsidio2() {
        return subsidio2;
    }

    public void setSubsidio2(double subsidio2) {
        this.subsidio2 = subsidio2;
    }

    public double getTotalImpuestos2() {
        return totalImpuestos2;
    }

    public void setTotalImpuestos2(double totalImpuestos2) {
        this.totalImpuestos2 = totalImpuestos2;
    }

    public double getImpuestosSF() {
        return impuestosSF;
    }

    public void setImpuestosSF(double impuestosSF) {
        this.impuestosSF = impuestosSF;
    }

    public double getTotalOtro1() {
        return totalOtro1;
    }

    public void setTotalOtro1(double totalOtro1) {
        this.totalOtro1 = totalOtro1;
    }

    public double getTotalOtro2() {
        return totalOtro2;
    }

    public void setTotalOtro2(double totalOtro2) {
        this.totalOtro2 = totalOtro2;
    }

    public double getTotalOtro1NO() {
        return totalOtro1NO;
    }

    public void setTotalOtro1NO(double totalOtro1NO) {
        this.totalOtro1NO = totalOtro1NO;
    }

    public String getTxtSubsidio() {
        return txtSubsidio;
    }

    public void setTxtSubsidio(String txtSubsidio) {
        this.txtSubsidio = txtSubsidio;
    }

    public String getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(String devolucion) {
        this.devolucion = devolucion;
    }

    public String getClaveOtro() {
        return claveOtro;
    }

    public void setClaveOtro(String claveOtro) {
        this.claveOtro = claveOtro;
    }

    public String getUUIDRelacionado() {
        return UUIDRelacionado;
    }

    public void setUUIDRelacionado(String UUIDRelacionado) {
        this.UUIDRelacionado = UUIDRelacionado;
    }

    public List<Conceptos> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<Conceptos> conceptos) {
        this.conceptos = conceptos;
    }

    public String getPercepciones() {
        return percepciones;
    }

    public void setPercepciones(String percepciones) {
        this.percepciones = percepciones;
    }

    public String getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(String deducciones) {
        this.deducciones = deducciones;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechai() {
        return fechai;
    }

    public void setFechai(String fechai) {
        this.fechai = fechai;
    }

    public String getFechaf() {
        return fechaf;
    }

    public void setFechaf(String fechaf) {
        this.fechaf = fechaf;
    }

    public String getFechapago() {
        return fechapago;
    }

    public void setFechapago(String fechapago) {
        this.fechapago = fechapago;
    }

    public String getSindicato() {
        return sindicato;
    }

    public void setSindicato(String sindicato) {
        this.sindicato = sindicato;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }
}
