package edu.eci.arsw.blueprints.filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("redundancy")
public class RedundancyBlueprintFilter implements BlueprintFilter {

    @Override
    public Blueprint filter(Blueprint blueprint) {
        List<Point> originalPoints = blueprint.getPoints();
        List<Point> filteredPoints = new ArrayList<>();
        if (!originalPoints.isEmpty()) {
            filteredPoints.add(originalPoints.get(0));
            for (int i = 1; i < originalPoints.size(); i++) {
                Point prev = originalPoints.get(i - 1);
                Point curr = originalPoints.get(i);
                if (!(curr.getX() == prev.getX() && curr.getY() == prev.getY())) {
                    filteredPoints.add(curr);
                }
            }
        }
        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), filteredPoints.toArray(new Point[0]));
    }
}