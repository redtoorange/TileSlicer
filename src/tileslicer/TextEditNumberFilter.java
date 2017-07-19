/*
 * Copyright 2017 Andrew James McGuiness
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 *  to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tileslicer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * TextEditNumberFilter.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class TextEditNumberFilter implements ChangeListener<String> {
    private TextField controlled;

    public TextEditNumberFilter( TextField controlled){
        this.controlled = controlled;
    }

    @Override
    public void changed( ObservableValue< ? extends String > observable, String oldValue, String newValue ) {
        if (!newValue.matches("\\d*")) {
            controlled.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
}
