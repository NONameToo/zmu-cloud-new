package com.zmu.cloud.demos.producer;

import com.zmu.cloud.demos.producer.rest.HelloController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;

public class MvcMockTest {

    @Before
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(new HelloController());
    }

    @Test
    public void testMethod() {
    }
}