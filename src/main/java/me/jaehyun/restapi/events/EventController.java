package me.jaehyun.restapi.events;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : 진실
 * github: https://github.com/jaehyun8719
 * email: jaehyun8719@gmail.com
 * <p>
 * Date: 2019-03-16
 * Description:
 * Copyright(©) 2019 by jaehyun.
 **/
@RestController
@RequestMapping(path = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping()
    public ResponseEntity createEvent(@RequestBody Event event) {
        Event newEvent = this.eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        event.setId(10);
        return ResponseEntity.created(createUri).body(event);
    }
}
