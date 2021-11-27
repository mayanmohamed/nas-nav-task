package nas.nav.task.DTO;

import lombok.Data;
import nas.nav.task.models.Category;

@Data
public class AdminUploadDTO {
    int width;
    int height;
    Category category;
    byte[] attachment;
    String description;
}
