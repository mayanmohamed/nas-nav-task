package nas.nav.task.DTO;

import lombok.Data;
import nas.nav.task.models.Category;

@Data
public class UserUploadDTO {


    Category category;
    byte[] attachment;
    String description;
}
