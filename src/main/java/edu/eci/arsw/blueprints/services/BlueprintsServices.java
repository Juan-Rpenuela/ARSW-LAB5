/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filter.BlueprintFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */

@Service
public class BlueprintsServices {

    @Autowired
    @Qualifier("subsampling") // Cambia a "redundancy" para usar el otro filtro
    BlueprintFilter blueprintFilter;

    @Autowired
    BlueprintsPersistence bpp;

    public BlueprintsServices (BlueprintsPersistence bpp){
        this.bpp= bpp;
    }


    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException{
        try {
            bpp.saveBlueprint(bp);
        } catch (Exception e) {
            System.err.println("Error saving blueprint: " + e.getMessage());
            throw new BlueprintPersistenceException("Error saving blueprint: " + e.getMessage());
        }
    }
    
    public Set<Blueprint> getAllBlueprints(){
        Set<Blueprint> blueprints = bpp.getAllBlueprints();
        Set<Blueprint> filteredBlueprints = new java.util.HashSet<>();
        for (Blueprint bp : blueprints) {
            filteredBlueprints.add(blueprintFilter.filter(bp));
        }
        return filteredBlueprints;
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        Blueprint bp = bpp.getBlueprint(author, name);
        return blueprintFilter.filter(bp);
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        Set<Blueprint> blueprints = bpp.getBlueprintsByAuthor(author);
        Set<Blueprint> filteredBlueprints = new HashSet<>();
        for (Blueprint bp : blueprints) {
            filteredBlueprints.add(blueprintFilter.filter(bp));
        }
        return filteredBlueprints;
    }
    
}
