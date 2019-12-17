package com.cocoker.handler;

import com.cocoker.config.ProjectUrl;
import com.cocoker.exception.AdminAuthorizeException;
import com.cocoker.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description:
 * @Author: y
 * @CreateDate: 2019/1/9 7:19 PM
 * @Version: 1.0
 */
@ControllerAdvice
public class AdminExceptionHandler {

    @Autowired
    private ProjectUrl projectUrl;

    @Autowired
    private TipService tipService;

    @ExceptionHandler(value = AdminAuthorizeException.class)
    public ModelAndView handlerAuthorizeException() {
        return new ModelAndView("redirect:".concat(tipService.getReturnUrl()).concat("/cocoker/admin/login"));
//        return new ModelAndView("redirect:".concat(projectUrl.getReturnUrl()).concat("/cocoker/admin/login"));
    }
}
