/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.render;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import firo.TemplateEngine;
import java.util.Map;

/**
 *
 * @author hoaronal
 */
public class RenderEngine extends TemplateEngine {

    private Configuration configuration;
    private static RenderEngine _instance;

    public static RenderEngine getInstance() {
        if (_instance == null) {
            _instance = new RenderEngine();
        }
        return _instance;
    }

    public RenderEngine() {
        this.configuration = createDefaultConfiguration();
    }

    public RenderEngine(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String render(Map<String, Object> attributes, String tplPath) {
        try {
            StringWriter stringWriter = new StringWriter();
            Template template = configuration.getTemplate(tplPath);
            template.process(attributes, stringWriter);
            return stringWriter.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private Configuration createDefaultConfiguration() {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
        conf.setClassForTemplateLoading(RenderEngine.class, "/tpl/");
        return conf;
    }

}
