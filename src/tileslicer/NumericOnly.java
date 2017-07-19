package tileslicer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * NumericOnly.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class NumericOnly implements ChangeListener<String> {
    private TextField controlled;

    public NumericOnly( TextField controlled){
        this.controlled = controlled;
    }

    @Override
    public void changed( ObservableValue< ? extends String > observable, String oldValue, String newValue ) {
        if (!newValue.matches("\\d*")) {
            controlled.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
}
