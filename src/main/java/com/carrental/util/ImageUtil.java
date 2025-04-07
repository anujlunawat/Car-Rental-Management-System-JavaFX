package com.carrental.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtil {
    // Standard dimensions for car images
    public static final int CAR_IMAGE_WIDTH = 800;
    public static final int CAR_IMAGE_HEIGHT = 600;
    
    /**
     * Resizes and crops an image to the standard car image dimensions while maintaining aspect ratio
     * @param sourceImage The source image to process
     * @return A new image with the standard dimensions
     */
    public static Image processCarImage(Image sourceImage) {
        double sourceWidth = sourceImage.getWidth();
        double sourceHeight = sourceImage.getHeight();
        
        // Calculate scaling factors
        double widthScale = CAR_IMAGE_WIDTH / sourceWidth;
        double heightScale = CAR_IMAGE_HEIGHT / sourceHeight;
        
        // Use the larger scale to ensure the image covers the target area
        double scale = Math.max(widthScale, heightScale);
        
        // Calculate scaled dimensions
        double scaledWidth = sourceWidth * scale;
        double scaledHeight = sourceHeight * scale;
        
        // Create a new image with the target dimensions
        WritableImage targetImage = new WritableImage(CAR_IMAGE_WIDTH, CAR_IMAGE_HEIGHT);
        PixelWriter writer = targetImage.getPixelWriter();
        
        // Fill background with white
        for (int x = 0; x < CAR_IMAGE_WIDTH; x++) {
            for (int y = 0; y < CAR_IMAGE_HEIGHT; y++) {
                writer.setColor(x, y, Color.WHITE);
            }
        }
        
        // Calculate center position
        int startX = (int) ((CAR_IMAGE_WIDTH - scaledWidth) / 2);
        int startY = (int) ((CAR_IMAGE_HEIGHT - scaledHeight) / 2);
        
        // Copy and scale the image
        for (int x = 0; x < CAR_IMAGE_WIDTH; x++) {
            for (int y = 0; y < CAR_IMAGE_HEIGHT; y++) {
                int sourceX = (int) ((x - startX) / scale);
                int sourceY = (int) ((y - startY) / scale);
                
                if (sourceX >= 0 && sourceX < sourceWidth && sourceY >= 0 && sourceY < sourceHeight) {
                    Color color = sourceImage.getPixelReader().getColor(sourceX, sourceY);
                    writer.setColor(x, y, color);
                }
            }
        }
        
        return targetImage;
    }
    
    /**
     * Processes a car image file and saves it with the standard dimensions
     * @param sourceFile The source image file
     * @param targetFile The target file to save the processed image
     * @throws IOException If there's an error processing the image
     */
    public static void processCarImageFile(File sourceFile, File targetFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            Image sourceImage = new Image(fis);
            Image processedImage = processCarImage(sourceImage);
            
            // Convert JavaFX Image to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(processedImage, null);
            
            // Save the image using ImageIO
            String format = getFileExtension(targetFile.getName());
            ImageIO.write(bufferedImage, format, targetFile);
        }
    }
    
    /**
     * Gets the file extension without the dot
     * @param fileName The file name
     * @return The extension (e.g., "jpg", "png")
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "png"; // Default to PNG if no extension found
    }
    
    /**
     * Creates an ImageView with the standard car image dimensions
     * @param image The image to display
     * @return An ImageView configured for car display
     */
    public static ImageView createCarImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(CAR_IMAGE_WIDTH);
        imageView.setFitHeight(CAR_IMAGE_HEIGHT);
        imageView.setPreserveRatio(false);
        return imageView;
    }
} 