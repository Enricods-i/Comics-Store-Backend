package im.enricods.ComicsStore.utils.Covers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public class Cover {

    @Value("$(image-path)")
    private static String IMAGES_DIRECTORY;

    @Async
    public static void save(String fileName, MultipartFile file) throws IOException{
        BufferedImage image = ImageIO.read(file.getInputStream());
        if(image.getWidth() != 435 || image.getHeight() != 682)
            throw new IllegalArgumentException("Image not valid! It must be 435x682");

        //save image to the server
        ImageIO.write(image, "webp", new File(IMAGES_DIRECTORY + fileName + ".webp"));
    }//save

    @Async
    public static void remove(String fileName){
        new File(IMAGES_DIRECTORY + fileName + ".webp").delete();
    }//remove

    public static java.awt.Image get(String fileName) throws IOException{
        try {
            return ImageIO.read(new File(IMAGES_DIRECTORY+fileName+".webp"));
        } catch (IOException e) {
            return ImageIO.read(new File("default.webp"));
        }
    }//get
    
}//Images
