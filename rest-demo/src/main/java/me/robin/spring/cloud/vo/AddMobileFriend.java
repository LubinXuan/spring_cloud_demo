package me.robin.spring.cloud.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lubin.Xuan on 2017-10-11.
 * {desc}
 */
@Getter
@Setter
public class AddMobileFriend {
    private String[] numbers;
    private String helloMessage;
    private int interval = 50;
}
