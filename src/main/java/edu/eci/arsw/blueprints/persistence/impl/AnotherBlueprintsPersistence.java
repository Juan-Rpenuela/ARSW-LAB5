package edu.eci.arsw.blueprints.persistence.impl;

import java.util.Set;

import org.springframework.stereotype.Repository;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;

public class AnotherBlueprintsPersistence implements BlueprintsPersistence {

    @Override
    public Set<Blueprint> getAllBlueprints() {
        // Implementation here
        return null;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        // Implementation here
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        // Implementation here
        return null;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        // Implementation here
        return null;
    }

}
