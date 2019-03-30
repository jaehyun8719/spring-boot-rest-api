package me.jaehyun.restapi.index;

import me.jaehyun.restapi.events.EventController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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
@RestController
public class IndexController {

    @GetMapping("/api")
    public ResourceSupport index() {
        var index = new ResourceSupport();
        index.add(linkTo(EventController.class).withRel("events"));
        return index;
    }

}
