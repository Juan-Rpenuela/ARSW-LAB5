package edu.eci.arsw.blueprints.filter;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;

public class SubsamplingBlueprintFilterTest {

    @Test
    public void testRemovesEveryOtherPoint() {
        Point[] points = new Point[] {
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3),
                new Point(4, 4),
                new Point(5, 5)
        };
        Blueprint bp = new Blueprint("author", "bp1", points);
        SubsamplingBlueprintFilter filter = new SubsamplingBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        List<Point> filteredPoints = filtered.getPoints();
        Assert.assertEquals(3, filteredPoints.size());
        Assert.assertEquals(new Point(1,1), filteredPoints.get(0));
        Assert.assertEquals(new Point(3,3), filteredPoints.get(1));
        Assert.assertEquals(new Point(5,5), filteredPoints.get(2));
    }

    @Test
    public void testSinglePoint() {
        Point[] points = new Point[] {
                new Point(1, 1)
        };
        Blueprint bp = new Blueprint("author", "bp2", points);
        SubsamplingBlueprintFilter filter = new SubsamplingBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        Assert.assertEquals(1, filtered.getPoints().size());
    }

    @Test
    public void testEmpty() {
        Point[] points = new Point[] {};
        Blueprint bp = new Blueprint("author", "bp3", points);
        SubsamplingBlueprintFilter filter = new SubsamplingBlueprintFilter();
        Blueprint filtered = filter.filter(bp);
        Assert.assertEquals(0, filtered.getPoints().size());
    }
}
