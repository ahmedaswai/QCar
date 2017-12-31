package com.qcar.service.handlers;

import com.qcar.dao.DaoFactory;
import com.qcar.dao.UserDao;
import com.qcar.model.mongo.User;
import com.qcar.model.mongo.service.ServiceReturnList;
import com.qcar.model.mongo.service.ServiceReturnSingle;
import com.qcar.model.mongo.service.exception.ErrorCodes;
import com.qcar.model.mongo.service.exception.QCarSecurityException;
import com.qcar.utils.MediaType;
import com.qcar.utils.SecurityUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;

/**
 * Created by ahmedissawi on 12/28/17.
 */
public class UserHandler {


    public void findUserById(RoutingContext ctx){

        final UserDao dao = DaoFactory.getUserDao();
            Long id = Long.parseLong(ctx.request().getParam("id"));

            User u = dao.findUserById(id);
            Buffer rs = ServiceReturnSingle.response(dao.saveOrMerge(u));
            ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                    .setStatusCode(200).end(rs);


    }
    public void findAllUsers(RoutingContext ctx){

        final UserDao dao = DaoFactory.getUserDao();


        List<User> lst = dao.findAll();

        Buffer rs = ServiceReturnList.response(lst);

        ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);


    }
    public void doAddUser(RoutingContext ctx){


        final UserDao dao = DaoFactory.getUserDao();
            User user = Json.decodeValue(ctx.getBody(), User.class);
            String hashedPassword=SecurityUtils.hashPassword(user.getPassword());
            user.password(hashedPassword);

            Buffer rs = ServiceReturnSingle.response(dao.saveOrMerge(user));
            ctx.response().
                    putHeader("content-type", MediaType.APPLICATION_JSON)

                    .setStatusCode(200).end(rs);


    }
    public void doDeleteUser(RoutingContext ctx){


        final UserDao dao = DaoFactory.getUserDao();
        Long id = Long.parseLong(ctx.request().getParam("id"));
        Buffer rs = ServiceReturnSingle.response(dao.deleteById(id));

        ctx.response().
                putHeader("content-type", MediaType.APPLICATION_JSON)

                .setStatusCode(200).end(rs);


    }
    public void doLogin(RoutingContext ctx) {
        JsonObject login = ctx.getBodyAsJson();

        String password = login.getString("password");
        String userName = login.getString("userName");

        LoginResult rs = checkLogin(userName, password);

        switch (rs) {

            case INVALID_USER_NAME:
                ctx.fail(new QCarSecurityException(ErrorCodes.USER_NOT_FOUND));
                break;
            case INVALID_PASSWORD:
                ctx.fail(new QCarSecurityException(ErrorCodes.INVALID_PASSWORD));
                break;
            case LOGIN_SUCCESS:
                ctx.response()
                        .putHeader("content-type", MediaType.APPLICATION_JSON)
                        .setStatusCode(200).
                        end(ServiceReturnSingle.response(true));
                break;
        }


    }

    private LoginResult checkLogin(String user, String pass) {
        final UserDao dao = DaoFactory.getUserDao();

        Optional<User> u = dao.findUserByLoginName(user);

        if (u.isPresent()) {
            boolean status = SecurityUtils.checkPassword(pass, u.get().getPassword());
            if (!status) {
                return LoginResult.INVALID_PASSWORD;
            }
        } else {
            return LoginResult.INVALID_USER_NAME;
        }

        return LoginResult.LOGIN_SUCCESS;


    }

}