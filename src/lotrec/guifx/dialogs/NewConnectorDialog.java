package lotrec.guifx.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lotrec.dataStructure.expression.Connector;

public class NewConnectorDialog extends Dialog<Connector> {

    private final TextField nameField;
    private final TextField arityField;
    private final TextField outputFormatField;
    private final TextField priorityField;
    private final CheckBox associativeCheck;
    private final TextArea commentArea;

    public NewConnectorDialog(Stage owner) {
        this(owner, null);
    }

    public NewConnectorDialog(Stage owner, Connector existing) {
        setTitle(existing == null ? "New Connector" : "Edit Connector");
        initOwner(owner);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("e.g. not, and, nec");
        arityField = new TextField();
        arityField.setPromptText("e.g. 1, 2");
        outputFormatField = new TextField();
        outputFormatField.setPromptText("e.g. ~_ or (_ & _)");
        priorityField = new TextField();
        priorityField.setPromptText("e.g. 0");
        associativeCheck = new CheckBox("Associative");
        commentArea = new TextArea();
        commentArea.setPrefRowCount(3);
        commentArea.setPromptText("Optional comment");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Arity:"), 0, 1);
        grid.add(arityField, 1, 1);
        grid.add(new Label("Output Format:"), 0, 2);
        grid.add(outputFormatField, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityField, 1, 3);
        grid.add(associativeCheck, 1, 4);
        grid.add(new Label("Comment:"), 0, 5);
        grid.add(commentArea, 1, 5);

        if (existing != null) {
            nameField.setText(existing.getName());
            arityField.setText(String.valueOf(existing.getArity()));
            outputFormatField.setText(existing.getOutString());
            priorityField.setText(String.valueOf(existing.getPriority()));
            associativeCheck.setSelected(existing.isAssociative());
            if (existing.getComment() != null) {
                commentArea.setText(existing.getComment());
            }
        } else {
            arityField.setText("1");
            outputFormatField.setText("(_)");
            priorityField.setText("0");
        }

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Validation on OK
        final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String error = validate();
            if (error != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, error, ButtonType.OK);
                alert.initOwner(owner);
                alert.showAndWait();
                event.consume();
            }
        });

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Connector conn = existing != null ? existing : new Connector();
                conn.setName(nameField.getText().trim());
                conn.setArity(Integer.parseInt(arityField.getText().trim()));
                conn.setOutString(outputFormatField.getText().trim());
                conn.setPriority(Integer.parseInt(priorityField.getText().trim()));
                conn.setAssociative(associativeCheck.isSelected());
                conn.setComment(commentArea.getText().trim());
                return conn;
            }
            return null;
        });
    }

    private String validate() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            return "Connector name is required.";
        }
        if (name.equals("variable") || name.equals("constant")) {
            return "'" + name + "' is a reserved keyword.";
        }
        if (Character.isUpperCase(name.charAt(0)) || name.startsWith("_")) {
            return "Connector name must not start with uppercase or underscore.";
        }
        try {
            int arity = Integer.parseInt(arityField.getText().trim());
            if (arity < 0) {
                return "Arity must be >= 0.";
            }
        } catch (NumberFormatException e) {
            return "Arity must be a valid integer.";
        }
        try {
            int priority = Integer.parseInt(priorityField.getText().trim());
            if (priority < 0) {
                return "Priority must be >= 0.";
            }
        } catch (NumberFormatException e) {
            return "Priority must be a valid integer.";
        }
        String format = outputFormatField.getText().trim();
        if (format.isEmpty()) {
            return "Output format is required.";
        }
        int underscoreCount = 0;
        for (char c : format.toCharArray()) {
            if (c == '_') underscoreCount++;
        }
        int arity = Integer.parseInt(arityField.getText().trim());
        if (underscoreCount != arity) {
            return "Output format must contain exactly " + arity + " underscore(s) matching the arity.";
        }
        return null;
    }

    public String getConnectorName() {
        return nameField.getText().trim();
    }

    public int getArity() {
        return Integer.parseInt(arityField.getText().trim());
    }

    public String getOutputFormat() {
        return outputFormatField.getText().trim();
    }

    public int getPriority() {
        return Integer.parseInt(priorityField.getText().trim());
    }
}
