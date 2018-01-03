package com.qcar.service.ctrl;

import com.qcar.service.handlers.HandlerFactory;
import com.qcar.service.handlers.business.UserHandler;
import com.qcar.utils.MediaType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;


/**
 * Created by ahmedissawi on 12/27/17.
 */
public class UserCtrl implements ICtrl {



     UserCtrl(){

    }
    @Override
    public void registerHandler(Router mainRouter) {

        UserHandler handler = HandlerFactory.userHandler();


        mainRouter.post()
                .path("/login")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .handler(BodyHandler.create())

                .handler(handler::doLogin);

        mainRouter.post()
                .path(getRoute() + "/password/reset")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .handler(BodyHandler.create())

                .handler(handler::doResetPassword);

        mainRouter.get()
                .path(getRoute() + "/id/:id")
                .produces(MediaType.APPLICATION_JSON)

                .handler(handler::findById);

        mainRouter.get()
                .path(getRoute() + "/permissions")
                .produces(MediaType.APPLICATION_JSON)

                .handler(handler::findAllPermissions);

        mainRouter.get()
                .path(getRoute() + "/login/:loginName")
                .produces(MediaType.APPLICATION_JSON)

                .handler(handler::findByLoginName);

        mainRouter.get()
                .path(getRoute())
                .produces(MediaType.APPLICATION_JSON)

                .handler(handler::findAll);


        mainRouter.post()
                .path(getRoute())
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .handler(BodyHandler.create())
                .handler(handler::doAdd);

        mainRouter.put()
                .path(getRoute())
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .handler(BodyHandler.create())
                .handler(handler::doAdd);

        mainRouter.put()
                .path(getRoute()+"/:id/activate")
                .produces(MediaType.APPLICATION_JSON)
                .handler(handler::doActivate);

        mainRouter.put()
                .path(getRoute()+"/:id/de-activate")
                .produces(MediaType.APPLICATION_JSON)
                .handler(handler::doDeActivate);

        mainRouter.delete()
                .path(getRoute()+"/id/:id")
                .produces(MediaType.APPLICATION_JSON)

                .handler(handler::doDelete);

        mainRouter.delete()
                .path(getRoute()+"/bulk")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .handler(BodyHandler.create())
                .handler(handler::doDeleteBulk);

    }

    @Override
    public String getRoute() {
        return "/api/users";
    }
}
