package lotrec.guifx.graph;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.awt.*;

/**
 * Bridge component that embeds Cytoscape's Swing-based graph visualization
 * into the JavaFX UI via SwingNode.
 */
public class CytoscapeSwingBridge extends StackPane {

    private final SwingNode swingNode;
    private final Label errorLabel;
    private boolean showingError;

    public CytoscapeSwingBridge() {
        swingNode = new SwingNode();
        errorLabel = new Label("No graph to display");
        errorLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        getChildren().addAll(swingNode, errorLabel);
        showingError = false;

        // Initialize with empty panel on EDT
        SwingUtilities.invokeLater(() -> {
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setBackground(Color.WHITE);
            swingNode.setContent(placeholder);
        });
    }

    /**
     * Displays a Cytoscape JComponent (desktop pane or network view) in the bridge.
     */
    public void displayComponent(JComponent component) {
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            showingError = false;
        });
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(component);
        });
    }

    /**
     * Shows an error message in the graph area.
     */
    public void showError(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setStyle("-fx-text-fill: #c00; -fx-font-size: 14;");
            showingError = true;
        });
    }

    /**
     * Clears the graph display.
     */
    public void clear() {
        Platform.runLater(() -> {
            errorLabel.setText("No graph to display");
            errorLabel.setVisible(true);
            errorLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");
            showingError = false;
        });
        SwingUtilities.invokeLater(() -> {
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setBackground(Color.WHITE);
            swingNode.setContent(placeholder);
        });
    }

    public SwingNode getSwingNode() { return swingNode; }
    public boolean isShowingError() { return showingError; }
}
