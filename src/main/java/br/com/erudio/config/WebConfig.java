package br.com.erudio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.originPatterns}")
    //recebe uma string contendo as origens permitidas atraves arquivo de configuração
    private String corsOriginPatterns = "";

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        /*
        cria uma lista contendo as origens que trouxemos do application.yml,
        separando-as em origens diferentes quando encontra uma virgula
        */
        var allowedOrigins = corsOriginPatterns.split(",");

        //Regista quais endpoints aceitarão a configuração do cors (regitramos todos, configuração global)
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                //permite todos os verbos http
                .allowedMethods("*")
                .allowCredentials(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //Content Negotiation via Header Params (ideal)
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("yaml", MediaType.APPLICATION_YAML);


    }
}
