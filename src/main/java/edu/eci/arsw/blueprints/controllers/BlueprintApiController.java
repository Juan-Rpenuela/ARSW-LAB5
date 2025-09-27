package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;


@RestController
@RequestMapping("/blueprints")
public class BlueprintApiController {
    @Autowired
    BlueprintsServices blueprintService;
    
    // Obtener todos los blueprints
    @GetMapping
    public ResponseEntity<Set<Blueprint>> getAll(){
        Set<Blueprint> all = blueprintService.getAllBlueprints();
    return new ResponseEntity<>(all, HttpStatus.ACCEPTED);
    }

    // Obtener todos los blueprints de un autor
    @GetMapping("/{author}")
    public ResponseEntity<Set<Blueprint>> getByAuthor(@PathVariable String author){
        try {
            Set<Blueprint> bps = blueprintService.getBlueprintsByAuthor(author);
            return new ResponseEntity<>(bps, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtener blueprint específico
    @GetMapping("/{author}/{bprintname}")
    public ResponseEntity<Blueprint> getBlueprint(@PathVariable String author, @PathVariable String bprintname){
        try {
            Blueprint blueprint = blueprintService.getBlueprint(author, bprintname);
            if (blueprint==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(blueprint,HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            Logger.getLogger(BlueprintApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
    }

    // Crear un nuevo blueprint
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Blueprint> createNewBlueprint(@RequestBody Blueprint newbp){
        try {
            blueprintService.addNewBlueprint(newbp);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException e) {
            Logger.getLogger(BlueprintApiController.class.getName()).log(Level.SEVERE, null, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Blueprint> updateBlueprint(@RequestBody Blueprint newbp){
        try {
            blueprintService.addNewBlueprint(newbp);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException e) {
            Logger.getLogger(BlueprintApiController.class.getName()).log(Level.SEVERE, null, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
