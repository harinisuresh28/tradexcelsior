package com.tp.tradexcelsior.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "references")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reference extends CommonEntity{

    @Id
    private String id;
    private String name;
    private String type;
    private String link;
    private String imageId;
}