package org.example.backend.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeRule {
    private String id;
    private String categoryId;     // مثلاً id مربوط به "لپ‌تاپ"

    private String attributeName;  // مثلاً "RAM"

    @JsonProperty("isRequired") // 👈 به جکسون می‌گوید این فیلد در JSON با نام isRequired خوانده و نوشته شود
    private boolean required;
}
