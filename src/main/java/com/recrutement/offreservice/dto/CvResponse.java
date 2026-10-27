
package com.recrutement.offreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvResponse {

    private Long id;
    private Boolean active;
    private Boolean visible;
}
