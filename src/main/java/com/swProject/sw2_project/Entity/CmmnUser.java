package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CmmnUser {
    @Id
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    private String userNm;

    @Column(nullable = false)
    private String userEmail;

    private String regDt;
    private String chgDt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private CmmnUserLogin cmmnUserLogin;

    @Version
    private Long version;
}
