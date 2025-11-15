package org.lsz.uvm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Clase para generar funciones relacionadas al diccionario de aminoacidos
 *
 * @author [25-10-2025] Lizbeth Sanchez Zambrano
 * @version 1.0
 * @since 20/10/2025
 */
public class DiccionarioAminoacidos {

    /**
     *  Realiza una lectura de un archivo de aminoacidos y llena
     *  un mapa con la informacion sobre aminoacido y codon
     *  correspondiente
     *
     * @return Un mapa con los aminoacidos y su codon
     */
    public static Map<String, String> crearDiccionarioAminoacidos(){
        // Declara el mapa que almacenará el diccionario.
        Map<String, String> mapaAminoacidos = new HashMap<>();
        // Inicio de manejo de excepciones
        try {
            // Creacion de instancia de archivo para realizar la lectura del archivo de
            // aminoacidos
            File archivoAminoacidos = new File("diccionario/aminoacidos.txt");
            // Valida si el archivo existe
            if(archivoAminoacidos.exists()){
                // Se crea instancia para poder leer el archivo linea por linea
                BufferedReader br = new BufferedReader(new FileReader(archivoAminoacidos));
                // Se crea variable para almacenar el contenido de cada linea
                String linea = "";
                // Sentencia de control para validar cuando ya se leyo la ultima linea
                while((linea = br.readLine()) != null) {
                    String delimeter = "-";
                    // Almacena el codigo genetico del codon
                    String codigoGenetico = linea.substring(0, linea.indexOf(delimeter));
                    // Almacena el nombre del aminoacido
                    String aminoacido = linea.substring(linea.indexOf(delimeter) + 1);
                    // Se agrega el codon y el aminoacido a un mapa en formato clave-valor
                    mapaAminoacidos.put(codigoGenetico, aminoacido);
                }
                br.close();
            }
            // Bloque de codigo para manejo de errores
        } catch (Exception e){
            e.getCause();
            // Bloque de codigo para sentencias no dependientes del manejo de
            // excepciones
        }
        return mapaAminoacidos;
    }


}