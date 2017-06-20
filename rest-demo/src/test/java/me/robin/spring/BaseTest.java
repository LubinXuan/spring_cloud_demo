package me.robin.spring;

import me.robin.spring.cloud.RestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * Created by Administrator on 2017-06-05.
 */
@SpringBootTest(classes = RestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BaseTest extends AbstractTestNGSpringContextTests {

}