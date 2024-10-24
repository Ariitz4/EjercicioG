package es.aritzherrero.ejerciciog.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.aritzherrero.ejerciciog.Conexion.ConexionMariaDB;
import es.aritzherrero.ejerciciog.modelo.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Esta clase gestiona las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * sobre la tabla "Persona" en la base de datos.
 *
 * La clase utiliza el patrón DAO (Data Access Object) para interactuar con la base de datos.
 * Contiene métodos para cargar todas las personas, insertar una nueva persona,
 * eliminar una persona existente y modificar la información de una persona.
 *
 * La conexión con la base de datos se realiza a través de la clase ConexionMariaDB.
 */
public class PersonaDAO {

    // Objeto de conexión a la base de datos
    private ConexionMariaDB conexion;

    /**
     * Carga todas las personas almacenadas en la base de datos.
     *
     * Este procediento ejecuta una consulta SQL para recuperar todos los registros
     * de la tabla "Persona" y devuelve una lista observable de objetos Persona.
     *
     * @return ObservableList<Persona> Lista de objetos Persona con todos los registros de la base de datos.
     */
    public ObservableList<Persona> cargarPersonas() {

        // Crear una lista observable para almacenar las personas
        ObservableList<Persona> persona = FXCollections.observableArrayList();

        try {
            // Establecer conexión con la base de datos
            conexion = new ConexionMariaDB();
            // Consulta SQL para obtener todas las personas
            String consulta = "SELECT * FROM Persona;";
            // Preparar la sentencia SQL
            PreparedStatement pstmt = conexion.getConexion().prepareStatement(consulta);
            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();

            // Recorrer los resultados de la consulta y crear objetos Persona
            while (rs.next()) {
                int idPersona = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                int edad = rs.getInt("edad");
                Persona p = new Persona(nombre, apellidos, edad, idPersona);
                persona.add(p); // Agregar la persona a la lista
            }

            // Cerrar el ResultSet después de su uso
            rs.close();

        } catch (SQLException e) {
            // Manejo de excepciones de SQL
            e.printStackTrace();
        }

        // Devolver la lista de personas cargadas
        return persona;
    }

    /**
     *
     * Este procdimiento ejecuta una consulta SQL de inserción para agregar un nuevo registro
     * en la tabla "Persona" y devuelve el ID generado para la nueva persona.
     *
     * @param p Objeto Persona que contiene los datos a insertar.
     * @return int El ID de la nueva persona generada por la base de datos o -1 si ocurre un error.
     */
    public int insertarPersona(Persona p) {
        // Consulta SQL para insertar una nueva persona en la tabla
        String consulta = "INSERT INTO Persona(nombre,apellidos,edad) values('" + p.getNombre() + "','" + p.getApellidos() + "'," + p.getEdad() + ")";

        try {
            // Establecer conexión con la base de datos
            conexion = new ConexionMariaDB();
            // Preparar la sentencia SQL para obtener el ID generado
            PreparedStatement ps = conexion.getConexion().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            // Ejecutar la consulta de inserción
            ps.executeUpdate();

            // Obtener el ID generado automáticamente por la base de datos
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            p.setId(rs.getInt(1)); // Asignar el ID a la persona

            // Devolver el ID de la persona insertada
            return p.getId();

        } catch (SQLException e) {
            // Manejo de excepciones de SQL
            e.printStackTrace();
            return -1; // En caso de error, devolver -1
        }
    }

    /**
     * Elimina una persona existente de la base de datos.
     *
     * @param p Objeto Persona que contiene el ID de la persona a eliminar.
     * @return boolean true si la eliminación fue exitosa, false si ocurre un error.
     */
    public boolean eliminarPersona(Persona p) {
        try {
            // Establecer conexión con la base de datos
            conexion = new ConexionMariaDB();
            // Consulta SQL para eliminar una persona de la tabla
            String consulta = "DELETE FROM Persona WHERE id = " + p.getId() + ";";
            PreparedStatement ps = conexion.getConexion().prepareStatement(consulta);
            // Ejecutar la consulta de eliminación
            ps.executeUpdate();

            // Cerrar la conexión después de la operación
            conexion.CloseConexion();
            return true; // Devolver true si se eliminó correctamente

        } catch (SQLException e) {
            // Manejo de excepciones de SQL
            return false; // Devolver false en caso de error
        }
    }

    /**
     * Modifica los datos de una persona existente en la base de datos.
     *
     * @param pAnt Objeto Persona que representa los datos originales de la persona.
     * @param p Objeto Persona que contiene los nuevos datos a actualizar.
     * @return boolean true si la modificación fue exitosa, false si ocurre un error.
     */
    public boolean modificarPersona(Persona pAnt, Persona p) {
        try {
            // Establecer conexión con la base de datos
            conexion = new ConexionMariaDB();
            // Consulta SQL para actualizar los datos de la persona
            String consulta = "UPDATE Persona SET nombre = '" + p.getNombre() + "', apellidos = '" + p.getApellidos() + "', edad = " + p.getEdad() + " WHERE id = " + pAnt.getId() + ";";
            PreparedStatement ps = conexion.getConexion().prepareStatement(consulta);
            // Ejecutar la consulta de actualización
            ps.executeUpdate();

            // Cerrar la conexión después de la operación
            conexion.CloseConexion();
            return true; // Devolver true si se actualizó correctamente

        } catch (SQLException e) {
            // Manejo de excepciones de SQL
            e.printStackTrace();
            return false; // Devolver false en caso de error
        }
    }
}
