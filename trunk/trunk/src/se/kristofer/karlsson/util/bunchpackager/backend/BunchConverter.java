/*
    Copyright (c) 2005, 2008 Kristofer Karlsson

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package se.kristofer.karlsson.util.bunchpackager.backend;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import se.kristofer.karlsson.util.bunchpackager.backend.bunch.Bunch;

public class BunchConverter {

    private File imageFile;
    private File textFile;
    private File outputDir;

    public void setImage(File f) {
        this.imageFile = f;
    }
    
    public void setText(File f) {
        this.textFile = f;
    }
    
    public void setOutputdir(File f) {
        this.outputDir = f;
    }
    
    public void execute() {

        outputDir.mkdirs();
        	
        String baseOutput = outputDir.getAbsolutePath() + File.separator; 
            
        Image image = Bunch.getImage(imageFile);
        
        try {
            Scanner scanner = new Scanner(textFile);
            
            while (scanner.hasNext()) {
                String partName = scanner.next();
                int col = scanner.nextInt(); 
                int row = scanner.nextInt(); 
                int width = scanner.nextInt(); 
                int height = scanner.nextInt();


                BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics g = outputImage.getGraphics();

                g.drawImage(image, -col, -row, null);
                try {
                    ImageIO.write(outputImage, "png", new File(baseOutput + partName + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (FileNotFoundException e1) {
            System.err.println("Text file not found!");
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Syntax: java BunchConverter <image file> <text file> <output directory>");
            System.exit(1);
        }
        BunchConverter bc = new BunchConverter();
        bc.setImage(new File(args[0]));
        bc.setText(new File(args[1]));
        bc.setOutputdir(new File(args[2]));
        
        bc.execute();
    }
}
