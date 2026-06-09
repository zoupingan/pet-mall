package com.anan.petadmin.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data

public class UserVO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private Integer role;

    private Integer status;

    private String token;
}
