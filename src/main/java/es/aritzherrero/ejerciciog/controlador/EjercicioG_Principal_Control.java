package es.aritzherrero.ejerciciog.controlador;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import es.aritzherrero.ejerciciog.HelloApplication;
import es.aritzherrero.ejerciciog.dao.PersonaDAO;
import es.aritzherrero.ejerciciog.modelo.Persona;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Controlador principal de la aplicación EjercicioG.
 * Este controlador maneja las interacciones entre la interfaz gráfica y la base de datos de personas.
 *
 * Implementa la interfaz `Initializable` para cargar los datos de la base de datos al iniciar el programa.
 */
public class EjercicioG_Principal_Control implements Initializable {

    // Botones de la interfaz
    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnModificar;

    // Tabla y columnas para mostrar personas
    @FXML
    private TableView<Persona> tblvTabla;

    @FXML
    private TableColumn<Persona, String> tblcApellidos;

    @FXML
    private TableColumn<Persona, Integer> tblcEdad;

    @FXML
    private TableColumn<Persona, String> tblcNombre;

    // Campo de texto para filtrar personas por nombre
    @FXML
    private TextField txtFiltrar;

    // Variables de clase
    public static PersonaDAO pDao = new PersonaDAO(); // DAO para acceder a la base de datos
    static ObservableList<Persona> listaPersonas;     // Lista principal de personas
    static ObservableList<Persona> listaFiltrada;     // Lista filtrada de personas
    static Persona p = new Persona("", "", 0);        // Persona seleccionada o nueva

    /**
     * Inicializa la vista y carga los datos desde la base de datos.
     *
     * @param arg0 URL del recurso
     * @param arg1 Conjunto de recursos que se pueden utilizar
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Cargar las listas de personas
        listaPersonas = pDao.cargarPersonas();
        listaFiltrada = pDao.cargarPersonas();

        // Asociar las columnas de la tabla con las propiedades de la clase Persona
        tblcNombre.setCellValueFactory(new PropertyValueFactory<Persona, String>("nombre"));
        tblcApellidos.setCellValueFactory(new PropertyValueFactory<Persona, String>("apellidos"));
        tblcEdad.setCellValueFactory(new PropertyValueFactory<Persona, Integer>("edad"));

        // Establecer la lista filtrada como contenido de la tabla
        tblvTabla.setItems(listaFiltrada);
        actualizarTablaCompleta(); // Actualizar la tabla con los datos iniciales
    }

    /**
     * Procedimiento para abrir una ventana auxiliar para agregar una nueva persona.
     *
     * @param event El evento de clic del botón "Agregar"
     */
    @FXML
    void agregarPersona(ActionEvent event) {
        // Limpiar los datos de la persona antes de abrir la ventana
        p.setNombre("");
        p.setApellidos("");
        p.setEdad(0);
        crearVentanaAux(); // Crear la ventana para añadir una nueva persona
        actualizarTablaCompleta(); // Actualizar la tabla con la nueva persona
    }

    /**
     * Procedimiento para eliminar una persona seleccionada de la tabla.
     *
     * Si no se selecciona ninguna persona, se muestra una alerta.
     *
     * @param event El evento de clic del botón "Eliminar"
     */
    @FXML
    void eliminarPersona(ActionEvent event) {
        try {
            // Obtener la persona seleccionada de la tabla
            Persona p = tblvTabla.getSelectionModel().getSelectedItem();
            listaPersonas.remove(p);   // Eliminar la persona de la lista principal
            listaFiltrada.remove(p);   // Eliminar la persona de la lista filtrada
            pDao.eliminarPersona(p);   // Eliminar la persona de la base de datos
            ventanaAlerta("I", "Persona eliminada correctamente"); // Mostrar confirmación
            actualizarTablaCompleta(); // Actualizar la tabla
        } catch (NullPointerException e) {
            // Mostrar alerta si no hay ninguna persona seleccionada
            ventanaAlerta("E", "Seleccione un registro de la tabla. Si no lo hay, añada uno.");
        }
    }

    /**
     * Procedimiento para modificar una persona seleccionada de la tabla.
     *
     * Si no se selecciona ninguna persona, se muestra una alerta.
     *
     * @param event El evento de clic del botón "Modificar"
     */
    @FXML
    void modificarPersona(ActionEvent event) {
        try {
            // Cargar los datos de la persona seleccionada para modificarlos
            p.setNombre(tblvTabla.getSelectionModel().getSelectedItem().getNombre());
            p.setApellidos(tblvTabla.getSelectionModel().getSelectedItem().getApellidos());
            p.setEdad(tblvTabla.getSelectionModel().getSelectedItem().getEdad());
            p.setId(tblvTabla.getSelectionModel().getSelectedItem().getId());
            crearVentanaAux(); // Abrir la ventana para modificar los datos
            actualizarTablaCompleta(); // Actualizar la tabla con los nuevos datos
        } catch (NullPointerException e) {
            // Mostrar alerta si no hay ninguna persona seleccionada
            ventanaAlerta("E", "Seleccione un registro de la tabla. Si no lo hay, añada uno.");
        }
    }

    /**
     * Procedimiento para filtrar las personas por nombre en la tabla.
     *
     * Cada vez que se introduce o elimina un carácter en el campo de texto, la tabla se actualiza.
     *
     * @param event El evento de teclado que provoca el filtrado
     */
    @FXML
    void filtrarTabla(KeyEvent event) {
        // Obtener el texto del filtro y convertirlo a minúsculas
        String sFiltro = txtFiltrar.getText().toLowerCase();

        // Limpiar la lista filtrada antes de aplicar el filtro
        listaFiltrada.clear();
        for (Persona p : listaPersonas) {
            // Si el nombre de la persona contiene el texto del filtro, agregarla a la lista filtrada
            if (p.getNombre().toLowerCase().contains(sFiltro)) {
                listaFiltrada.add(p);
            }
        }

        // Actualizar la tabla con la lista filtrada
        tblvTabla.setItems(listaFiltrada);
    }

    /*
     * Métodos auxiliares
     */

    /**
     * Muestra una ventana de alerta.
     *
     * @param tipoAlerta Tipo de alerta: "E" para error, "I" para información
     * @param mensaje El mensaje a mostrar en la alerta
     */
    static void ventanaAlerta(String tipoAlerta, String mensaje) {
        Alert alert = null;
        switch (tipoAlerta) {
            case ("E"): // Alerta de error
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            case ("I"): // Alerta informativa
                alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setContentText(mensaje); // Establecer el mensaje en la alerta
        alert.showAndWait(); // Mostrar la alerta y esperar a que el usuario la cierre
    }

    /**
     * Crea una ventana auxiliar para agregar o modificar una persona.
     */
    void crearVentanaAux() {
        Stage escena = new Stage(); // Crear una nueva ventana
        escena.setTitle("NUEVA PERSONA"); // Establecer el título de la ventana
        FlowPane flwPanel;

        try {
            // Cargar el archivo FXML para la nueva persona
            flwPanel = FXMLLoader.load(HelloApplication.class.getResource("fxml/ejerciciog_nuevapersona.fxml"));
            Scene scene = new Scene(flwPanel, 600, 300);
            escena.setScene(scene);

            // Configurar las dimensiones mínimas de la ventana
            escena.setMinHeight(300);
            escena.setMinWidth(600);
            escena.show();
        } catch (IOException e) {
            // Si hay un error al cargar la vista, mostrar el error en la consola
            System.out.println("No ha sido posible abrir la ventana");
            e.printStackTrace();
        }
    }

    /**
     * Comprueba si una persona tiene datos válidos.
     *
     * @param p La persona a comprobar
     * @return boolean true si la persona es válida, false si no lo es
     */
    boolean comprobarPersona(Persona p) {
        boolean correcto = true;

        if (p.getNombre().isEmpty()) correcto = false;
        if (p.getApellidos().isEmpty()) correcto = false;
        if (p.getEdad() == 0) correcto = false;

        return correcto;
    }

    /**
     * Actualiza la tabla con la lista completa de personas.
     */
    void actualizarTablaCompleta() {
        tblvTabla.setItems(listaPersonas); // Establecer la lista completa de personas en la tabla
    }
}