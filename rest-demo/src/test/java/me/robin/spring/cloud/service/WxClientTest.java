package me.robin.spring.cloud.service;

import me.robin.spring.BaseTest;
import me.robin.wx.client.model.WxUser;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017-06-15.
 */
public class WxClientTest extends BaseTest {

    @Resource
    WxClient wxClient;


    @Test
    public void testApi() {

        List<WxUser> userList = wxClient.contactList("xuanlubin","all");

        System.out.println();

    }
}