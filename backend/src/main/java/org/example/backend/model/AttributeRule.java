package org.example.backend.model;


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
    private boolean isRequired;    // آیا کاربر حتماً باید این را وارد کند؟
}
