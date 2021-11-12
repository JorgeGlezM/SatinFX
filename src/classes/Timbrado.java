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
    double totalImpuestos,totalDeducciones,totalOtros,subsidio,subsidio2,totalImpuestos2,impuestosSF,totalOtro1,totalOtro2,totalOtro1NO,totalRetenciones,totalTraslados,totalGravado,totalExento,importeMixto,importePropio,totalP,subtotalFactura,descuentoFactura,totalFactura;
    int EST,leyenda;
    boolean poneHonorarios=false;
    public String getDescripcionPuesto() {
        return descripcionPuesto;
    }

    public void setDescripcionPuesto(String descripcionPuesto) {
        this.descripcionPuesto = descripcionPuesto;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public String getActividad2() {
        return actividad2;
    }

    public void setActividad2(String actividad2) {
        this.actividad2 = actividad2;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getClavePago() {
        return clavePago;
    }

    public void setClavePago(String clavePago) {
        this.clavePago = clavePago;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public double getTotalRetenciones() {
        return totalRetenciones;
    }

    public void setTotalRetenciones(double totalRetenciones) {
        this.totalRetenciones = totalRetenciones;
    }

    public double getTotalTraslados() {
        return totalTraslados;
    }

    public void setTotalTraslados(double totalTraslados) {
        this.totalTraslados = totalTraslados;
    }

    public double getTotalGravado() {
        return totalGravado;
    }

    public void setTotalGravado(double totalGravado) {
        this.totalGravado = totalGravado;
    }

    public double getTotalExento() {
        return totalExento;
    }

    public void setTotalExento(double totalExento) {
        this.totalExento = totalExento;
    }

    public double getImporteMixto() {
        return importeMixto;
    }

    public void setImporteMixto(double importeMixto) {
        this.importeMixto = importeMixto;
    }

    public double getImportePropio() {
        return importePropio;
    }

    public void setImportePropio(double importePropio) {
        this.importePropio = importePropio;
    }

    public double getTotalP() {
        return totalP;
    }

    public void setTotalP(double totalP) {
        this.totalP = totalP;
    }

    public double getSubtotalFactura() {
        return subtotalFactura;
    }

    public void setSubtotalFactura(double subtotalFactura) {
        this.subtotalFactura = subtotalFactura;
    }

    public String getTipoRegimen() {
        return tipoRegimen;
    }

    public void setTipoRegimen(String tipoRegimen) {
        this.tipoRegimen = tipoRegimen;
    }
    String txtSubsidio,devolucion,claveOtro,UUIDRelacionado,tipoRegimen;
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
        String select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion,c.tipo " +
        "from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and" +
        " ((c.tipo='P' and dc.importe+dc.importe_ng<0) or (c.tipo='D' and dc.importe+dc.importe_ng>0)) and cs.clave_sat=c.clave_sat and" +
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
                if(rs.getString(7).equals("D")){ //deducciones
                    String pad=StringUtils.leftPad(rs.getString(3), 3, '0');
                    if(pad.equals("002") && !rs.getString(5).equals("201SF") && !rs.getString(5).equals("201CA")){
                         totalImpuestos=totalImpuestos+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }else{
                        totalDeducciones=totalDeducciones+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }
                }else{//Percepciones negativas
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
        devolucion=" OR (c.tipo='D' and dc.importe+dc.importe_ng<0 and (dc.id_concepto='P00' or mid(dc.id_concepto,1,3)='201'))";//Deducción negativa es devolución?
        claveOtro="001";
        select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion "+
        "from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and cs.clave_sat=c.clave_sat and "+
        " ((c.tipo='P' and (c.clave_sat=17 or c.clave_sat=99)  and (dc.importe+dc.importe_ng>0)) "+devolucion+
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
        " where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and cs.clave_sat=c.clave_sat and ((c.tipo='D' and dc.importe+dc.importe_ng>0) OR (c.tipo='P' and dc.importe+dc.importe_ng<0))"+
        " and dn.producto='" +producto+"' and dn.rfc='" +rfc+"' and dn.id='"+id+"'";
        rs=mysql.select(select);
        totalGravado=0;
        totalExento=0;
        importeMixto=0;
        importePropio=0;
        totalP=0;
        try {
            while(rs.next()){
                if(rs.getString(7).equals("P")){//Percepciones
                    totalGravado=totalGravado+Float.parseFloat(rs.getString(1));
                    totalExento=totalExento+Float.parseFloat(rs.getString(2));
                    totalP=totalP+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                }else{//Deducciones negativas
                    totalExento=totalExento+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));
                }
                
                if(StringUtils.leftPad(rs.getString(3),3,"0").equals("046")){
                    tipoRegimen="09";//Si tiene concepto con clave 46 son honorarios.
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Timbrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Calcular honorarios Nayarit
        if(DatosPatronales.getRfc().equals("SSN960901HJ7")&&(unidad.equals("972")||unidad.equals("907")||producto.substring((producto.length()-1), producto.length()).equals("A")||producto.substring((producto.length()-1), producto.length()).equals("U")||producto.substring((producto.length()-1), producto.length()).equals("5"))){
            poneHonorarios=true;
            this.banco="072";
            this.cuentaBancaria=cuentaBancaria.substring(2,22);
            if(producto.endsWith("5")||producto.endsWith("A")){
                leyenda=2;
            }else if(unidad.equals("907")){
                leyenda=2;
            }else if(producto.endsWith("U")){
                leyenda=1;
            }
            
        }
        subtotalFactura=totalGravado+totalExento;
        if(importeMixto>0&&importePropio>0){
            EST=50;
        }
        descuentoFactura=totalImpuestos+totalDeducciones;
        totalFactura=subtotalFactura-descuentoFactura;
        

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
