/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxml;

import com.mycompany.searchengine.GeneralController;
import com.mycompany.searchengine.Switcher;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import utils.Fxml;
import vue.ListResult.ListResult;

/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class ResultVisualisation implements Initializable {

    /**
     * Initializes the controller class.
     */
    private CodeArea codeArea;
    private Pattern pattern;
    @FXML
    private StackPane resultContainer;
    @FXML
    private StackPane root;
    private GeneralController generalController=GeneralController.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        codeArea = new CodeArea();
        resultContainer.getChildren().clear();
        resultContainer.getChildren().add(codeArea);
        init();
    }

    public void init() {

        String query=generalController.getCurrentQuery();
        String text = generalController.getCurrentResult().getText();
        String[] queryTerms = query.toLowerCase().split(" ");
        String KEYWORD_PATTERN = "\\b("
                + String.join("|", queryTerms) + ")\\b";
        pattern = Pattern.compile("(?<KEYWORD>"
                + KEYWORD_PATTERN + ")");

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
        codeArea.replaceText(0, 0, text);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = pattern.matcher(text.toLowerCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" : null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start()
                    - lastKwEnd);

            spansBuilder.add(Collections.singleton(styleClass), matcher.end()
                    - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void goBack(ActionEvent event) {
        ListResult listResult = (ListResult) Switcher.newInstance().swtichScene(root,Fxml.LIST_RESULT);
    }

}
