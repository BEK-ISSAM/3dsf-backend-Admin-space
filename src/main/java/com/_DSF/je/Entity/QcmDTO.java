package com._DSF.je.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class QcmDTO {
    private String question;
    private String correctAnswer;
    private Long quiz;

    private String imageName;
    private String imageType;
}
