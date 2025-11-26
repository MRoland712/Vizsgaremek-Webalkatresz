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
        resources.add(com.mycompany.vizsgaremek.controller.AddressesController.class);
        resources.add(com.mycompany.vizsgaremek.controller.UsersController.class);
    }
}
