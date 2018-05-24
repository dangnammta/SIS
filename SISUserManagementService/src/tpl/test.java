package tpl;

import crdhn.sis.model.ReportInfo;
import crdhn.sis.utils.DataResponse;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author namdv
 */
public class test {

    public static void main(String[] args) {
        System.out.println(new DataResponse(ReportInfo.ReportStatus.values()));
        
    }

}
