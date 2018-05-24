package crdhn.sis.imagescaler;

import crdhn.sis.configuration.Configuration;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author namdv
 */
public class ImageWorker extends Thread {

    private static final String _className = "ImageWorker";
    private final int _workerNumber;

    public ImageWorker(int workerNumber) {
        _workerNumber = workerNumber;
    }

    @Override
    public void run() {
        System.out.println("ImageWorker " + _workerNumber + " started!");
        String pathScaled = Configuration.HOME_PATH + File.separator + Configuration.SCALED_IMAGE_DIRECTORY;
        ImageInfo image;
        while (true) {
            image = ImageQueue.get();
            if (image == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ImageWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }
            System.out.println("ImageWorker process path=" + image.name + "\t workerNumber=" + _workerNumber);
            processImage(image, pathScaled);
            image = null;
        }
    }

    private void processImage(ImageInfo image, String pathScaled) {
        try {
            BufferedImage imageSrc = getDataImage(image.path);
            if (imageSrc == null) {
                System.out.println("Image nullllllllllllllllllll name=" + image.name + "\t size=" + image.width + "x" + image.height);
                return;
            }
            List<String> imageSizes = Configuration.images_size;
            BufferedImage resizedImage;
            for (int i = 1; i < imageSizes.size(); i++) {
                String[] size = imageSizes.get(i).split("x");
                int width = Integer.valueOf(size[0]);
                int height = Integer.valueOf(size[1]);
                resizedImage = Scalr.resize(imageSrc, Scalr.Mode.BEST_FIT_BOTH, width, height);
                ImageIO.write(resizedImage, image.type, new File(pathScaled + File.separator + image.name + "_" + width + "_" + height + "." + image.type));
                resizedImage.flush();
            }
            resizedImage = null;
            imageSrc.flush();
            imageSrc = null;
        } catch (IOException | IllegalArgumentException | ImagingOpException ex) {
            Logger.getLogger(ImageWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized BufferedImage getDataImage(String pathFile) {
        try {
            File file = new File(pathFile);
            BufferedImage srcImage = ImageIO.read(file);
            return srcImage;
        } catch (IOException ex) {
            Logger.getLogger(ImageWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
