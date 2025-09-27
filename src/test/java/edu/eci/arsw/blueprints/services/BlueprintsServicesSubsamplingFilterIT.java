package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filter.SubsamplingBlueprintFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {edu.eci.arsw.blueprints.config.AppConfig.class})
public class BlueprintsServicesSubsamplingFilterIT {

    @Autowired
    private BlueprintsServices blueprintsServices;

    @Autowired
    @Qualifier("subsampling")
    private SubsamplingBlueprintFilter subsamplingFilter;


    @Test
    public void testBlueprintIsFilteredBySubsampling() throws Exception {
        Point[] points = new Point[] {
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3),
                new Point(4, 4),
                new Point(5, 5)
        };
        Blueprint bp = new Blueprint("authorIT", "bpSubsampling", points);
        blueprintsServices.addNewBlueprint(bp);
        Blueprint filtered = blueprintsServices.getBlueprint("authorIT", "bpSubsampling");
        Assert.assertEquals(3, filtered.getPoints().size());
        Assert.assertEquals(new Point(1,1), filtered.getPoints().get(0));
        Assert.assertEquals(new Point(3,3), filtered.getPoints().get(1));
        Assert.assertEquals(new Point(5,5), filtered.getPoints().get(2));
    }
}
