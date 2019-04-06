package me.jaehyun.restapi.events;

import me.jaehyun.restapi.accounts.Account;
import me.jaehyun.restapi.accounts.AccountRepository;
import me.jaehyun.restapi.accounts.AccountRole;
import me.jaehyun.restapi.accounts.AccountService;
import me.jaehyun.restapi.common.AppProperties;
import me.jaehyun.restapi.common.BaseControllerTest;
import me.jaehyun.restapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp() {
        this.accountRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        relaxedResponseFields(
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

    private String getBearerToken() throws Exception {
        return "Bearer " +  getAccessToken();
    }

    private String getAccessToken() throws Exception {
        //Given
        Account jaehyun = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.accountService.saveAccount(jaehyun);

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"))
                .andDo(print());
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("content[0].objectName").exists())
                    .andExpect(jsonPath("content[0].defaultMessage").exists())
                    .andExpect(jsonPath("content[0].code").exists())
                    .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("page").exists())
                    .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("query-events"));
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").exists())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("get-an-event"));
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // Given

        // When & Then
        this.mockMvc.perform(get("/api/events/12312")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                    .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        String eventName = "Updated Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(eventName))
                    .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @TestDescription("입력값이 비어 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않은 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/11232")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 03, 16, 15, 16))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 03, 17, 15, 15))
                .beginEventDateTime(LocalDateTime.of(2019, 03, 18, 15, 15))
                .endEventDateTime(LocalDateTime.of(2019, 03, 19, 15, 15))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("우리집")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }
}
