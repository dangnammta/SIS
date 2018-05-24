package crdhn.sis.server;

import crdhn.sis.configuration.Config;
import crdhn.sis.controller.UserController;
import firo.Firo;
import static firo.Firo.*;

public class ServiceDaemon {

    public static void main(String[] args) throws NoSuchMethodException {

        Firo.getInstance().init(Config.getParamString("service", "host", "localhost"), Config.getParamInt("service", "port", 1301));
        Firo.getInstance().initializeControllerFromPackage(Config.getParamString("service", "controllerPackage", "crdhn.sis.controller"), ServiceDaemon.class);
        
        get("/hello", (req, res) -> {
            System.out.println("abc");
            return "";
        });
        //insert Organization
        UserController.insertAcountOrganization();
    }
}
