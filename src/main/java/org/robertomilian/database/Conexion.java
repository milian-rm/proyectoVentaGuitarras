/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.robertomilian.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Roberto
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/DBconexionGuitarras?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "xEnon0402*";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    
    public Conexion(){
        
    }
    
    public void conectar(){
        try{
            Class.forName(DRIVER).newInstance();
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion realizada con exito");
        } catch(ClassNotFoundException | InstantiationException
                |IllegalAccessException | SQLException ex) {
            System.out.println("Error al conectar");
        }
    }
    
    public static Conexion getInstancia(){
        if (instancia == null) {
            instancia  = new Conexion();
        }
        return instancia;
    }
    
    public Connection getConexion(){
        try{
            if (conexion == null || conexion.isClosed()) {
            conectar(); // reconectar
            }        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    } 
}

