package com.anan.petadmin.dto;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data

@TableName("user")
public class UserDTO implements Serializable {
    private String username;

    private String password;

}
