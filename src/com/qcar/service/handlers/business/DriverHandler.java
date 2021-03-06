package com.qcar.service.handlers.business;

import com.qcar.dao.DaoFactory;
import com.qcar.dao.DriverDao;
import com.qcar.dao.GenericDao;
import com.qcar.model.mongo.entity.Driver;
import com.qcar.model.mongo.embedded.FileStore;
import com.qcar.model.mongo.embedded.Location;
import com.qcar.model.service.result.ServiceReturnList;
import com.qcar.model.service.result.ServiceReturnSingle;
import com.qcar.model.service.exception.ErrorCodes;
import com.qcar.model.service.exception.QCarException;
import com.qcar.utils.MediaType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DriverHandler extends GenericHandler<Driver>{
    final DriverDao dao;

    public DriverHandler(){
        dao = DaoFactory.driverDao();
    }

    @Override
    public GenericDao<Driver> getDao() {
        return dao;
    }

    public void findInDistance(RoutingContext ctx){



        Double lng=Double.parseDouble(ctx.request().getParam("lng"));
        Double lat=Double.parseDouble(ctx.request().getParam("lat"));
        Long radius=Long.parseLong(ctx.request().getParam("radius"));


        Location loc=new Location().
                coordinates(
                new Double[]{lng,lat}).
                range(radius);
        List<Driver> lst = dao.findInDistance(loc);

        Buffer rs = ServiceReturnList.response(lst);

        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);


    }

    public void findAllActiveOnline(RoutingContext ctx){
        List<Driver> lst = dao.findAllActiveOnline();

        Buffer rs = ServiceReturnList.response(lst);

        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);
    }

    public void findAllActive(RoutingContext ctx) {
        List<Driver> lst = dao.findAllActive();

        Buffer rs = ServiceReturnList.response(lst);

        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);
    }
    public void findPic(RoutingContext ctx){
        Long id = Long.parseLong(ctx.request().getParam("id"));
        final FileStore pic=dao.findDriverPic(id);

        JsonObject config=(JsonObject) ctx.data().get("config");
        String filePath=String.join(File.separator,config.getString("web-root-dir"),pic.getFileName());
        Path path = Paths.get(filePath);
        try {
            Files.write(path, pic.getContents());
            ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                    .setStatusCode(200).end(ServiceReturnSingle.response(true));
        } catch (IOException e) {
            throw new QCarException(e, ErrorCodes.ERROR_DOWNLOAD);
        }


    }


    public void doActivate(RoutingContext ctx){

        Long id = Long.parseLong(ctx.request().getParam("id"));

        Driver u = dao.changeStatus(id,true);
        Buffer rs = ServiceReturnSingle.response(u);
        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);
    }
    public void doDeActivate(RoutingContext ctx){

        Long id = Long.parseLong(ctx.request().getParam("id"));

        Driver u = dao.changeStatus(id,false);
        Buffer rs = ServiceReturnSingle.response(u);
        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);
    }
    public void doUploadPic(RoutingContext ctx){

        Long id = Long.parseLong(ctx.request().getParam("id"));
        Driver d=dao.findById(id).get();

        ctx.response().setChunked(true);


        FileUpload fileUpload=ctx.fileUploads().iterator().next();

        if(fileUpload.size()==0||!fileUpload.contentType().contains("image")){
            throw new QCarException("You can not Upload Images with Size 0 or " +
                    " with Invalid Content", ErrorCodes.ERROR_UPLOAD);
        }

        FileStore store=FileStore.instance(fileUpload);
        d.pic(store);

        dao.update(d);
        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(ServiceReturnSingle.response(true));
    }


}
