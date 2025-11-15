package org.lsz.uvm.gui;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

// Se asumen las importaciones de tus clases de utilidad
// import org.lsz.uvm.main.ARN;
// import org.lsz.uvm.util.DiccionarioAminoacidos;
import java.io.IOException;
import java.util.Map;

/**
 * Interfaz Gráfica de Simulación Ribosomal usando JavaFX.
 * Muestra el movimiento codón por codón para los alelos COL5A1 (Normal) y cEDS (Mutado).
 */
public class SimulacionRibosomalGUI extends Application {

    // --- ESTADO DE LA SIMULACIÓN ---
    private Map<String, String> mapaAminoacidos;
    private String secuenciaARN;
    private int punteroInicial = 0;
    private int aminoacidosEncontrados = 0;
    private StringBuilder secuenciaAminoacidos = new StringBuilder();
    private Timeline simulationTimer;
    private boolean isSimulationActive = false;

    // --- COMPONENTES UI CLAVE ---
    private Pane simulationCanvas;
    private Label aminoAcidChainLabel;
    private Label aminoAcidCountLabel;
    private Label currentCodonLabel;
    private Label currentEventLabel;
    private ChoiceBox<String> proteinSelector;

    // --- COMPONENTES VISUALES (para animación) ---
    private Rectangle smallSubunit; // Subunidad menor del Ribosoma
    private Rectangle largeSubunit; // Subunidad mayor del Ribosoma
    private HBox mrnaContainer;    // Contenedor del ARNm (lo que se mueve)

    // Constantes de diseño
    private final double BASE_WIDTH = 25; // Ancho visual de cada base
    private final double START_X = 200;   // Posición X donde el ribosoma 'espera' el ARNm (sitio P)

    // --- CONSTANTES DE POSICIÓN Y DISEÑO ---
    private final double MRNA_CHANNEL_Y = 360;
    private final double CHANNEL_SPACE = 35;

    // Alturas de las subunidades
    private final double SMALL_SUBUNIT_HEIGHT = 25;
    private final double LARGE_SUBUNIT_HEIGHT = 50;

    // ANCHOS DE LAS SUBUNIDADES
    private final double SMALL_SUBUNIT_WIDTH = 80;
    private final double LARGE_SUBUNIT_WIDTH = 90;

    // Posiciones Verticales Calculadas
    private final double SMALL_SUBUNIT_Y_POS = MRNA_CHANNEL_Y - SMALL_SUBUNIT_HEIGHT - (CHANNEL_SPACE / 2) + 10;
    private final double LARGE_SUBUNIT_Y_POS = MRNA_CHANNEL_Y + (CHANNEL_SPACE / 2);
    private final double MRNA_CONTAINER_Y_POS = MRNA_CHANNEL_Y - 10;
    // ----------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        // Se asume la existencia de la clase DiccionarioAminoacidos
        try {
            Class<?> dictClass = Class.forName("org.lsz.uvm.util.DiccionarioAminoacidos");
            java.lang.reflect.Method createMethod = dictClass.getMethod("crearDiccionarioAminoacidos");
            mapaAminoacidos = (Map<String, String>) createMethod.invoke(null);
        } catch (Exception e) {
            System.err.println("Error al cargar DiccionarioAminoacidos: " + e.getMessage());
            mapaAminoacidos = new java.util.HashMap<>();
        }

        BorderPane root = new BorderPane();
        root.setTop(createHeader());

        simulationCanvas = createSimulationCanvas();
        root.setCenter(simulationCanvas);

        root.setRight(createInfoPanelWithScroll());

        root.setBottom(createControlPanel());

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setTitle("Simulación Ribosomal: COL5A1 vs. cEDS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Envuelve el panel de información en un ScrollPane.
     * @return ScrollPane con el VBox de información.
     */
    private ScrollPane createInfoPanelWithScroll() {
        VBox infoPanel = createInfoPanel();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(infoPanel);

        // Ajustes para el ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefWidth(400);

        return scrollPane;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #006064;");

        Label title = new Label("Simulador de Traducción (COL5A1 / cEDS)");
        title.setFont(new Font("Arial Bold", 24));
        title.setTextFill(Color.WHITE);

        Label selectLabel = new Label("Cargar Alelo:");
        selectLabel.setFont(new Font("Arial", 16));
        selectLabel.setTextFill(Color.WHITE);

        proteinSelector = new ChoiceBox<>();
        proteinSelector.getItems().addAll("COL5A1 (Normal)", "COL5A1_cEDS (Mutado)");
        proteinSelector.setValue("Seleccionar Proteína");

        Button loadButton = new Button("Cargar");
        loadButton.setOnAction(e -> {
            try {
                // Al cargar la proteína, se llama a loadProtein, que reinicia el estado lógico.
                loadProtein(proteinSelector.getValue().split(" ")[0]);
            } catch (Exception ex) {
                currentEventLabel.setText("ERROR: No se pudo cargar la proteína.");
            }
        });
        loadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        header.getChildren().addAll(title, new Separator(), selectLabel, proteinSelector, loadButton);
        return header;
    }

    private Pane createSimulationCanvas() {
        Pane canvas = new Pane();
        canvas.setPrefSize(800, 500);
        canvas.setStyle("-fx-background-color: #e0f7fa;");

        drawRibosome(canvas);

        return canvas;
    }

    private void drawRibosome(Pane canvas) {

        final double LARGE_OFFSET_X = LARGE_SUBUNIT_WIDTH / 2.0;
        final double SMALL_OFFSET_X = SMALL_SUBUNIT_WIDTH / 2.0;

        // Subunidad Menor (la de arriba) - Centrada en START_X
        smallSubunit = new Rectangle(START_X - SMALL_OFFSET_X, SMALL_SUBUNIT_Y_POS, SMALL_SUBUNIT_WIDTH, SMALL_SUBUNIT_HEIGHT);
        smallSubunit.setFill(Color.web("#81D4FA"));
        smallSubunit.setStroke(Color.web("#039BE5"));
        smallSubunit.setStrokeWidth(2);
        smallSubunit.setArcWidth(10);
        smallSubunit.setArcHeight(10);
        smallSubunit.setEffect(new javafx.scene.effect.DropShadow());

        // Subunidad Mayor (la de abajo) - Centrada en START_X
        largeSubunit = new Rectangle(START_X - LARGE_OFFSET_X, LARGE_SUBUNIT_Y_POS, LARGE_SUBUNIT_WIDTH, LARGE_SUBUNIT_HEIGHT);
        largeSubunit.setFill(Color.web("#4DB6AC"));
        largeSubunit.setStroke(Color.web("#00897B"));
        largeSubunit.setStrokeWidth(2);
        largeSubunit.setArcWidth(20);
        largeSubunit.setArcHeight(20);
        largeSubunit.setEffect(new javafx.scene.effect.DropShadow());

        canvas.getChildren().addAll(smallSubunit, largeSubunit);
    }

    /**
     * Dibuja la secuencia de ARNm dentro de un contenedor (HBox).
     */
    private void drawmRNA(String sequence) {
        // Eliminar el contenedor anterior
        if (mrnaContainer != null) {
            simulationCanvas.getChildren().remove(mrnaContainer);
        }

        // Si la secuencia es nula o vacía, no se dibuja nada
        if (sequence == null || sequence.isEmpty()) {
            mrnaContainer = null;
            return;
        }

        mrnaContainer = new HBox(0); // 0 de espaciado entre bases
        mrnaContainer.setLayoutY(MRNA_CONTAINER_Y_POS);
        mrnaContainer.setLayoutX(START_X - BASE_WIDTH * 2);

        // Dibuja la secuencia completa
        for (int i = 0; i < sequence.length(); i++) {
            String base = String.valueOf(sequence.charAt(i));

            Label baseLabel = new Label(base);
            baseLabel.setFont(new Font("Courier New Bold", 20));

            // Colores para diferenciar bases
            Color color;
            if (base.equals("A")) color = Color.web("#EF5350");
            else if (base.equals("U")) color = Color.web("#FFC107");
            else if (base.equals("G")) color = Color.web("#66BB6A");
            else if (base.equals("C")) color = Color.web("#42A5F5");
            else color = Color.BLACK;

            baseLabel.setTextFill(color);

            baseLabel.setMinWidth(BASE_WIDTH);
            baseLabel.setAlignment(Pos.CENTER);

            mrnaContainer.getChildren().add(baseLabel);
        }

        simulationCanvas.getChildren().add(mrnaContainer);
    }

    /**
     * Crea el panel de información que muestra el estado y la cadena de aminoácidos.
     */
    private VBox createInfoPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #00838f;");

        // Sección de Codón Actual
        Label codonTitle = new Label("Codón Actual:");
        codonTitle.setFont(new Font("Arial Bold", 18));
        codonTitle.setTextFill(Color.WHITE);
        currentCodonLabel = new Label("---");
        currentCodonLabel.setFont(new Font("Courier New Bold", 24));
        currentCodonLabel.setTextFill(Color.web("#ffff00"));

        // Sección de Contador de Aminoácidos
        Label countTitle = new Label("Aminoácidos Encontrados:");
        countTitle.setFont(new Font("Arial Bold", 18));
        countTitle.setTextFill(Color.WHITE);
        aminoAcidCountLabel = new Label("0");
        aminoAcidCountLabel.setFont(new Font("Courier New Bold", 24));
        aminoAcidCountLabel.setTextFill(Color.web("#a7ffeb"));


        // Sección de Cadena Peptídica
        Label seqTitle = new Label("Cadena Peptídica:");
        seqTitle.setFont(new Font("Arial Bold", 18));
        seqTitle.setTextFill(Color.WHITE);

        aminoAcidChainLabel = new Label("Aún no iniciada...");
        aminoAcidChainLabel.setWrapText(true); // Se reactiva el wrap para ver la cadena en el ancho del panel.
        aminoAcidChainLabel.setFont(new Font("Courier New", 14));
        aminoAcidChainLabel.setTextFill(Color.web("#a7ffeb"));

        // Sección de Evento
        Label eventTitle = new Label("\nEVENTO:");
        eventTitle.setFont(new Font("Arial Bold", 18));
        eventTitle.setTextFill(Color.WHITE);
        currentEventLabel = new Label("Cargue una proteína para empezar.");
        currentEventLabel.setFont(new Font("Arial", 16));
        currentEventLabel.setTextFill(Color.web("#ffffff"));


        panel.getChildren().addAll(
                codonTitle,
                currentCodonLabel,
                countTitle,
                aminoAcidCountLabel,
                seqTitle,
                aminoAcidChainLabel,
                eventTitle,
                currentEventLabel
        );
        return panel;
    }

    private HBox createControlPanel() {
        HBox panel = new HBox(20);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #cccccc;");
        panel.setAlignment(Pos.CENTER);

        // Botones de control
        Button startButton = new Button("Iniciar");
        startButton.setOnAction(e -> startSimulation());

        Button pauseButton = new Button("Pausa/Reanudar");
        pauseButton.setOnAction(e -> toggleSimulation());

        Button stepButton = new Button("Paso Único");
        stepButton.setOnAction(e -> stepTranslation());

        // NUEVO BOTÓN DE REINICIO
        Button resetButton = new Button("Reiniciar Todo");
        resetButton.setOnAction(e -> resetSimulation());

        // Configuración del Timer de animación (simulación automática)
        simulationTimer = new Timeline(new javafx.animation.KeyFrame(Duration.millis(500), e -> stepTranslation()));
        simulationTimer.setCycleCount(Timeline.INDEFINITE);

        // Estilos
        String style = "-fx-font-size: 14px; -fx-padding: 10 20; -fx-text-fill: white; -fx-font-weight: bold;";
        startButton.setStyle(style + "-fx-background-color: #4CAF50;");
        pauseButton.setStyle(style + "-fx-background-color: #ff9800;");
        stepButton.setStyle(style + "-fx-background-color: #2196F3;");
        resetButton.setStyle(style + "-fx-background-color: #D32F2F;"); // Rojo

        panel.getChildren().addAll(startButton, pauseButton, stepButton, resetButton);
        return panel;
    }

    /**
     * Reinicia completamente el estado lógico y visual de la simulación.
     */
    private void resetSimulation() {
        // 1. Detener el timer
        simulationTimer.stop();
        isSimulationActive = false;

        // 2. Reiniciar estado lógico
        punteroInicial = 0;
        aminoacidosEncontrados = 0;
        secuenciaAminoacidos = new StringBuilder();
        secuenciaARN = null;

        // 3. Reiniciar el estado visual
        currentCodonLabel.setText("---");
        aminoAcidCountLabel.setText("0");
        aminoAcidChainLabel.setText("Aún no iniciada...");
        currentEventLabel.setText("Simulación Reiniciada. Cargue una proteína.");

        // 4. Quitar el ARNm del lienzo
        if (mrnaContainer != null) {
            simulationCanvas.getChildren().remove(mrnaContainer);
            mrnaContainer = null;
        }

        // 5. Reiniciar la selección de proteína
        proteinSelector.setValue("Seleccionar Proteína");
    }

    private void startSimulation() {
        if (secuenciaARN == null || secuenciaARN.isEmpty()) {
            currentEventLabel.setText("ERROR: Primero debe cargar una secuencia.");
            return;
        }
        isSimulationActive = true;
        simulationTimer.play();
        currentEventLabel.setText("Traducción iniciada. Velocidad: Normal.");
    }

    private void toggleSimulation() {
        if (isSimulationActive) {
            simulationTimer.pause();
            isSimulationActive = false;
            currentEventLabel.setText("Traducción en pausa.");
        } else {
            simulationTimer.play();
            isSimulationActive = true;
            currentEventLabel.setText("Traducción reanudada.");
        }
    }

    private void loadProtein(String proteina) {
        // Aseguramos que cualquier simulación previa se detenga
        simulationTimer.stop();
        isSimulationActive = false;

        try {
            // Reinicia el estado para la nueva proteína
            punteroInicial = 0;
            aminoacidosEncontrados = 0;
            secuenciaAminoacidos = new StringBuilder();

            // Llama a la función de tu clase ARN para cargar y limpiar la secuencia
            String ARN_CLASS_NAME = "org.lsz.uvm.main.ARN";
            java.lang.reflect.Method method = Class.forName(ARN_CLASS_NAME)
                    .getMethod("procesarDiccionarioProteina", String.class);

            secuenciaARN = (String) method.invoke(null, proteina);


            if (secuenciaARN != null && !secuenciaARN.isEmpty()) {
                // Reinicio visual de etiquetas de estado
                currentEventLabel.setText("Secuencia '" + proteina + "' cargada. Longitud: " + secuenciaARN.length() + " bases.");
                aminoAcidChainLabel.setText("Lista para iniciar...");
                aminoAcidCountLabel.setText(String.valueOf(aminoacidosEncontrados));

                // Dibuja el nuevo ARNm
                drawmRNA(secuenciaARN);

                // Posiciona el ARNm al inicio (TranslateX(0) lo pone en START_X - 2*BASE_WIDTH)
                if (mrnaContainer != null) {
                    mrnaContainer.setTranslateX(0);
                }

                if (secuenciaARN.length() >= 3) {
                    currentCodonLabel.setText(secuenciaARN.substring(0, 3));
                }
            } else {
                // Si la carga falla, reiniciamos el canvas a vacío
                drawmRNA(null);
                secuenciaARN = null;
                currentEventLabel.setText("Error: Archivo no encontrado. Verifique la ruta.");
            }
        } catch (Exception e) {
            currentEventLabel.setText("Error de E/S o reflexión al cargar la proteína.");
            drawmRNA(null);
            secuenciaARN = null;
        }
    }

    /**
     * Mueve visualmente el ARNm debajo del ribosoma, simulando la translocación.
     */
    private void translocatemRNA() {
        // ... (sin cambios)
        final double PIXELS_PER_CODON = BASE_WIDTH * 3;

        double newX = -(punteroInicial / 3) * PIXELS_PER_CODON;

        TranslateTransition tt = new TranslateTransition(Duration.millis(500), mrnaContainer);
        tt.setToX(newX);
        tt.play();
    }


    /**
     * Realiza un único paso de la traducción (1 codón), incluyendo la actualización visual.
     */
    private void stepTranslation() {
        if (secuenciaARN == null || mrnaContainer == null) {
            currentEventLabel.setText("ERROR: Cargue una secuencia primero.");
            simulationTimer.stop();
            isSimulationActive = false;
            return;
        }

        if (punteroInicial + 3 > secuenciaARN.length()) {
            simulationTimer.stop();
            currentCodonLabel.setText("--- / Fin");
            currentEventLabel.setText("TERMINACIÓN DE TRADUCCIÓN. Proteína finalizada. Total: " + aminoacidosEncontrados);
            isSimulationActive = false;
            return;
        }

        int punteroFinal = punteroInicial + 3;
        String codon = secuenciaARN.substring(punteroInicial, punteroFinal);
        String aminoacido = mapaAminoacidos.getOrDefault(codon, "");

        // 1. Detección de Parada
        if (aminoacido.equals("STOP") || aminoacido.equals("")) {
            // Animación de disociación (se desvanecen)
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.millis(1000), largeSubunit);
            ft.setToValue(0);
            ft.play();
            javafx.animation.FadeTransition ft2 = new javafx.animation.FadeTransition(Duration.millis(1000), smallSubunit);
            ft2.setToValue(0);
            ft2.play();

            simulationTimer.stop();
            currentCodonLabel.setText(codon + " (STOP)");
            currentEventLabel.setText("Codón de Parada Prematuro Encontrado. Ribosoma se disocia. Total: " + aminoacidosEncontrados);
            isSimulationActive = false;
        } else {
            // 2. Proceso de Elongación
            // ... (omito manejo de error de mapa)

            aminoacidosEncontrados++;

            secuenciaAminoacidos.append(aminoacido).append("-");
            currentCodonLabel.setText(codon + " -> " + aminoacido);
            currentEventLabel.setText("Elongación: Unión del ARNt con " + aminoacido + ".");

            // ACTUALIZA EL LABEL DE LA CADENA Y EL CONTADOR
            aminoAcidChainLabel.setText(secuenciaAminoacidos.toString());
            aminoAcidCountLabel.setText(String.valueOf(aminoacidosEncontrados));

            // 3. Avance del Puntero Lógico
            punteroInicial = punteroInicial + 3;

            // 4. Animación de Translocación (Movimiento Ribosomal)
            translocatemRNA();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}