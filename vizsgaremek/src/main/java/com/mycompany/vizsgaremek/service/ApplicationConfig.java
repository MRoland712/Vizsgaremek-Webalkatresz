/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author ddori
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.mycompany.vizsgaremek.controller.UsersController.class);
        resources.add(com.mycompany.vizsgaremek.model.service.ManufacturersFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.MotorBrandsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.MotorModelsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.PartCompatibilityFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.PartImagesFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.PartsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.ShippingMethodsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.UserTwofaFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.VehicleBrandsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.VehicleModelsFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.WarehouseStockFacadeREST.class);
        resources.add(com.mycompany.vizsgaremek.model.service.WarehousesFacadeREST.class);
    }
    
}
