package com.culturarte.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // ==================== USUARIOS ====================
    @Bean
    public XsdSchema usuariosSchema() {
        return new SimpleXsdSchema(new ClassPathResource("ws/usuarios.xsd"));
    }

    @Bean(name = "usuarios")
    public DefaultWsdl11Definition usuariosWsdl(XsdSchema usuariosSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("UsuariosPort");
        definition.setLocationUri("/ws/usuarios");
        definition.setTargetNamespace("http://www.culturarte.com/ws/usuarios");
        definition.setSchema(usuariosSchema);
        return definition;
    }

    // ==================== PROPUESTAS ====================
    @Bean
    public XsdSchema propuestasSchema() {
        return new SimpleXsdSchema(new ClassPathResource("ws/propuestas.xsd"));
    }

    @Bean(name = "propuestas")
    public DefaultWsdl11Definition propuestasWsdl(XsdSchema propuestasSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("PropuestasPort");
        definition.setLocationUri("/ws/propuestas");
        definition.setTargetNamespace("http://www.culturarte.com/ws/propuestas");
        definition.setSchema(propuestasSchema);
        return definition;
    }

    // ==================== CATEGORIAS ====================
    @Bean
    public XsdSchema categoriasSchema() {
        return new SimpleXsdSchema(new ClassPathResource("ws/categorias.xsd"));
    }

    @Bean(name = "categorias")
    public DefaultWsdl11Definition categoriasWsdl(XsdSchema categoriasSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("CategoriasPort");
        definition.setLocationUri("/ws/categorias");
        definition.setTargetNamespace("http://www.culturarte.com/ws/categorias");
        definition.setSchema(categoriasSchema);
        return definition;
    }

    // ==================== CULTURARTE GENERAL (ping) ====================
    @Bean
    public XsdSchema culturarteSchema() {
        return new SimpleXsdSchema(new ClassPathResource("ws/culturarte.xsd"));
    }

    @Bean(name = "culturarte")
    public DefaultWsdl11Definition culturarteWsdl(XsdSchema culturarteSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("CulturartePort");
        definition.setLocationUri("/ws/core");
        definition.setTargetNamespace("http://culturarte.com/soap");
        definition.setSchema(culturarteSchema);
        return definition;
    }
}
