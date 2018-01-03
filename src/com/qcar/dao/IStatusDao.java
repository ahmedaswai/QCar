package com.qcar.dao;

import com.qcar.model.mongo.GenericEntity;
import com.qcar.model.mongo.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

public interface IStatusDao<T extends GenericEntity> {
    public static final String STATUS = "status";

    default T changeStatus(GenericDao dao,Long id,Boolean status,Class<T>cls){
        Query<T> query = dao.getDataStore().
                createQuery(cls).field(GenericDao.ID).
                equal(id);
        UpdateOperations<T> updateOperations=dao.getDataStore(). createUpdateOperations(cls).set(STATUS,status);
        T t= dao.getDataStore().findAndModify(query,updateOperations);
        dao.getCache().add(t);
        return t;
    }
}
