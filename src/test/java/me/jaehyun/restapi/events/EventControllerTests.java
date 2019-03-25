package me.jaehyun.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jaehyun.restapi.common.RestDocsConfiguration;
import me.jaehyun.restapi.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 이벤트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
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

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self")
                                , linkWithRel("query-events").description("link to query events")
                                , linkWithRel("update-event").description("link to update an existing event")
                                , linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("header accept")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("Description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("basePrice of new event")
                                , fieldWithPath("maxPrice").description("maxPrice of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event")
                                ,fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("Description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("basePrice of new event")
                                , fieldWithPath("maxPrice").description("maxPrice of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of new event")
                                , fieldWithPath("free").description("it tells if this event is free or not")
                                , fieldWithPath("offline").description("it tells if this event is offline meeting or not")
                                , fieldWithPath("eventStatus").description("eventStatus")
                                , fieldWithPath("_links.self.href").description("link to self")
                                , fieldWithPath("_links.query-events.href").description("link to query list")
                                , fieldWithPath("_links.update-event.href").description("link to self update existing event")
                                , fieldWithPath("_links.profile.href").description("link to self profile")
                        )
                ));
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEventBadRequest() throws Exception {
        Event event = Event.builder()
                .id(100)
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
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEventBadRequestEmptyInput() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEventBadRequestWrongInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Develoment with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 03, 19, 15, 16))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 03, 18, 15, 15))
                .beginEventDateTime(LocalDateTime.of(2019, 03, 17, 15, 15))
                .endEventDateTime(LocalDateTime.of(2019, 03, 16, 15, 15))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("우리집")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }
}
