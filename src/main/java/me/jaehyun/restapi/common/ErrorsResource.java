package me.jaehyun.restapi.common;

import me.jaehyun.restapi.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : kjh
 * github: https://github.com/jaehyun8719
 * email: jaehyun8719@gmail.com
 * <p>
 * Date: 2019-03-25
 * Copyright(©) 2019 by jaehyun.
 */
public class ErrorsResource extends Resource<Errors> {

    public ErrorsResource(Errors errors, Link... links) {
        super(errors, links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}
