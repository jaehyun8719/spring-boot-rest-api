package me.jaehyun.restapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() {
        // Given
        Event event = Event.builder()
                        .basePrice(0)
                        .maxPrice(0)
                        .build();

        //When
        event.update();

        // Then
        assertThat(event.isFree()).isTrue();


        // Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();


        // Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        // Given
        Event event = Event.builder()
                .location("강남역 도곡역")
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();


        // Given
        event = Event.builder()
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse();
    }

}