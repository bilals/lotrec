package lotrec.guifx.dialogs;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileDialogs {

    private final Stage owner;
    private final FileChooser.ExtensionFilter xmlFilter;
    private final FileChooser.ExtensionFilter imageFilter;
    private final FileChooser.ExtensionFilter allFilter;

    public FileDialogs(Stage owner) {
        this.owner = owner;
        xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
        imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
        allFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
    }

    public File showOpenLogicDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Logic File");
        chooser.getExtensionFilters().addAll(xmlFilter, allFilter);
        return chooser.showOpenDialog(owner);
    }

    public File showSaveLogicDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Logic File");
        chooser.getExtensionFilters().addAll(xmlFilter, allFilter);
        return chooser.showSaveDialog(owner);
    }

    public File showExportDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Image", "*.png"),
            new FileChooser.ExtensionFilter("PDF Document", "*.pdf"),
            new FileChooser.ExtensionFilter("PostScript", "*.ps"),
            allFilter
        );
        return chooser.showSaveDialog(owner);
    }

    public FileChooser.ExtensionFilter getXmlFilter() { return xmlFilter; }
    public FileChooser.ExtensionFilter getImageFilter() { return imageFilter; }
}
