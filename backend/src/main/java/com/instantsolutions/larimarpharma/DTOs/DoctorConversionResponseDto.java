package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorConversionResponseDto {

   private Long id;
   private Long fieldExecutiveId;
   private String fieldExecutiveName;
   private Long managerId;
   private String managerName;
   private Long doctorId;
   private String doctorName;
   private String hospitalName;
   private Long productId;
   private String productName;
   private Date createdAt;

}
