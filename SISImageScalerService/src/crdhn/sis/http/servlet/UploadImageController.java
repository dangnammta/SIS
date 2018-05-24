/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.http.servlet;

import crdhn.sis.configuration.Configuration;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import crdhn.sis.http.response.DataResponse;
import crdhn.sis.imagescaler.ImageInfo;
import crdhn.sis.imagescaler.ImageQueue;
import crdhn.sis.imagescaler.Scalr;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;

/**
 *
 * @author longmd
 */
public class UploadImageController extends BaseServlet {

    private static Logger logger = Logger.getLogger(UploadImageController.class.getName());

//        @Override
//        protected void doPost(HttpServletRequest request, HttpServletResponse resp) {
//            DataResponse dataResponse = null;
//            
//            String method = ServletUtil.getStringParameter(request, "method");
//            log.info("method: " + method);
//
////            if ("patient.add_check_up".equals(method)) {
////                dataResponse = patientAddCheckUp(request);
////            } else {
////                dataResponse = DataResponse.METHOD_NOT_FOUND;
////            }
//			dataResponse = DataResponse.METHOD_NOT_FOUND;
//            dataResponse.setEscape(true);
//            responseJson(dataResponse.toString(), resp);
//        }
    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        DataResponse dataResponse;
        String method = ServletUtil.getStringParameter(request, "method");
        if (method == null || method.isEmpty()) {
            return;
        }
        logger.info("method: " + method);
        method = method.replace(".", "_").toLowerCase();

        switch (method) {
            case "uploadimage":
                dataResponse = uploadImage(request);
                break;

            default:
                dataResponse = DataResponse.METHOD_NOT_FOUND;
        }

        dataResponse.setEscape(true);
        responseJson(dataResponse.toString(), resp);
    }

    private String getRandomName() {
        String randomStr = "SIS_Image" + Long.toString(System.nanoTime());
        return toHex(randomStr.getBytes());
    }

    public DataResponse uploadImage(HttpServletRequest request) {

        try {
            Part filePart = request.getPart("file");
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()
                    || (!fileName.toLowerCase().endsWith(".jpg")
                    && !fileName.toLowerCase().endsWith(".png")
                    && !fileName.toLowerCase().endsWith(".jpeg"))) {
                return DataResponse.PARAM_ERROR;
            }
            String fileType = "";
            if (fileName.toLowerCase().endsWith(".jpg")) {
                fileType = "jpg";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                fileType = "png";
            } else if (fileName.toLowerCase().endsWith(".jpeg")) {
                fileType = "jpeg";
            }
            String newFileName = getRandomName();
//            String filePath = Configuration.HOME_PATH + File.separator + Configuration.ORIGINAL_IMAGE_DIRECTORY + File.separator + newFileName + "." + fileType;
            String pathScaled = Configuration.HOME_PATH + File.separator + Configuration.SCALED_IMAGE_DIRECTORY;
//            FileOutputStream fout;
            try (InputStream filecontent = filePart.getInputStream()) {
                List<String> imageSizes = Configuration.images_size;
                String[] size = imageSizes.get(0).split("x");
                int width = Integer.valueOf(size[0]);
                int height = Integer.valueOf(size[1]);
                filePart.delete();
                pathScaled = pathScaled + File.separator + newFileName + "_" + width + "_" + height + "." + fileType;
                BufferedImage srcImage = ImageIO.read(filecontent);
                BufferedImage resizedImage = Scalr.resize(srcImage, Scalr.Mode.BEST_FIT_BOTH, width, height);
                ImageIO.write(resizedImage, fileType, new File(pathScaled));
                srcImage.flush();
                resizedImage.flush();
                srcImage = null;
                resizedImage = null;
//                fout = new FileOutputStream(filePath);
//                int len = -1;
//                byte[] buffer = new byte[8192];
//                filePart.delete();
//                while ((len = filecontent.read(buffer)) != -1) {
//                    fout.write(buffer, 0, len);
//                }
//                buffer = null;
            }
//            fout.close();

            ImageInfo imageInfo = new ImageInfo(newFileName, fileType, pathScaled);
            ImageQueue.put(imageInfo);

            JSONObject obj = new JSONObject();
            obj.put("filename", newFileName);
            obj.put("filetype", "." + fileType);
            obj.put("filepath", Configuration.url_static + Configuration.SCALED_IMAGE_DIRECTORY);
            return new DataResponse(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return DataResponse.UNKNOWN_EXCEPTION;

    }

    private String getFileName(Part part) {
        String partHeader = part.getHeader("content-disposition");
        logger.info("Part Header = " + partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < arrayBytes.length; i++) {
            String hex = Long.toHexString(0xff & arrayBytes[i]);
            if (hex.length() == 1) {
                stringBuffer.append('0');
            }
            stringBuffer.append(hex);
        }
        return stringBuffer.toString();
    }

    public static String toHex(byte[] input) {
        try {
            MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
            digest256.update(input);
            return convertByteArrayToHexString(digest256.digest());
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(UploadImageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
