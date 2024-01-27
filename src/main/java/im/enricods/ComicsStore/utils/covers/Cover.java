package im.enricods.ComicsStore.utils.covers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public class Cover {

    //@Value("${images-path}")
    private static final String IMAGES_DIRECTORY = "/home/eds/Documenti/Comics-Store-BE/src/main/resources/images/";

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

    public static byte[] get(String fileName) throws IOException {
        if(new File(IMAGES_DIRECTORY+fileName+".webp").exists()){
            Resource imgResource = new ClassPathResource("images/"+fileName+".webp");
            return StreamUtils.copyToByteArray(imgResource.getInputStream());
        }
        else{
            Resource imgResource = new ClassPathResource("images/"+"default.webp");
            return StreamUtils.copyToByteArray(imgResource.getInputStream());
        }
    }//get
    
}//Images
