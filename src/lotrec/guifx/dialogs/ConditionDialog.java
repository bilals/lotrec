package lotrec.guifx.dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.guifx.DialogsFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ConditionDialog extends Dialog<ConditionDialog.ConditionResult> {

    private final ComboBox<String> conditionTypeCombo;
    private final List<TextField> paramFields = new ArrayList<>();
    private final List<Label> paramLabels = new ArrayList<>();
    private final List<Button> infoButtons = new ArrayList<>();
    private final VBox paramsBox;

    public ConditionDialog(Stage owner) {
        this(owner, null);
    }

    public ConditionDialog(Stage owner, ConditionResult existing) {
        setTitle(existing == null ? "Add Condition" : "Edit Condition");
        initOwner(owner);
        setResizable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Condition type selector from CLASSES_KEYWORDS
        TreeSet<String> sortedKeywords = new TreeSet<>(AbstractCondition.CLASSES_KEYWORDS.keySet());
        conditionTypeCombo = new ComboBox<>(FXCollections.observableArrayList(sortedKeywords));
        conditionTypeCombo.setPromptText("Select condition type");

        grid.add(new Label("Condition Type:"), 0, 0);
        grid.add(conditionTypeCombo, 1, 0);

        // Dynamic parameter fields (up to 4)
        paramsBox = new VBox(5);
        paramsBox.setPadding(new Insets(10, 0, 0, 0));
        for (int i = 0; i < 4; i++) {
            Label lbl = new Label("Param " + (i + 1) + ":");
            lbl.setVisible(false);
            lbl.setManaged(false);
            TextField tf = new TextField();
            tf.setVisible(false);
            tf.setManaged(false);
            HBox.setHgrow(tf, Priority.ALWAYS);
            Button infoBtn = createInfoButton("");
            infoBtn.setVisible(false);
            infoBtn.setManaged(false);
            paramLabels.add(lbl);
            paramFields.add(tf);
            infoButtons.add(infoBtn);
            HBox row = new HBox(5, lbl, tf, infoBtn);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            paramsBox.getChildren().add(row);
        }
        grid.add(paramsBox, 0, 1, 2, 1);

        // When condition type changes, discover parameters via reflection
        conditionTypeCombo.setOnAction(e -> updateParameterFields());

        // Pre-populate if editing existing condition
        if (existing != null) {
            conditionTypeCombo.setValue(existing.getKeyword());
            updateParameterFields();
            List<String> params = existing.getParameters();
            for (int i = 0; i < params.size() && i < paramFields.size(); i++) {
                paramFields.get(i).setText(params.get(i));
            }
        }

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK && conditionTypeCombo.getValue() != null) {
                String keyword = conditionTypeCombo.getValue();
                List<String> params = new ArrayList<>();
                for (TextField tf : paramFields) {
                    if (tf.isVisible() && !tf.getText().trim().isEmpty()) {
                        params.add(tf.getText().trim());
                    }
                }
                return new ConditionResult(keyword, params);
            }
            return null;
        });
    }

    private Button createInfoButton(String description) {
        Button btn = new Button();
        btn.setMinSize(24, 24);
        btn.setMaxSize(24, 24);
        try {
            InputStream is = getClass().getResourceAsStream("/lotrec/images/info.png");
            if (is != null) {
                ImageView iv = new ImageView(new Image(is, 16, 16, true, true));
                btn.setGraphic(iv);
            } else {
                btn.setText("?");
            }
        } catch (Exception e) {
            btn.setText("?");
        }
        btn.setTooltip(new Tooltip(description));
        btn.setOnAction(e -> {
            String text = btn.getTooltip() != null ? btn.getTooltip().getText() : "";
            if (!text.isEmpty()) {
                Stage stage = (Stage) getDialogPane().getScene().getWindow();
                DialogsFactory.infoDialog(stage, "Parameter Info", text);
            }
        });
        return btn;
    }

    private void updateParameterFields() {
        // Hide all first
        for (int i = 0; i < 4; i++) {
            paramLabels.get(i).setVisible(false);
            paramLabels.get(i).setManaged(false);
            paramFields.get(i).setVisible(false);
            paramFields.get(i).setManaged(false);
            paramFields.get(i).clear();
            infoButtons.get(i).setVisible(false);
            infoButtons.get(i).setManaged(false);
        }

        String keyword = conditionTypeCombo.getValue();
        if (keyword == null) return;

        String className = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        if (className == null) return;

        try {
            Class<?> clazz = Class.forName(AbstractCondition.CONDITIONS_PACKAGE + className);
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length > 0) {
                Constructor<?> ctor = constructors[0];
                Class<?>[] paramTypes = ctor.getParameterTypes();
                // Try to get annotations for descriptions
                java.lang.annotation.Annotation[] annotations = ctor.getAnnotations();
                String[] typeNames = null;
                String[] descriptions = null;
                for (java.lang.annotation.Annotation ann : annotations) {
                    if (ann.annotationType().getSimpleName().equals("ParametersTypes")) {
                        try {
                            typeNames = (String[]) ann.annotationType().getMethod("types").invoke(ann);
                        } catch (Exception ignored) {}
                    }
                    if (ann.annotationType().getSimpleName().equals("ParametersDescriptions")) {
                        try {
                            descriptions = (String[]) ann.annotationType().getMethod("descriptions").invoke(ann);
                        } catch (Exception ignored) {}
                    }
                }

                for (int i = 0; i < paramTypes.length && i < 4; i++) {
                    String label = "Param " + (i + 1);
                    if (typeNames != null && i < typeNames.length) {
                        label = typeNames[i];
                    }
                    String desc = "";
                    if (descriptions != null && i < descriptions.length) {
                        desc = descriptions[i];
                        paramFields.get(i).setPromptText(desc);
                    }
                    paramLabels.get(i).setText(label + ":");
                    paramLabels.get(i).setVisible(true);
                    paramLabels.get(i).setManaged(true);
                    paramFields.get(i).setVisible(true);
                    paramFields.get(i).setManaged(true);
                    if (!desc.isEmpty()) {
                        infoButtons.get(i).setTooltip(new Tooltip(desc));
                        infoButtons.get(i).setVisible(true);
                        infoButtons.get(i).setManaged(true);
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {
            // Class not found - no parameters to show
        }

        if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() != null) {
            getDialogPane().getScene().getWindow().sizeToScene();
        }
    }

    public ComboBox<String> getConditionTypeCombo() {
        return conditionTypeCombo;
    }

    public List<TextField> getParamFields() {
        return paramFields;
    }

    public static class ConditionResult {
        private final String keyword;
        private final List<String> parameters;

        public ConditionResult(String keyword, List<String> parameters) {
            this.keyword = keyword;
            this.parameters = parameters;
        }

        public String getKeyword() { return keyword; }
        public List<String> getParameters() { return parameters; }
    }
}
