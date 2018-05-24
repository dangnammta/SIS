/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.http.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author longmd
 */
public class BaseServlet extends HttpServlet{
	private static final Logger logger = Logger.getLogger(BaseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
//			LogRequestData requestData = new LogRequestData(req, LogConstants.CATEGORY_HC_REQUEST);
            doProcess(req, resp);
//			requestData.sendLogApi();
        } catch (IOException ex) {
        } catch (ServletException ser) {
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp);
    }

    protected void doProcess(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("do process data");
	}
    
    protected void responseJson(String content, HttpServletResponse resp) {
        try {
            resp.setCharacterEncoding("utf-8");
            resp.addHeader("Content-Type", "application/json; charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.print(content);
            out.close();
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
        }

    }

    public boolean checkValidParam(HttpServletRequest request, String[] params) {
        try {
            Set<String> s = new HashSet<String>();
            Enumeration<String> requestParam = request.getParameterNames();

            while (requestParam.hasMoreElements()) {
                String param = requestParam.nextElement();
                s.add(param.toLowerCase());
            }

            for (int i = 0; i < params.length; i++) {
                if (!s.contains(params[i].toLowerCase())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    
    public int checkPresentedParams(HttpServletRequest request, String[] params) {
        int presentedParams = 0;
        try {
            Set<String> s = new HashSet<String>();
            Enumeration<String> requestParam = request.getParameterNames();
            
            while (requestParam.hasMoreElements()) {
                String param = requestParam.nextElement();
                s.add(param.toLowerCase());
            }

            for (int i = 0; i < params.length; i++) {
                if (s.contains(params[i].toLowerCase())) {
                    presentedParams += Math.pow(2, i);
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return presentedParams;
    }
    
    public boolean checkRequiredParams(int presentedParams, int[] requiredParamsArray){
        int requiredParams = 0;
        for (int i = 0; i<requiredParamsArray.length; i++){
            requiredParams += Math.pow(2,requiredParamsArray[i]);
        }
        return (requiredParams == (requiredParams & presentedParams));
    }
    
    public boolean checkParamAt(int presentedParams, int paramPosition){
        int requiredParams = (int)Math.pow(2,paramPosition);        
        return (requiredParams == (requiredParams & presentedParams));
    }
}