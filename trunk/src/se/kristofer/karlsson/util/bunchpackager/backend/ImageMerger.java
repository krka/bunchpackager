package se.kristofer.karlsson.util.bunchpackager.backend;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import se.kristofer.karlsson.util.bunchpackager.backend.bunch.Bunch;

public class ImageMerger {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Syntax: java ImageMerger <image directory> <output image>");
            System.exit(1);
        }
        
        File inputDir = new File(args[0]);
        File outputImage = new File(args[1]);
        
        File[] children = inputDir.listFiles();
        Arrays.sort(children, new FileNameComparator());
        
        int width = 0;
        int height = 0;
        ArrayList<Image> images = new ArrayList<Image>();
        for (int i = 0; i < children.length; i++) {
            File child = children[i];
            Image im = Bunch.getImage(child);
            int w = im.getWidth(null);
            int h = im.getHeight(null);
            if (w != -1) {
                System.out.println(child.getAbsolutePath());
                if (width == 0) {
                    width = w;
                    height = h;
                } else {
                    if (width != w || height != h) {
                        System.err.println("All images were not of the same size!");
                        System.exit(1);
                    }
                }
                images.add(im);
            }
        }
        
        BufferedImage outputImageImage = new BufferedImage(width * images.size(), height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = outputImageImage.getGraphics();
        for (int i = 0; i < images.size(); i++) {
            Image im = (Image) images.get(i);
            g.drawImage(im, i * width, 0, null);
        }
        try {
            ImageIO.write(outputImageImage, "png", outputImage);
        } catch (IOException e) {
            System.err.println("Error while saving output image!");
        }
    }
}
