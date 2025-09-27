/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

/**
 *
 * @author hcadavid
 */

@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    // Utilizamos ConcurrentHashMap para manejar concurrencia este nos permite
    // realizar operaciones thread-safe jsjsj
    private final Map<Tuple<String,String>,Blueprint> blueprints=new ConcurrentHashMap<>();

    public InMemoryBlueprintPersistence() {
        Point[] pts1=new Point[]{new Point(140, 140),new Point(115, 115)};
        Point[] pts2=new Point[]{new Point(0,0),new Point(50, 50)};
        Point[] pts3=new Point[]{new Point(10,10),new Point(20, 20)};
        Blueprint bp1=new Blueprint("Karol", "House",pts1);
        Blueprint bp2=new Blueprint("Juan", "Backyard",pts2);
        Blueprint bp3 = new Blueprint("Karol", "House2",pts3);
        blueprints.put(new Tuple<>(bp1.getAuthor(),bp1.getName()), bp1);
        blueprints.put(new Tuple<>(bp2.getAuthor(),bp2.getName()), bp2);
        blueprints.put(new Tuple<>(bp3.getAuthor(),bp3.getName()), bp3);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new java.util.HashSet<>(blueprints.values());
    }
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        Tuple<String,String> key = new Tuple<>(bp.getAuthor(),bp.getName());
        // Operación atómica: inserta solo si no existe.
        Blueprint previous = blueprints.putIfAbsent(key, bp);
        if (previous != null){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(new Tuple<>(author, bprintname));
        if (bp == null) {
            throw new BlueprintNotFoundException("Blueprint not found for author=" + author + " name=" + bprintname);
        }
        return bp;
    }

    public Set<Blueprint> getBlueprintsByAuthor(String Author) throws BlueprintNotFoundException{
        Set<Blueprint> result = blueprints.values().stream()
                .filter(bp -> bp.getAuthor().equals(Author))
                .collect(Collectors.toSet());
        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("Author not found: " + Author);
        }
        return result;
    }



}
