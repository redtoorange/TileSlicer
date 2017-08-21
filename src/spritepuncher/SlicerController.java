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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * SlicerController.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/21/2017
 */
public class SlicerController {
    private TextField tileHeightText, tileWidthText, outsideMarginText, insideMarginText, tilePrefixText;
    private File selectedFile;
    private int tileHeight, tileWidth, outsideMargin, insideMargin;

    public SlicerController( TextField tileWidthText, TextField tileHeightText, TextField outsideMarginText, TextField insideMarginText, TextField tilePrefixText ) {
        this.tileWidthText = tileWidthText;
        this.tileHeightText = tileHeightText;
        this.outsideMarginText = outsideMarginText;
        this.insideMarginText = insideMarginText;

        this.tilePrefixText = tilePrefixText;

        this.tileHeightText.textProperty().addListener( new TextEditNumberFilter( tileHeightText ) );
        this.tileWidthText.textProperty().addListener( new TextEditNumberFilter( tileWidthText ) );
        this.outsideMarginText.textProperty().addListener( new TextEditNumberFilter( outsideMarginText ) );
        this.insideMarginText.textProperty().addListener( new TextEditNumberFilter( insideMarginText ) );
    }

    public void loadImage( File selectedFile ){
        this.selectedFile = selectedFile;
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


        try {
            Runtime.getRuntime().exec( "explorer " + folder );
        }
        catch(Exception e){

        }
    }
}
