/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author neblg
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
            addRestResourceClasses(resources);
            return resources;
        }
        
    

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.mycompany.vizsgaremek.config.CorsFilter.class);
        resources.add(com.mycompany.vizsgaremek.controller.AddressesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.AnaliticsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.CarsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.CartItemsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.EmailVerificationsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.InvoicesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.JWTController.class);
        resources.add(com.mycompany.vizsgaremek.controller.ManufacturersController.class);
        resources.add(com.mycompany.vizsgaremek.controller.MotorsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.OTPController.class);
        resources.add(com.mycompany.vizsgaremek.controller.OrderItemsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.OrdersController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartCompatibilityController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartImagesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartVariantsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PasswordResetsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PaymentsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.ReviewsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.SendEmailController.class);
        resources.add(com.mycompany.vizsgaremek.controller.SessionsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.StaticResourceController.class);
        resources.add(com.mycompany.vizsgaremek.controller.TFAController.class);
        resources.add(com.mycompany.vizsgaremek.controller.TrucksController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UserLogsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UserTwofaController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UserVehiclesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UsersController.class);
        resources.add(org.jboss.resteasy.core.AcceptHeaderByFileSuffixFilter.class);
        resources.add(org.jboss.resteasy.core.AsynchronousDispatcher.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor.class);
        resources.add(org.jboss.resteasy.plugins.interceptors.MessageSanitizerContainerResponseFilter.class);
        resources.add(org.jboss.resteasy.plugins.providers.AsyncStreamingOutputProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.ByteArrayProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.DataSourceProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultBooleanWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultNumberWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.DefaultTextPlain.class);
        resources.add(org.jboss.resteasy.plugins.providers.DocumentProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.FileProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.FileRangeWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.IIOImageProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.InputStreamProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.JaxrsFormProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.JaxrsServerFormUrlEncodedProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.MultiValuedParamConverterProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.ReaderProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.SourceProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.StreamingOutputProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.StringTextStar.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.MapProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.ListMultipartReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.ListMultipartWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MimeMultipartProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.MultipartWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.XopWithMultipartRelatedReader.class);
        resources.add(org.jboss.resteasy.plugins.providers.multipart.XopWithMultipartRelatedWriter.class);
        resources.add(org.jboss.resteasy.plugins.providers.sse.SseEventProvider.class);
        resources.add(org.jboss.resteasy.plugins.providers.sse.SseEventSinkInterceptor.class);
    }
}
