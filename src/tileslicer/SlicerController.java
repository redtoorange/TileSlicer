package tileslicer;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * SlicerController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class SlicerController {
    @FXML
    Button cutButton, fileChooserButton;
    @FXML
    TextField tileHeightText, tileWidthText, outsideMarginText, insideMarginText, fileChooserText, tilePrefixText;

    private FileChooser chooser;
    private File selectedFile;

    private int tileHeight, tileWidth, outsideMargin, insideMargin;

    @FXML public void initialize(){
        chooser = new FileChooser();
        chooser.setTitle( "Select Spritesheet" );
        chooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter( "Image File", "*.png", "*.jpg", "*.JPG" )
        );

        fileChooserText.setDisable( true );

        tileHeightText.textProperty().addListener( new NumericOnly( tileHeightText ) );
        tileWidthText.textProperty().addListener( new NumericOnly( tileWidthText ) );
        outsideMarginText.textProperty().addListener( new NumericOnly( outsideMarginText ) );
        insideMarginText.textProperty().addListener( new NumericOnly( insideMarginText ) );

        fileChooserButton.addEventHandler( MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>( ){
            @Override
            public void handle( MouseEvent event ) {
                File f = chooser.showOpenDialog( null );
                if( f != null && f.exists()){
                    fileChooserText.setText( f.getName() );
                    selectedFile = f;
                }
            }
        });

        cutButton.addEventHandler( MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>( ){
            @Override
            public void handle( MouseEvent event ) {
                if( selectedFile != null && selectedFile.exists()){
                    loadImage( );
                }
                else{
                    new Alert( Alert.AlertType.ERROR, "Please select a file." ).show();
                }
            }
        });
    }



    private void loadImage( ){
        try {
            BufferedImage img = ImageIO.read( selectedFile );

            tileHeight = Integer.parseInt( tileHeightText.getText() );
            tileWidth = Integer.parseInt( tileWidthText.getText() );
            outsideMargin = Integer.parseInt( outsideMarginText.getText() );
            insideMargin = Integer.parseInt( insideMarginText.getText() );

            processImage( img );
        }
        catch (IOException e) {
            new Alert( Alert.AlertType.ERROR, "Failed to load image." ).show();
        }
    }

    private void processImage( BufferedImage image ){
        int width = image.getWidth();
        int height = image.getHeight();

        int insideWidth = width - (outsideMargin*2);
        int tileXCount = (insideWidth + insideMargin) / (tileWidth + insideMargin);

        int insideHeight = height - (outsideMargin*2);
        int tileYCount = (insideHeight + insideMargin) / (tileHeight + insideMargin);

        BufferedImage[][] images = new BufferedImage[tileXCount][tileYCount];

        for( int x = 0; x < tileXCount; x++){
            for( int y = 0; y < tileYCount; y++){
                int startingX = outsideMargin + (x * (tileWidth + insideMargin) );
                int startingY = outsideMargin + (y * (tileHeight + insideMargin) );

                images[x][y] = image.getSubimage( startingX, startingY, tileWidth, tileHeight );
            }
        }

        String folder = selectedFile.getAbsolutePath().replaceAll( selectedFile.getName(), "" );
        try {

            for( int x = 0; x < tileXCount; x++){
                for( int y = 0; y < tileYCount; y++){
                    File outputFile = new File(folder + tilePrefixText.getText() + x + "_" + y + ".png");
                    ImageIO.write(images[x][y], "png", outputFile);
                }
            }
        } catch (IOException e) {
            try{
                BufferedWriter bos = new BufferedWriter( new FileWriter(folder + "error_log.txt") );
                bos.write( e.getMessage() );
                bos.close();
            }
            catch( Exception e2){
                e2.printStackTrace();
            }
        }

        Toolkit.getDefaultToolkit().beep();
        new Alert( Alert.AlertType.CONFIRMATION, "Operation Complete!" ).show();
    }
}
