package com.qcar.service.handlers;

import com.qcar.service.ctrl.CtrlFactory;
import com.qcar.service.handlers.business.DriverHandler;
import com.qcar.service.handlers.business.UserHandler;
import com.qcar.service.handlers.config.QCarErrorHandler;
import com.qcar.service.handlers.config.ResourceNotFoundHandler;
import com.qcar.service.handlers.config.SecurityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ahmedissawi on 12/28/17.
 */
public class HandlerFactory {

    private static transient volatile UserHandler userHandler;
    private static transient volatile QCarErrorHandler errorHandler;
    private static transient volatile SecurityHandler securityHandler;
    private static transient volatile ResourceNotFoundHandler resourceNotFoundHandler;
    private static transient volatile DriverHandler driverHandler;


    private static final Logger logger= LoggerFactory.getLogger(HandlerFactory.class);

    public static UserHandler userHandler() {

        if (userHandler == null) {
            userHandler = new UserHandler();
            logger.debug("Creating Instance of UserHandler");
        }
        return userHandler;
    }

    public static QCarErrorHandler errorHandler() {

        if (errorHandler == null) {
            errorHandler = new QCarErrorHandler();
            logger.debug("Creating Instance of UserHandler");
        }
        return errorHandler;
    }
    public static SecurityHandler securityHandler() {

        if (securityHandler == null) {
            securityHandler = new SecurityHandler();
            logger.debug("Creating Instance of SecurityHandler");
        }
        return securityHandler;
    }
    public static ResourceNotFoundHandler resourceNotFoundHandler() {

        if (resourceNotFoundHandler == null) {
            resourceNotFoundHandler = new ResourceNotFoundHandler();
            logger.debug("Creating Instance of ResourceNotFoundHandler");
        }
        return resourceNotFoundHandler;
    }

    public static DriverHandler driverHandler() {

        if (driverHandler == null) {
            driverHandler = new DriverHandler();
            logger.debug("Creating Instance of DriverHandler");
        }
        return driverHandler;
    }

}
