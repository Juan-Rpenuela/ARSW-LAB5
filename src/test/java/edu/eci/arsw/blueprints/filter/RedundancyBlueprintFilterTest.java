package edu.eci.arsw.blueprints.filter;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;

public class RedundancyBlueprintFilterTest {

    @Test
    public void testRemovesConsecutiveDuplicates() {
        Point[] points = new Point[] {
                new Point(1, 1),
                new Point(1, 1),
                new Point(2, 2),
                new Point(2, 2),
                new Point(3, 3),
                new Point(3, 3),
                new Point(3, 3),
                new Point(4, 4)
        };
        Blueprint bp = new Blueprint("author", "bp1", points);
        RedundancyBlueprintFilter filter = new RedundancyBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        List<Point> filteredPoints = filtered.getPoints();
        Assert.assertEquals(4, filteredPoints.size());
        Assert.assertEquals(new Point(1,1), filteredPoints.get(0));
        Assert.assertEquals(new Point(2,2), filteredPoints.get(1));
        Assert.assertEquals(new Point(3,3), filteredPoints.get(2));
        Assert.assertEquals(new Point(4,4), filteredPoints.get(3));
    }

    @Test
    public void testNoDuplicates() {
        Point[] points = new Point[] {
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3)
        };
        Blueprint bp = new Blueprint("author", "bp2", points);
        RedundancyBlueprintFilter filter = new RedundancyBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        Assert.assertEquals(3, filtered.getPoints().size());
    }

    @Test
    public void testAllDuplicates() {
        Point[] points = new Point[] {
                new Point(1, 1),
                new Point(1, 1),
                new Point(1, 1)
        };
        Blueprint bp = new Blueprint("author", "bp3", points);
        RedundancyBlueprintFilter filter = new RedundancyBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        Assert.assertEquals(1, filtered.getPoints().size());
    }

    @Test
    public void testEmpty() {
        Point[] points = new Point[] {};
        Blueprint bp = new Blueprint("author", "bp4", points);
        RedundancyBlueprintFilter filter = new RedundancyBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        Assert.assertEquals(0, filtered.getPoints().size());
    }
}
