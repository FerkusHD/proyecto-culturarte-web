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

    // Registers Spring-WS MessageDispatcherServlet under /ws/*
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // Placeholder schema for future SOAP operations
    @Bean
    public XsdSchema culturarteSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/culturarte.xsd"));
    }

    // Expose a WSDL named culturarte at /ws/culturarte.wsdl
    @Bean(name = "culturarte")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema culturarteSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CulturartePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://culturarte.com/soap");
        wsdl11Definition.setSchema(culturarteSchema);
        return wsdl11Definition;
    }
}
