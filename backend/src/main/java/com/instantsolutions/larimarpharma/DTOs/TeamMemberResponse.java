package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponse {
    private Long id;
    private String name;
    private String market;
    private String headquarters;
    private String email;
    private Integer todayVisitCount;
    private Integer targetProgress;
    private List<VisitResponse> visits;
}