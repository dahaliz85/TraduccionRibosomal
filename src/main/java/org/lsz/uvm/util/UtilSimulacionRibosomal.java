package org.lsz.uvm.util;

/**
 *
 * Clase para generar metodos para generar funcionalidades
 * de formato
 *
 * @author [25-10-2025] Lizbeth Sanchez Zambrano
 * @version 1.0
 * @since 20/10/2025
 *
 */
public class UtilSimulacionRibosomal {

    /**
     * Metodo para remover numeros contenidos en el archivo de la proteina
     *
     * @param lineaInicial El codigo genetico original
     * @return una cadena sin los numeros contenidos en el codigo genetico previo
     */
    public static String removerNumeros(String lineaInicial){
        // Paso 1: Limpieza Absoluta usando Regex.
        // Reemplaza TODO lo que NO sea A, T, C, G (mayúsculas o minúsculas) por una cadena vacía.
        // Esto elimina dígitos, espacios, tabulaciones y caracteres de control de una sola vez.
        String lineaLimpia = lineaInicial.replaceAll("[^atcgATCG]", "");

        // Paso 2: Conversión Segura a ARN.
        if (lineaLimpia.isEmpty()) {
            return "";
        }

        // Convierte todo a mayúsculas y luego cambia T por U.
        return lineaLimpia.toUpperCase().replace('T', 'U');
    }
}