package es.aritzherrero.ejerciciog.controlador;


import java.net.URL;
import java.util.ResourceBundle;
import es.aritzherrero.ejerciciog.modelo.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador para la ventana auxiliar de añadir o modificar personas.
 * Implementa la interfaz Initializable para manejar la inicialización de los componentes.
 */
public class EjercicioG_NuevaPersona_Control implements Initializable{

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtEdad;

    @FXML
    private TextField txtNombre;

    // Variables de clase
    String camposNulos;

    /**
     * Procedimiento de inicialización que se ejecuta al crear la ventana.
     * Si se está editando una persona, carga los datos en los campos de texto.
     *
     * @param arg0 URL de inicialización
     * @param arg1 ResourceBundle para inicialización
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Si hay una persona seleccionada para modificar, cargar sus datos
        if (!EjercicioG_Principal_Control.p.getApellidos().isEmpty()) {
            txtNombre.setText(EjercicioG_Principal_Control.p.getNombre());
            txtApellidos.setText(EjercicioG_Principal_Control.p.getApellidos());
            txtEdad.setText(EjercicioG_Principal_Control.p.getEdad() + "");
        }
    }

    /**
     * Procedimiento para cerrar la ventana sin guardar cambios.
     *
     * @param event Evento de acción generado al hacer clic en el botón cancelar.
     */
    @FXML
    void cancelarVentana(ActionEvent event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close(); // Cierra la ventana actual
    }

    /**
     * Procedimiento para guardar la persona en la base de datos.
     * Si la persona ya existe, se modifica, si no, se agrega.
     *
     * @param event Evento de acción generado al hacer clic en el botón guardar.
     */
    @FXML
    void guardarPersona(ActionEvent event) {
        if (EjercicioG_Principal_Control.p.getNombre().equals("")) {
            aniadir(); // Añadir nueva persona
        } else {
            modificar(); // Modificar persona existente
        }
        cancelarVentana(event); // Cerrar ventana tras guardar
    }

    /*
     * Métodos auxiliares
     */

    /**
     * Añade una nueva persona a la tabla y base de datos, verificando que no exista previamente.
     * Muestra mensajes de alerta en caso de error.
     */
    private void aniadir() {
        String camposNulos = "";
        try {
            // Comprobar que los campos no son nulos
            camposNulos = comprobarCampos();
            if (!camposNulos.equals("")) throw new NullPointerException();
            if (Integer.parseInt(txtEdad.getText()) < 1) throw new NumberFormatException();

            // Crear nueva persona
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            Integer edad = Integer.parseInt(txtEdad.getText());
            Persona p = new Persona(nombre, apellidos, edad);

            // Comprobar que la persona no existe antes de añadirla
            if (!EjercicioG_Principal_Control.listaPersonas.contains(p)) {
                p.setId(EjercicioG_Principal_Control.pDao.insertarPersona(p));
                EjercicioG_Principal_Control.listaPersonas.add(p);
                EjercicioG_Principal_Control.listaFiltrada.add(p);

                EjercicioG_Principal_Control.ventanaAlerta("I", "Persona añadida correctamente");
                eliminarValores(); // Limpiar campos tras añadir
            } else {
                EjercicioG_Principal_Control.ventanaAlerta("E", "La persona ya existe");
            }
        } catch (NullPointerException e) {
            EjercicioG_Principal_Control.ventanaAlerta("E", camposNulos);
        } catch (NumberFormatException e) {
            EjercicioG_Principal_Control.ventanaAlerta("E", "El valor de edad debe ser un número mayor que cero");
        }
    }

    /**
     * Modifica los datos de una persona existente en la tabla y base de datos.
     * Muestra mensajes de alerta en caso de error.
     */
    private void modificar() {
        camposNulos = "";
        try {
            // Comprobar que los campos no son nulos
            camposNulos = comprobarCampos();
            if (!camposNulos.equals("")) throw new NullPointerException();
            if (Integer.parseInt(txtEdad.getText()) < 1) throw new NumberFormatException();

            // Crear nueva persona para la modificación
            Persona pAux = new Persona(txtNombre.getText(), txtApellidos.getText(), Integer.parseInt(txtEdad.getText()), EjercicioG_Principal_Control.p.getId());
            if (!EjercicioG_Principal_Control.listaPersonas.contains(pAux)) {
                // Modificar persona
                EjercicioG_Principal_Control.pDao.modificarPersona(EjercicioG_Principal_Control.p, pAux);
                EjercicioG_Principal_Control.listaPersonas.remove(EjercicioG_Principal_Control.p);
                EjercicioG_Principal_Control.listaFiltrada.remove(EjercicioG_Principal_Control.p);
                EjercicioG_Principal_Control.listaPersonas.add(pAux);
                EjercicioG_Principal_Control.listaFiltrada.add(pAux);
                EjercicioG_Principal_Control.ventanaAlerta("I", "Persona modificada correctamente");
                eliminarValores(); // Limpiar campos tras modificar
            } else {
                EjercicioG_Principal_Control.ventanaAlerta("E", "Persona existente");
            }
        } catch (NullPointerException e) {
            EjercicioG_Principal_Control.ventanaAlerta("E", camposNulos);
        }
    }

    /**
     * Vacía los valores de los campos de texto después de guardar.
     */
    private void eliminarValores() {
        txtNombre.clear();
        txtApellidos.clear();
        txtEdad.clear();
    }

    /**
     * Comprueba que los campos de texto no están vacíos.
     *
     * @return Un mensaje con los campos que están vacíos, si los hay.
     */
    private String comprobarCampos() {
        String sCamposNulos = "";
        if (txtNombre.getText().equals("")) sCamposNulos = "El campo nombre es obligatorio\n";
        if (txtApellidos.getText().equals("")) sCamposNulos += "El campo apellidos es obligatorio\n";
        if (txtEdad.getText().isEmpty()) sCamposNulos += "El campo edad es obligatorio";
        return sCamposNulos;
    }
}


