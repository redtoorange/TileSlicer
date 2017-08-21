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

package spritepuncher;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;

/**
 * ViewController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class ViewController {
    @FXML
    ImageView imagePreview;
    @FXML
    Button cutButton, fileChooserButton;
    @FXML
    TextField tileHeightText, tileWidthText, outsideMarginText, insideMarginText, fileChooserText, tilePrefixText;


    @FXML
    Button addPaddingButton, padFileChooser;
    @FXML
    TextField padTileHeight, padTileWidth, padOutsideMargin, padInsideMargin, padFileChooserText, paddingAmount;
    @FXML
    ImageView padImageView;

    private FileChooser chooser;
    private File selectedFile;

    private SlicerController slicerController;
    private PaddingController paddingController;

    @FXML public void initialize(){
        initFileChooser();

        initSlicerController();
        initPaddingController();
    }

    private void initSlicerController() {
        slicerController = new SlicerController( tileWidthText, tileHeightText, outsideMarginText, insideMarginText, tilePrefixText );

        fileChooserButton.addEventHandler( MouseEvent.MOUSE_PRESSED, new FileLoadHandler());
        imagePreview.addEventHandler( MouseEvent.MOUSE_PRESSED, new FileLoadHandler());

        cutButton.addEventHandler( MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>( ){
            @Override
            public void handle( MouseEvent event ) {
                if( selectedFile != null && selectedFile.exists()){
                    slicerController.loadImage( selectedFile );
                }
                else{
                    new Alert( Alert.AlertType.ERROR, "Please select a file." ).show();
                }
            }
        });
    }

    private void initPaddingController() {
        paddingController = new PaddingController( padTileWidth, padTileHeight, padOutsideMargin, padInsideMargin, paddingAmount );

        padFileChooser.addEventHandler( MouseEvent.MOUSE_PRESSED, new FileLoadHandler());
        padImageView.addEventHandler( MouseEvent.MOUSE_PRESSED, new FileLoadHandler());

        addPaddingButton.addEventHandler( MouseEvent.MOUSE_PRESSED, new EventHandler< MouseEvent >() {
            @Override
            public void handle( MouseEvent event ) {
                if ( selectedFile != null && selectedFile.exists() ) {
                    paddingController.loadImage( selectedFile );
                } else {
                    new Alert( Alert.AlertType.ERROR, "Please select a file." ).show();
                }
            }
        } );
    }

    private void initFileChooser() {
        chooser = new FileChooser();
        chooser.setTitle( "Select Spritesheet" );
        chooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter( "Image File", "*.png", "*.jpg", "*.JPG" )
        );
    }


    class FileLoadHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle( MouseEvent event ) {
            if( selectedFile != null ){
                chooser.setInitialDirectory( selectedFile.getParentFile() );
            }

            File f = chooser.showOpenDialog( null );
            if( f != null && f.exists()){
                fileChooserText.setText( f.getName() );
                padFileChooserText.setText( f.getName() );

                selectedFile = f;
                try {
                    imagePreview.setImage( new Image( new FileInputStream( selectedFile ) ) );
                    padImageView.setImage( new Image( new FileInputStream( selectedFile ) ) );
                }
                catch(Exception e){

                }
            }
        }
    }
}
