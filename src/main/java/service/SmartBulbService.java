package service;


import beans.HueBridge;
import beans.HueBulb;
import beans.LifxBulb;
import beans.SmartBulb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("smartbulbs")
@Stateless
public class SmartBulbService {

    @PersistenceContext(unitName = "persistence_unit")
    private EntityManager entityManager;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<SmartBulb> getSmartBulbs(){
        CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
        cq.select(cq.from(HueBulb.class));
        List<SmartBulb> smartBulbs = entityManager.createQuery(cq).getResultList();
        return smartBulbs;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateSmartBulb(List<SmartBulb> smartBulbs){

        CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
        cq.select(cq.from(HueBulb.class));
        List<SmartBulb> storedBulbs = entityManager.createQuery(cq).getResultList();

        cq.select(cq.from(HueBridge.class));
        List<HueBridge> bridges = entityManager.createQuery(cq).getResultList();
        HueBridge.setBridges(bridges.stream().collect(Collectors.toMap(HueBridge::getId, b->b, (b1,b2)->b1)));

        Map<Long, SmartBulb> storedBulbMap = storedBulbs.stream().collect(Collectors.toMap(SmartBulb::getObjectid, b->b, (b1, b2)->b1));
        for (SmartBulb smartBulb : smartBulbs) {
            SmartBulb storedBulb = storedBulbMap.get(smartBulb.getObjectid());
            if(storedBulb == null) continue;

            if(storedBulb instanceof HueBulb)
            {
                HueBulb hueBulb = new HueBulb(smartBulb);
                hueBulb.setBridgeId(((HueBulb) storedBulb).getBridgeId());
                hueBulb.update();
                entityManager.merge(hueBulb);
            }
            if(storedBulb instanceof LifxBulb)
            {
                LifxBulb lifxBulb = (LifxBulb) smartBulb;
                entityManager.merge(lifxBulb);
            }
        }
    }

    @GET
    @Path("discover")
    public List<SmartBulb> findSmartBulbs(@QueryParam("save") @DefaultValue("N") String save){
        javax.persistence.criteria.CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();

        cq.select(cq.from(HueBridge.class));
        List<HueBridge> bridges = entityManager.createQuery(cq).getResultList();
        HueBridge.setBridges(bridges.stream().collect(Collectors.toMap(HueBridge::getId, b->b, (b1,b2)->b1)));
        List<HueBulb> hueBulbs = bridges.stream().map(HueBridge::findBulbs).flatMap(List::stream).collect(Collectors.toList());

        List<LifxBulb> lifxBulbs = LifxBulb.findAllBulbs();

        List<SmartBulb> smartBulbs = new ArrayList<>();
        smartBulbs.addAll(lifxBulbs);
        smartBulbs.addAll(hueBulbs);

        if(save.equals("Y"))
        {
            entityManager.createQuery("delete from HUE_BULB").executeUpdate();
//            entityManager.createQuery("delete from HUE_BULB").executeUpdate();
            smartBulbs.forEach(bulb -> {
                if(bulb instanceof HueBulb)
                    entityManager.persist((HueBulb) bulb);
                if(bulb instanceof LifxBulb)
                    entityManager.persist((LifxBulb) bulb);
            });
        }

        return smartBulbs;
    }
}
