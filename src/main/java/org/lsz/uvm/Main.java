package org.lsz.uvm;

import javafx.application.Application;
import org.lsz.uvm.gui.SimulacionRibosomalGUI;
import org.lsz.uvm.main.ARN;
import org.lsz.uvm.util.DiccionarioAminoacidos;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Función principal del programa.
 * Orquesta la ejecución: pide la proteína, carga su ARNm, crea el diccionario
 * de aminoácidos y procesa la secuencia.
 *
 * @return 0 si el programa finaliza correctamente.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Application.launch(SimulacionRibosomalGUI.class, args);
    }
}