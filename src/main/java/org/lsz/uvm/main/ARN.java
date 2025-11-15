package org.lsz.uvm.main;

import org.lsz.uvm.util.UtilSimulacionRibosomal;

import java.io.*;
import java.util.Map;

/**
 *
 * Clase para generar funciones relacionadas al procesamiento de ARN
 *
 * @author [25-10-2025] Lizbeth Sanchez Zambrano
 * @version 1.0
 * @since 20/10/2025
 */
public class ARN {

    /**
     *
     * Procesa un archivo de texto de una proteína específica para extraer su secuencia de ARNm.
     * Se asume que el archivo está en la carpeta "diccionario" y que su nombre
     * coincide con el parámetro 'proteina' (ej., "COL5A1.txt").
     *
     * @param proteina El nombre de la proteína cuyo archivo ARNm se desea procesar.
     * @return Un string que contiene la secuencia de ARNm limpia y concatenada.
     * Retorna un string vacío si el archivo de la proteína no se encuentra.
     * @throws FileNotFoundException excepcion al no poder encontrar o leer el archivo
     */
    public static String procesarDiccionarioProteina(String proteina) throws IOException {
        try {

            // Muestra en consola la proteína que se está intentando procesar.
            System.out.println("Proteina a procesar: " );

            // Construye la ruta completa al archivo de la proteína.
            // Se asume que hay una carpeta "diccionario" en el mismo directorio que el ejecutable.
            String rutaArchivo = "proteina/" + proteina + ".txt";

            // Intenta abrir el archivo.
            File archivoProteina = new File(rutaArchivo);

            // Verifica si el archivo no pudo ser abierto.
            if (!archivoProteina.exists()) {
                // Muestra un mensaje de error en el flujo de errores estándar.
                System.out.println("Error: No se pudo encontrar la proteina en la base de datos");
                // Retorna un string vacío para indicar un fallo.
                return "";
            }

            BufferedReader bf = new BufferedReader(new FileReader(archivoProteina));
            String linea="";      // Variable para almacenar cada línea leída del archivo.
            StringBuilder cadenaARNm = new StringBuilder(); // Variable para construir la secuencia de ARNm concatenada.

            // Lee el archivo línea por línea hasta el final.
            while ((linea = bf.readLine())!= null) {
                // 1. Limpieza rigurosa: usa un regex para remover cualquier cosa que no sea A, T, C, G (números, espacios, caracteres ocultos, etc.)
                // La expresión "[^atcgATCG]" remueve todo lo que NO sea A, T, C, G
                String lineaLimpia = linea.replaceAll("[^atcgATCG]", "");
                // 2. Procesa la línea limpia (convierte 'T' a 'U' y pone en mayúsculas)
                if (!lineaLimpia.isEmpty()) {
                    cadenaARNm.append(UtilSimulacionRibosomal.removerNumeros(lineaLimpia));
                }

            }
            bf.close();
            // Retorna la secuencia de ARNm limpia y completa.
            return cadenaARNm.toString();
        }catch(Exception e) {
            e.getCause();
        }
        return proteina;
    }
    /**
     *
     * Simula el proceso ribosomal de traducción de una secuencia de ARNm a una cadena
     * de aminoácidos. Busca el codón de inicio (AUG) y traduce los codones subsiguientes
     * hasta encontrar un codón de terminación.
     *
     * @param secuenciaARN La cadena de ARNm a ser procesada.
     * @param mapaAminoacidos Un mapa que contiene la correspondencia entre codones y
     *                        aminoácidos.
     */
    public static void procesarSecuenciaARN(String secuenciaARN, Map<String, String> mapaAminoacidos){
        boolean isMetionina = false;
        int punteroInicial = 0;
        int punteroFinal = punteroInicial+3;
        int aminoacidosEncontrados = 0;
        String aminoacido = "";
        String codon = "";
        StringBuilder secuenciaAminoacidos = new StringBuilder();

        while(punteroInicial + 3 <= secuenciaARN.length()){
            punteroFinal = punteroInicial+3;
            codon = secuenciaARN.substring(punteroInicial, punteroFinal);
            aminoacido = (mapaAminoacidos.containsKey(codon) ? mapaAminoacidos.get(codon) : "");
            if(aminoacido.equals("") || aminoacido.equals("STOP")){
                    break;
            }
            aminoacidosEncontrados++;
            secuenciaAminoacidos.append(aminoacido+" --> ");
            punteroInicial = punteroInicial +3;

        }
        System.out.println("AMINOACIDOS ENCONTRADOS: " + aminoacidosEncontrados);
        System.out.println("SECUENCIA DE AMINOACIDOS ENCONTRADA: \n");
        System.out.println(secuenciaAminoacidos.toString());
    }


}
