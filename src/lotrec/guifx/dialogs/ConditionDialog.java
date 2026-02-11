package lotrec.guifx.dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.dataStructure.tableau.condition.AbstractCondition;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ConditionDialog extends Dialog<ConditionDialog.ConditionResult> {

    private final ComboBox<String> conditionTypeCombo;
    private final List<TextField> paramFields = new ArrayList<>();
    private final List<Label> paramLabels = new ArrayList<>();
    private final VBox paramsBox;

    public ConditionDialog(Stage owner) {
        setTitle("Add Condition");
        initOwner(owner);

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
            paramLabels.add(lbl);
            paramFields.add(tf);
            paramsBox.getChildren().addAll(lbl, tf);
        }
        grid.add(paramsBox, 0, 1, 2, 1);

        // When condition type changes, discover parameters via reflection
        conditionTypeCombo.setOnAction(e -> updateParameterFields());

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

    private void updateParameterFields() {
        // Hide all first
        for (int i = 0; i < 4; i++) {
            paramLabels.get(i).setVisible(false);
            paramLabels.get(i).setManaged(false);
            paramFields.get(i).setVisible(false);
            paramFields.get(i).setManaged(false);
            paramFields.get(i).clear();
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
                    if (descriptions != null && i < descriptions.length) {
                        paramFields.get(i).setPromptText(descriptions[i]);
                    }
                    paramLabels.get(i).setText(label + ":");
                    paramLabels.get(i).setVisible(true);
                    paramLabels.get(i).setManaged(true);
                    paramFields.get(i).setVisible(true);
                    paramFields.get(i).setManaged(true);
                }
            }
        } catch (ClassNotFoundException ignored) {
            // Class not found - no parameters to show
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
