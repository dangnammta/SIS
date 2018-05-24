/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.imagescaler;

import crdhn.sis.configuration.ConfigHelper;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author namdv
 */
public class ImageQueue {

    private static final int max_queue_size = ConfigHelper.getParamInt("service", "site_queue_size");
    public static ArrayBlockingQueue<ImageInfo> _fileQueue = new ArrayBlockingQueue(max_queue_size);

    public static void put(ImageInfo image) {
        try {
            if (_fileQueue.size() <= max_queue_size - 10) {
                _fileQueue.put(image);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static ImageInfo get() {
        return _fileQueue.poll();
    }

}
