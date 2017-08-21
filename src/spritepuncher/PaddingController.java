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

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * PaddingController.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/21/2017
 */
public class PaddingController {
    private TextField tileHeightText, tileWidthText, outsideMarginText, insideMarginText, paddingAmount;
    private File selectedFile;
    private int tileHeight, tileWidth, outsideMargin, insideMargin, padAmount;

    public PaddingController( TextField tileWidthText, TextField tileHeightText, TextField outsideMarginText, TextField insideMarginText, TextField paddingAmount ) {
        this.tileWidthText = tileWidthText;
        this.tileHeightText = tileHeightText;
        this.outsideMarginText = outsideMarginText;
        this.insideMarginText = insideMarginText;

        this.paddingAmount = paddingAmount;

        this.tileHeightText.textProperty().addListener( new TextEditNumberFilter( tileHeightText ) );
        this.tileWidthText.textProperty().addListener( new TextEditNumberFilter( tileWidthText ) );
        this.outsideMarginText.textProperty().addListener( new TextEditNumberFilter( outsideMarginText ) );
        this.insideMarginText.textProperty().addListener( new TextEditNumberFilter( insideMarginText ) );
        this.paddingAmount.textProperty().addListener( new TextEditNumberFilter( paddingAmount ) );
    }

    public void loadImage( File selectedFile ){
        this.selectedFile = selectedFile;
        try {
            BufferedImage img = ImageIO.read( selectedFile );

            tileHeight = Integer.parseInt( tileHeightText.getText() );
            tileWidth = Integer.parseInt( tileWidthText.getText() );
            outsideMargin = Integer.parseInt( outsideMarginText.getText() );
            insideMargin = Integer.parseInt( insideMarginText.getText() );
            padAmount = Integer.parseInt( paddingAmount.getText() );

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

        BufferedImage compositeImage = new BufferedImage( tileXCount * (tileWidth + padAmount*2), tileYCount * (tileHeight + padAmount*2), image.getType());

        for( int x = 0; x < tileXCount; x++){
            for( int y = 0; y < tileYCount; y++){
                int startingX = outsideMargin + (x * (tileWidth + insideMargin) );
                int startingY = outsideMargin + (y * (tileHeight + insideMargin) );

                BufferedImage processedTile = padImage( image.getSubimage( startingX, startingY, tileWidth, tileHeight ) );
                compositeImage.createGraphics().drawImage( processedTile, null, x*(tileWidth+(padAmount*2)), y*(tileHeight+(padAmount+2)) );
            }
        }



        String folder = selectedFile.getAbsolutePath().replaceAll( selectedFile.getName(), "" );
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle( "Where to save" );
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter( "Image File", "*.png", "*.jpg", "*.JPG" )
            );
            chooser.setInitialDirectory( selectedFile.getParentFile() );

            File outputFile = chooser.showSaveDialog( null );

            if( outputFile != null ) {
                int i = outputFile.getName().lastIndexOf( '.' );
                String extension = outputFile.getName().substring( i+1 );

                if( !extension.isEmpty()) {
                    ImageIO.write( compositeImage, extension, outputFile );
                    Toolkit.getDefaultToolkit().beep();
                    try {
                        Runtime.getRuntime().exec( "explorer " + folder );
                    }
                    catch(Exception e){

                    }
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
    }

    private BufferedImage padImage( BufferedImage original ){
        BufferedImage resizedImage = new BufferedImage( tileWidth + (padAmount*2), tileHeight + (padAmount*2), original.getType() );


        Graphics2D g = resizedImage.createGraphics();
        g.drawImage( original, null, padAmount, padAmount );

        //              edge a = width of image, height of padding
        //                       pos = 0,0 -> padding, 0
        //                 A
        //          +---------------+
        //          |               |
        //       C  |               | D
        //          |               |
        //          |               |
        //          +---------------+
        //                  B
        //
        //              edge b = width of image, height of padding
        //                       pos = 0,0 -> padding, 0
        //

        //fill Width
        for( int y = (tileHeight+padAmount); y > 0; y--){
            int value = resizedImage.getRGB( padAmount, y );
            for( int x = padAmount-1; x >= 0; x--) {
                resizedImage.setRGB( x, y, value );
            }

            value = resizedImage.getRGB( (tileWidth + padAmount - 1), y );
            for( int x = tileWidth + padAmount; x < resizedImage.getWidth(); x++) {
                resizedImage.setRGB( x, y, value );
            }
        }

        for( int x = resizedImage.getWidth()-1; x >= 0; x--){
            int value = resizedImage.getRGB( x, padAmount );
            for( int y = padAmount-1; y >= 0; y--) {
                resizedImage.setRGB( x, y, value );
            }

            value = resizedImage.getRGB( x, (tileHeight + padAmount - 1) );
            for( int y = tileHeight + padAmount; y < resizedImage.getHeight(); y++) {
                resizedImage.setRGB( x, y, value );
            }
        }

        return resizedImage;
    }
}
