package com.anan.petadmin.dto;
import lombok.Data;

import java.io.Serializable;

@Data
public class AgentDTO implements Serializable {
    private String query;

    private String sessionId;

    private Long userId;
}
