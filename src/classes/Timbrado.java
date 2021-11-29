/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import static classes.GenerarXML.fmt;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static java.util.concurrent.TimeUnit.DAYS;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Jorge
 */
public class Timbrado {
    static NumberFormat fmt = NumberFormat.getInstance(Locale.US);
    String percepciones,deducciones,rfc,nombre,fechai,fechaf,fechapago,sindicato,puesto,fecha_ingreso,nss,clave,curp,jornada,contrato,producto,id,movimiento,descripcionPuesto,unidad,actividad,banco,cuentaBancaria,actividad2,proyecto,partida,clavePago,clue,ingresoAcumulable,ingresoNoAcumulable,ultimoSueldo;


    public void setIndemnizacionesList(List<Indemnizaciones> indemnizacionesList) {
        this.indemnizacionesList = indemnizacionesList;
    }
    double totalImpuestos,totalDeducciones,totalOtros,subsidio,subsidio2,totalImpuestos2,impuestosSF,totalOtro1,totalOtro2,totalOtro1NO,totalRetenciones,totalTraslados,totalGravado,totalExento,importeMixto,importePropio,totalP,subtotalFactura,descuentoFactura,totalFactura,subtotal,salarioDiario,totalSueldos,totalJubilacionPensionRetiro,totalSeparacion;
    int EST,leyenda,diasPagados;
    boolean poneHonorarios=false;//Pone o no atributo
    boolean ponerRegistroPatronal=false;//Pone o no atributo
    boolean ponerEntidadSNCF=false;//Pone o no atributo 
    public static boolean check1=false,check2=false,check3=false,check4=false,check5=false,check6=false;//Checks del formulario

    public boolean isPonerRegistroPatronal() {
        return ponerRegistroPatronal;
    }

    public void setPonerRegistroPatronal(boolean ponerRegistroPatronal) {
        this.ponerRegistroPatronal = ponerRegistroPatronal;
    }
    String txtSubsidio,devolucion,claveOtro,UUIDRelacionado,tipoRegimen,periodoPago,claveTipo,tipoCambio,tipoContrato,tipoRiesgo,antiguedad,CFDOrigen="";
    List<Percepciones> percepcionesList=new ArrayList<Percepciones>();
    List<Jubilaciones> jubilacionesList=new ArrayList<Jubilaciones>();
    List<Indemnizaciones> indemnizacionesList=new ArrayList<Indemnizaciones>();
    List<Horas> horasList=new ArrayList<Horas>();
    List<Deducciones> deduccionesList=new ArrayList<Deducciones>();
    List<OtrosPagos> otrosPagosList=new ArrayList<OtrosPagos>();





    public static int periodoFormulario=1;//Se obtiene del formulario, puse valor 1 par pruebas. Borrar asignación después.
    


    public Timbrado(String percepciones, String deducciones, String rfc, String nombre, String fechai, String fechaf, String fechapago, String sindicato, String puesto, String fecha_ingreso, String nss, String clave, String curp, String jornada, String contrato,String producto,String id,String movimiento,String descripcionPuesto,String unidad,String actividad,String banco,String cuentaBancaria,String actividad2,String proyecto,String partida,String clavePago,String clue,String ingresoAcumulable,String ingresoNoAcumulable,String ultimoSueldo) {
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
        this.ingresoAcumulable=ingresoAcumulable;
        this.ingresoNoAcumulable=ingresoNoAcumulable;
        this.ultimoSueldo=ultimoSueldo;
        fmt.setMaximumFractionDigits(2);
        fmt.setMinimumFractionDigits(2);
        salarioDiario=Double.parseDouble(this.percepciones);//Así estaba en el código, se tiene que recalcular, ahora solo se divide entre los días 
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
        devolucion=" OR (c.tipo='D' and dc.importe+dc.importe_ng<0 and (dc.id_concepto='P00' or mid(dc.id_concepto,1,3)='201'))";
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
            if(!cuentaBancaria.equals("")||!cuentaBancaria.equals("0")){
                this.cuentaBancaria=cuentaBancaria.substring(2,cuentaBancaria.length());
            }
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
        //Calculamos los días pagados (método crear nodo complemento)
        LocalDate fechaInicial=LocalDate.parse(fechai);
        LocalDate fechaFinal=LocalDate.parse(fechaf);
        tipoRiesgo="01";
        diasPagados = (int) ChronoUnit.DAYS.between(fechaInicial, fechaFinal);
        diasPagados=diasPagados+1;//Se suma un día para que cuente también el primero y no únicamente los días transcurridos posterior a éste.
        if(periodoFormulario==1){
            if(diasPagados==15){
                periodoPago="04";
            }else if(diasPagados==16&&fechaf.substring((fechaf.length()-2),(fechaf.length())).equals("31")){
                periodoPago="04";
                diasPagados=15;
            }else if((diasPagados==14||diasPagados==13)&&fechaf.substring((fechaf.length()-2),(fechaf.length())).equals("28")||fechaf.substring((fechaf.length()-2),(fechaf.length())).equals("29")){
                periodoPago="04";
                diasPagados=15;
            }else{periodoPago="99";}
        }else if(periodoFormulario==2){
            if(diasPagados==16){
                periodoPago="04";
                diasPagados=15;
            }else{
                periodoPago="99";
            }
        }else if(periodoFormulario==3){
            periodoPago="04";
            diasPagados=15;
        }else{
            periodoPago="99";
        }
        claveTipo="E";
        String temp=producto.substring(0,3);
        if(temp.equals("PRD")){
            if(producto.substring(3,4).equals("O")){
                claveTipo="O";
            }
        }else if(producto.substring(1,2).equals("O")){
            claveTipo="O";
        }
        double tempDescuentos=0;//variable DescuentosFactura del método crear atributos comprobante.
        if(totalImpuestos2!=99999){
            descuentoFactura=totalDeducciones+totalImpuestos;
            subtotal=subtotalFactura+totalOtros-subsidio-totalOtro1NO;
            totalFactura=subtotal-tempDescuentos-totalOtro1NO;
        }else{
            tempDescuentos=descuentoFactura-totalOtro1NO;
            subtotal=subtotalFactura+subsidio2-totalOtro1NO;
            totalFactura=subtotal-tempDescuentos;
        }
        descuentoFactura=descuentoFactura-totalOtro1NO;
        tipoContrato="01";
        if(!DatosPatronales.getRfc().equals("SSN970115QI9")&&!DatosPatronales.getRfc().equals("REP150914KD0")&&!DatosPatronales.getRfc().equals("SSC961129CH3")){
            tipoRegimen="02";
        }
        if(DatosPatronales.getRfc().equals("SSN970115QI9")&&unidad.equals("CON")){
            tipoRegimen="09";
        }
        if((DatosPatronales.getRfc().equals("SSS960912HW9")||DatosPatronales.getRfc().equals("ISD9609109M3")||DatosPatronales.getRfc().equals("SSN960901HJ7"))&&contrato.equals("HONORARIOS")){//SLP, NAY y Campeche
            tipoRegimen="09";
        }
        if(isPoneHonorarios()){
            tipoContrato="09";
            tipoRegimen="09";
            tipoRiesgo="99";
        }
        if(tipoRegimen.equals("09")){
            tipoContrato="09";
            tipoRiesgo="99";
        }else{
            ponerRegistroPatronal=true;
        }
        if(!DatosPatronales.getEntidad_sncf().equals("0")){
            if(check6){
                EST=0;
                select="select importe_propio FROM detalle_nomina where id= '"+id+"'";
                rs = mysql.select(select);
                try{
                    rs.next();
                    EST=Integer.parseInt(rs.getString(1));
                }catch(Exception e){
                    
                }
            }
            ponerEntidadSNCF=true;
            if(EST==100){
                CFDOrigen="IP";
            }else if(EST==0){
                CFDOrigen="IF";
            }else{
                CFDOrigen="IM";
            }
        }
        if(salarioDiario<0){
            salarioDiario=salarioDiario*(-1);
        }
        salarioDiario=salarioDiario/diasPagados;
        //Método antiguedad
        LocalDate fechaIngreso=LocalDate.parse(fecha_ingreso);
        String anios=String.valueOf(ChronoUnit.YEARS.between(fechaIngreso, fechaFinal));
        String meses=String.valueOf(ChronoUnit.MONTHS.between(fechaIngreso, fechaFinal)%12);
        LocalDate fechaTemp=LocalDate.parse(fecha_ingreso);
        fechaTemp=fechaTemp.plusYears(Long.parseLong(anios));
        fechaTemp=fechaTemp.plusMonths(Long.parseLong(meses));
        String dias=String.valueOf(ChronoUnit.DAYS.between(fechaTemp, fechaFinal));
        antiguedad="P";
        if(!anios.equals("0")){
            antiguedad=antiguedad+anios+"Y";
        }if(!meses.equals("0")){
            antiguedad=antiguedad+meses+"M";
        }
        antiguedad=antiguedad+dias+"D";
        String sub=" and (dc.id_concepto<>'1SE00')";
        String dev=" and ((mid(dc.id_concepto,1,3)<>'201') and c.clave_sat<>2)";
        
        select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion,c.tipo "+
        "from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and" +
        " ((c.tipo='P' and (c.clave_sat<>17 and c.clave_sat<>99)  and (dc.importe+dc.importe_ng)>0 " + sub + ") " +
        " OR (c.tipo='D' and (dc.importe+dc.importe_ng)<0 " +dev + " ))" +
        " and dn.producto='" +producto+ "' and dn.rfc='" +rfc+ "' and dn.id='"+id+ "' order by dc.id";
        System.out.println(select);
        totalGravado=0;
        totalExento=0;
        totalSueldos=0;
        totalJubilacionPensionRetiro=0;
        totalSeparacion=0;
        try{
            while(rs.next()){
                Float imp_gra=Float.parseFloat(rs.getString(1));
                if(rs.getString(1).equals("0.01")||rs.getString(1).equals("0.02")){
                    imp_gra=Float.parseFloat("0");
                }
                Float imp_exe=Float.parseFloat(rs.getString(2));
                if(rs.getString(2).equals("-0.01")||rs.getString(2).equals("-0.02")){
                    imp_exe=Float.parseFloat("0");
                }
                String tipo_per=StringUtils.leftPad(rs.getString(3), 3, '0');
                if(rs.getString(7).equals("P")){
                    totalGravado=totalGravado+Float.parseFloat(rs.getString(1));
                    totalExento=totalExento+Float.parseFloat(rs.getString(2));
                    if(!tipo_per.equals("022")&&!tipo_per.equals("023")&&!tipo_per.equals("025")&&!tipo_per.equals("039")&&!tipo_per.equals("044")&&!tipo_per.equals("019")){//Percepciones
                        totalSueldos=totalSueldos+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                        String clv=rs.getString(5).replaceAll("\\s+","");
                        String conc=rs.getString(6).replaceAll("\\s+","");
                        percepcionesList.add(new Percepciones(tipo_per,clv,conc,fmt.format(imp_gra),fmt.format(imp_exe)));
                    }else if(tipo_per.equals("025")||tipo_per.equals("022")||tipo_per.equals("023")){//Separaciones
                        indemnizacionesList.add(new Indemnizaciones(fmt.format(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))),anios,ultimoSueldo,ingresoAcumulable,ingresoNoAcumulable));
                        totalSeparacion=totalSeparacion+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }else if(tipo_per.equals("039")){//Jubilaciones
                        totalJubilacionPensionRetiro=totalJubilacionPensionRetiro+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                        jubilacionesList.add(new Jubilaciones(fmt.format(totalJubilacionPensionRetiro),ingresoAcumulable,ingresoNoAcumulable));
                    }else if(tipo_per.equals("019")){//Jubilaciones
                        totalSueldos=totalSueldos+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                        select="select h.* from horas_extras h, detalle_nomina dn "+
                        " where dn.id=h.id_detalle_nomina and dn.id='"+id+"'";
                        rs=mysql.select(select);
                        try{
                            String diaHorasDobles=rs.getString(4);
                            String horasDobles=rs.getString(5);
                            String importeHorasDobles=fmt.format(rs.getString(6));
                            String diaHorasTriples=rs.getString(7);
                            String horasTriples=rs.getString(8);
                            String importeHorasTriples=fmt.format(rs.getString(9));
                            if(!diaHorasDobles.equals("0")){
                                horasList.add(new Horas(diaHorasDobles,"01",horasDobles,importeHorasDobles));
                            }
                            if(!diaHorasTriples.equals("0")){
                                horasList.add(new Horas(diaHorasTriples,"02",horasTriples,importeHorasTriples));
                            }
                        }catch(Exception e){
                            
                        }
                    }
                }else{//deducciones negativas
                    tipo_per="038";
                    imp_exe=(imp_exe+imp_gra)*-1;
                    imp_gra=Float.parseFloat("0");
                    totalExento=totalExento+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));
                    totalSueldos=totalSueldos+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));
                    String clv=rs.getString(5).replaceAll("\\s+","");
                    String conc=rs.getString(6).replaceAll("\\s+","");
                    percepcionesList.add(new Percepciones(tipo_per,clv,conc,fmt.format(imp_gra),fmt.format(imp_exe)));

                }
            }
            String dev2="";
            sub="";
            dev=" OR (c.activo=1 and dc.importe+dc.importe_ng<0 and (dc.id_concepto='P00' or mid(dc.id_concepto,1,3)='201')) ";
            dev2=" OR (c.activo=1 and dc.importe+dc.importe_ng<0 and (dc.id_concepto<>'P00' and mid(dc.id_concepto,1,3)<>'201')) ";
            claveOtro="001";
            
            select="select dc.importe,dc.importe_ng,c.clave_sat,cs.descripcion,c.id_concepto,c.descripcion,c.tipo "+
            " from detalle_nomina dn, detalle_conceptos dc, conceptos c,conceptos_sat cs"+
            " where dn.id=dc.id_detalle_nomina and dc.id_concepto=c.id_concepto and "+
            " ((c.tipo='P' and dc.importe+dc.importe_ng<0) or (c.tipo='D' and dc.importe+dc.importe_ng>0)) "+
            "and dn.id='"+id+"'";
            rs=mysql.select(select);
            totalImpuestos=0;
            totalDeducciones=0;
            int BAND=0;
            while(rs.next()){
                String tipo_per=StringUtils.leftPad(rs.getString(3), 3, '0');
                Float imp_gra=Float.parseFloat(rs.getString(1));
                Float imp_exe=Float.parseFloat(rs.getString(2));
                if(rs.getString(7).equals("D")){//Deducciones
                    if(tipo_per.equals("002")){
                        totalImpuestos=totalImpuestos+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }else{
                        totalDeducciones=totalDeducciones+Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2));
                    }
                }else{//Percepciones negativas
                    totalDeducciones=totalDeducciones+((-1)*(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2))));
                    tipo_per="004";
                    imp_exe=(Float.parseFloat(rs.getString(1))+Float.parseFloat(rs.getString(2)))*(-1);
                    imp_gra=Float.parseFloat("0");
                }
                if(totalImpuestos2>0||!tipo_per.equals("002")){
                    if((totalImpuestos2==totalImpuestos||totalOtro1NO>0)&&tipo_per.equals("002")&&BAND==0){
                        String clv=rs.getString(5).replaceAll("\\s+","");
                        String conc=rs.getString(6).replaceAll("\\s+","");
                        deduccionesList.add(new Deducciones(tipo_per,clv,conc,fmt.format(totalImpuestos)));
                        BAND=1;
                    }
                }else if((totalImpuestos2==totalImpuestos||totalOtro1NO>0)&&tipo_per.equals("002")&&BAND==1){
                        String clv=rs.getString(5).replaceAll("\\s+","");
                        String conc=rs.getString(6).replaceAll("\\s+","");
                        deduccionesList.add(new Deducciones(tipo_per,clv,conc,fmt.format(imp_exe+imp_gra)));
                }
            }
            totalImpuestos=totalImpuestos-totalOtro1NO;
            if(totalImpuestos2==0){
                totalImpuestos=0;
            }else if(totalImpuestos!=99999){
                totalImpuestos=totalImpuestos2;
            }
            
            
        }catch(Exception e){
            
        }
        


    }

    public double getTotalSueldos() {
        return totalSueldos;
    }

    public void setTotalSueldos(double totalSueldos) {
        this.totalSueldos = totalSueldos;
    }

    public double getTotalJubilacionPensionRetiro() {
        return totalJubilacionPensionRetiro;
    }

    public void setTotalJubilacionPensionRetiro(double totalJubilacionPensionRetiro) {
        this.totalJubilacionPensionRetiro = totalJubilacionPensionRetiro;
    }

    public double getTotalSeparacion() {
        return totalSeparacion;
    }

    public void setTotalSeparacion(double totalSeparacion) {
        this.totalSeparacion = totalSeparacion;
    }

    public String getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
    }

    public double getSalarioDiario() {
        return salarioDiario;
    }

    public void setSalarioDiario(double salarioDiario) {
        this.salarioDiario = salarioDiario;
    }

    public String getCFDOrigen() {
        return CFDOrigen;
    }

    public void setCFDOrigen(String CFDOrigen) {
        this.CFDOrigen = CFDOrigen;
    }

    public boolean isPonerEntidadSNCF() {
        return ponerEntidadSNCF;
    }

    public void setPonerEntidadSNCF(boolean ponerEntidadSNCF) {
        this.ponerEntidadSNCF = ponerEntidadSNCF;
    }

    public static boolean isCheck1() {
        return check1;
    }

    public static void setCheck1(boolean check1) {
        Timbrado.check1 = check1;
    }

    public static boolean isCheck2() {
        return check2;
    }

    public static void setCheck2(boolean check2) {
        Timbrado.check2 = check2;
    }

    public static boolean isCheck3() {
        return check3;
    }

    public static void setCheck3(boolean check3) {
        Timbrado.check3 = check3;
    }

    public static boolean isCheck4() {
        return check4;
    }

    public static void setCheck4(boolean check4) {
        Timbrado.check4 = check4;
    }

    public static boolean isCheck5() {
        return check5;
    }

    public static void setCheck5(boolean check5) {
        Timbrado.check5 = check5;
    }

    public static boolean isCheck6() {
        return check6;
    }

    public static void setCheck6(boolean check6) {
        Timbrado.check6 = check6;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoRiesgo() {
        return tipoRiesgo;
    }

    public void setTipoRiesgo(String tipoRiesgo) {
        this.tipoRiesgo = tipoRiesgo;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getClaveTipo() {
        return claveTipo;
    }

    public void setClaveTipo(String claveTipo) {
        this.claveTipo = claveTipo;
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
        public int getDiasPagados() {
        return diasPagados;
    }

    public void setDiasPagados(int diasPagados) {
        this.diasPagados = diasPagados;
    }
    public double getDescuentoFactura() {
        return descuentoFactura;
    }

    public void setDescuentoFactura(double descuentoFactura) {
        this.descuentoFactura = descuentoFactura;
    }

    public double getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(double totalFactura) {
        this.totalFactura = totalFactura;
    }

    public int getEST() {
        return EST;
    }

    public void setEST(int EST) {
        this.EST = EST;
    }

    public int getLeyenda() {
        return leyenda;
    }

    public void setLeyenda(int leyenda) {
        this.leyenda = leyenda;
    }

    public String getPeriodoPago() {
        return periodoPago;
    }

    public void setPeriodoPago(String periodoPago) {
        this.periodoPago = periodoPago;
    }

    public boolean isPoneHonorarios() {
        return poneHonorarios;
    }

    public void setPoneHonorarios(boolean poneHonorarios) {
        this.poneHonorarios = poneHonorarios;
    }

    public static int getPeriodoFormulario() {
        return periodoFormulario;
    }

    public static void setPeriodoFormulario(int periodoFormulario) {
        Timbrado.periodoFormulario = periodoFormulario;
    }

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
    public static NumberFormat getFmt() {
        return fmt;
    }

    public static void setFmt(NumberFormat fmt) {
        Timbrado.fmt = fmt;
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

    public String getUltimoSueldo() {
        return ultimoSueldo;
    }

    public void setUltimoSueldo(String ultimoSueldo) {
        this.ultimoSueldo = ultimoSueldo;
    }

    public List<Percepciones> getPercepcionesList() {
        return percepcionesList;
    }

    public void setPercepcionesList(List<Percepciones> percepcionesList) {
        this.percepcionesList = percepcionesList;
    }

    public List<Jubilaciones> getJubilacionesList() {
        return jubilacionesList;
    }

    public void setJubilacionesList(List<Jubilaciones> jubilacionesList) {
        this.jubilacionesList = jubilacionesList;
    }

    public List<Indemnizaciones> getIndemnizacionesList() {
        return indemnizacionesList;
    }

}
