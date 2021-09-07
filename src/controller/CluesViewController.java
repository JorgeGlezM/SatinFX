package controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jorge
 */
import java.io.IOException;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.awt.Component;
import javax.swing.JOptionPane;
import classes.MySQL;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;

public class CluesViewController implements Initializable
{
    @FXML
    Label lbl;
    @FXML
    TextField txtCLUE;
    @FXML
    TextField txtDescripcion;
    @FXML
    TextField txtTipo;
    @FXML
    ChoiceBox cmbAcreditado;
    @FXML
    TextField txtTipoUnidad;
    @FXML
    ChoiceBox cmbCentro;
    public static boolean edicion;
    public static String idEdicion;
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    public void initialize(final URL url, final ResourceBundle rb) {
        System.out.println(CluesViewController.edicion);
        this.cmbAcreditado.getItems().add((Object)"N");
        this.cmbAcreditado.getItems().add((Object)"S");
        this.cmbAcreditado.getSelectionModel().select((Object)"N");
        final MySQL mysql = new MySQL();
        MySQL.conectar();
        final ResultSet r = mysql.select("Select id_centrotrabajo FROM centrotrabajo");
        try {
            while (r.next()) {
                this.cmbCentro.getItems().add((Object)r.getString(1));
            }
        }
        catch (SQLException ex2) {
            JOptionPane.showMessageDialog(null, "Error de conexi\u00f3n.", "Advertencia", 2);
        }
        if (CluesViewController.edicion) {
            this.lbl.setText("Editar CLUES");
            final String sql = "SELECT * FROM satin.clues where clue='" + CluesViewController.idEdicion + "';";
            final ResultSet rs = mysql.select(sql);
            System.out.println(sql);
            try {
                rs.next();
                this.txtCLUE.setText(rs.getString(1));
                this.txtDescripcion.setText(rs.getString(2));
                this.txtTipo.setText(rs.getString(3));
                this.cmbAcreditado.getSelectionModel().select((Object)rs.getString(4));
                this.txtTipoUnidad.setText(rs.getString(5));
                this.cmbCentro.getSelectionModel().select((Object)rs.getString(6));
            }
            catch (SQLException ex) {
                Logger.getLogger(CluesViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    public void btnRegistrar(final ActionEvent event) throws IOException {
        final String clue = this.txtCLUE.getText();
        final String descripcion = this.txtDescripcion.getText();
        final String tipo = this.txtTipo.getText();
        final String acreditado = (String)this.cmbAcreditado.getValue();
        final String tipoUnidad = this.txtTipoUnidad.getText();
        final String centro = (String)this.cmbCentro.getValue();
        if (!clue.equals("") && !descripcion.equals("") && !tipo.equals("") && !acreditado.equals("") && !tipoUnidad.equals("") && !centro.equals("")) {
            final MySQL mysql = new MySQL();
            MySQL.conectar();
            if (CluesViewController.edicion) {
                final String sql = "UPDATE satin.clues SET `clue`='" + clue + "',`descripcion`='" + descripcion + "',`tipo`='" + tipo + "',`acreditado`='" + acreditado + "',`tipo_unidad`='" + tipoUnidad + "',`id_centrotrabajo`='" + centro + "' WHERE `clue`='" + CluesViewController.idEdicion + "'";
                System.out.println("Entro");
                System.out.println(sql);
                if (mysql.stmt(sql)) {
                    JOptionPane.showMessageDialog(null, "El registro se ha actualizado exitosamente.", "Aviso", -1);
                    this.root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/LCluesView.fxml"));
                    this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    this.scene = new Scene(this.root);
                    this.stage.setScene(this.scene);
                    final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    this.stage.setX((screenBounds.getWidth() - this.stage.getWidth()) / 2.0);
                    this.stage.setY((screenBounds.getHeight() - this.stage.getHeight()) / 2.0);
                    this.stage.show();
                    MySQL.desconectar();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Error en la actualizacion, intente denuevo m\u00e1s tarde..", "Error", 2);
                }
            }
            else {
                final String sql = "INSERT INTO satin.clues (`clue`, `descripcion`, `tipo`, `acreditado`, `tipo_unidad`, `id_centrotrabajo`) VALUES ('" + clue + "','" + descripcion + "','" + tipo + "','" + acreditado + "','" + tipoUnidad + "','" + centro + "')";
                System.out.println(sql);
                if (mysql.stmt(sql)) {
                    JOptionPane.showMessageDialog(null, "El registro se ha realizado exitosamente.", "Aviso", -1);
                    this.root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/LCluesView.fxml"));
                    this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    this.scene = new Scene(this.root);
                    this.stage.setScene(this.scene);
                    final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    this.stage.setX((screenBounds.getWidth() - this.stage.getWidth()) / 2.0);
                    this.stage.setY((screenBounds.getHeight() - this.stage.getHeight()) / 2.0);
                    this.stage.show();
                    MySQL.desconectar();
                }
                else {
                    MySQL.desconectar();
                    JOptionPane.showMessageDialog(null, "Error en el registro, intente denuevo m\u00e1s tarde..", "Error", 2);
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Ingrese todos los datos.", "Error", 2);
        }
    }
    
    static {
        CluesViewController.edicion = false;
    }
}