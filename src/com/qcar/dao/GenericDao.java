package com.qcar.dao;

import com.qcar.model.mongo.entity.GenericEntity;
import com.qcar.model.mongo.entity.Order;
import com.qcar.model.mongo.entity.Sequence;
import com.mongodb.WriteConcern;
import com.qcar.model.mongo.entity.User;
import com.qcar.service.cache.QCarCache;
import com.qcar.utils.Constants;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by ahmedissawi on 12/8/17.
 */
public abstract class GenericDao<T extends GenericEntity> implements IDao<T> {


    public static final String ID = "id";
    public static final String UPDATED_ON = "updatedOn";
    private static final Logger logger= LoggerFactory.getLogger(GenericDao.class);

    private final QCarCache cache;

    protected GenericDao(QCarCache cache) {
        this.cache = cache;
    }

    public Datastore getDataStore() {

       return DatabaseClient.INSTANCE.connect().
               db(Constants.DB_NAME).
               startScanning(Constants.MONGO_MODEL_CLS).
                datastore();

    }

    @Override
    public Boolean delete(T u) {
       return deleteById(u.getId());
    }

    @Override
    public Boolean deleteById(Long id) {
        Query query = getDataStore().
                createQuery(User.class).field(ID).
                equal(id);
        Boolean status= getDataStore().findAndDelete(query)!=null;
        if(status){
            getCache().removeItem(getEntity(),id);
        }
        return status;
    }
    @Override
    public T update(T t) {
        WriteConcern concern = WriteConcern.ACKNOWLEDGED;

        getDataStore().merge(t, concern);
        return t;
    }


    @Override
    public T saveOrMerge(T t) {
        if (t.getId() == null) {
            setId(t);
            save(t);
        } else {
            update(t);
        }
        getCache().add(t);
        return t;
    }


    protected void setId(T entity) {

        if (entity.getId() == null) {
            String entityName = entity.getCollectionName();
            Long id = 1L;
            final Query<Sequence> q = getDataStore().createQuery(Sequence.class).field(Sequence.COLLECTION_NAME).equal(entityName);
            if (q.asList().isEmpty()) {
                getDataStore().save(new Sequence(entityName, id));
            } else {
                final UpdateOperations<Sequence> updateOperations = getDataStore().createUpdateOperations(Sequence.class)
                        .inc(Sequence.ID, 1);
                id = getDataStore().findAndModify(q, updateOperations).getId();


            }
            if (id != null) {
                entity.id(id);
            }


        }
    }

    public T save(T t) {

        if (t != null) {

            setId(t);
            prepareAdd(t);
            getDataStore().save(t);
            getCache().add(t);
        }
        return t;
    }

    public Boolean isExists(T entity) {

        Query query = getDataStore().
                createQuery(entity.getClass()).field(ID).
                equal(entity.getId());

        long count = query.count();
        return count > 0;
    }
    public List<T>findAll(){

        if(getEntity().isCached()) {
            List<T> values = (List<T>) getCache().getAllByEntity(getEntity());
            if (values != null && !values.isEmpty()) {
                return values;
            }
        }
        Query<T> query = getDataStore().
                createQuery(getEntityClass()).
                order(Sort.descending(UPDATED_ON));
        return query.asList();
    }


    public Optional<T> findById(Long id) {
        if(getEntity().isCached()){
            Optional<T> cached=(Optional<T>)getCache().get(id,getEntity());
            if(cached!=null){
                return cached;
            }
        }
        T t = getDataStore().
                createQuery(getEntityClass()).field(ID).
                equal(id).get();

        return Optional.ofNullable(t);
    }



    public QCarCache getCache() {
        return cache;
    }

    abstract public Class<T> getEntityClass();
    abstract public T getEntity();
     public void prepareAdd(T t){
         logger.debug("Starting Preparing Entity {} Id {} for saving",t.getCollectionName(),t.getId());

     }

    public Long getUniqueSerial(T t){

            Integer year= LocalDateTime.now().getYear();
            String id=t.getId().toString();
            StringBuilder idbt=new StringBuilder();
            for(int count=id.length();count<6;count++){
                idbt.append(0);
            }
            String orderNum=String.join("",year.toString(),idbt,id);
            return Long.parseLong(orderNum);
        }

    public void populateCache(){
        cache.addToCache(getEntity(),findAll());
    }
}
