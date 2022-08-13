package im.enricods.ComicsStore.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public class Images {

    @Value("$(image-path)")
    private static String IMAGES_DIRECTORY;

    @Async
    public static void saveImage(String fileName, MultipartFile file) throws IOException{
        BufferedImage image = ImageIO.read(file.getInputStream());
        if(image.getWidth() != 435 || image.getHeight() != 682)
            throw new IllegalArgumentException("Image not valid! It must be 435x682");

        //save image to the server
        ImageIO.write(image, "webp", new File(IMAGES_DIRECTORY + fileName + ".webp"));
    }//saveImage

    public static java.awt.Image getImage(String fileName) throws IOException{
        return ImageIO.read(new File(IMAGES_DIRECTORY+fileName+".webp"));
    }//getImage
    
}//Image
