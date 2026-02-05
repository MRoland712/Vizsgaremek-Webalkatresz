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
        resources.add(com.mycompany.vizsgaremek.controller.CarsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.JWTController.class);
        resources.add(com.mycompany.vizsgaremek.controller.ManufacturersController.class);
        resources.add(com.mycompany.vizsgaremek.controller.MotorsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.OTPController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartImagesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartVariantsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.PartsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.SendEmailController.class);
        resources.add(com.mycompany.vizsgaremek.controller.StaticResourceController.class);
        resources.add(com.mycompany.vizsgaremek.controller.TFAController.class);
        resources.add(com.mycompany.vizsgaremek.controller.TrucksController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UserLogsController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UserTwofaController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UsersController.class);
    }
}
