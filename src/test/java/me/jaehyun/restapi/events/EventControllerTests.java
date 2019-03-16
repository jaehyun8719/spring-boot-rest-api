package me.jaehyun.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                        .name("Spring")
                        .description("REST API Develoment with Spring")
                        .beginEnrollmentDateTime(LocalDateTime.of(2019, 03, 16, 15, 16))
                        .closeEnrollmentDateTime(LocalDateTime.of(2019, 03, 17, 15, 15))
                        .beginEventDateTime(LocalDateTime.of(2019, 03, 18, 15, 15))
                        .endEventDateTime(LocalDateTime.of(2019, 03, 19, 15, 15))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("우리집")
                        .build();
        event.setId(10);

        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
}
