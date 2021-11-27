package nas.nav.task.controllers;

import lombok.extern.slf4j.Slf4j;
import nas.nav.task.DTO.UserUploadDTO;
import nas.nav.task.models.Picture;
import nas.nav.task.models.Status;
import nas.nav.task.security.services.UploadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Autowired
    public UploadsService uploadsService;

    @GetMapping("/accepted")
    public List<String> getAcceptedPictures() {

        List<Picture> picByStatus = uploadsService.getPictureByStatus(Status.ACCEPTED);
        List<String> urls = new ArrayList<>();
        for (Picture byStatus : picByStatus) {
            String url = byStatus.getUrls();
            urls.add(url);
        }
        return urls;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Picture> fileUpload(@RequestPart("file") MultipartFile file, @RequestPart Picture picture) throws IOException {

        if (file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) || file.getContentType().equals(MediaType.IMAGE_GIF_VALUE)
                || file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            picture.setOriginalFilename(file.getOriginalFilename());
            picture.setAttachment(file.getBytes());
            return ResponseEntity.ok(uploadsService.save(picture));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{pictureId}")
    public ResponseEntity<UserUploadDTO> getUploadsById(@PathVariable int pictureId) {
        log.debug("get picture by ID {}", pictureId);
        var pictureOptional = uploadsService.findAcceptedPicture(pictureId);
        if (pictureOptional.isPresent()) {
            Picture pictureDAO = pictureOptional.get();

            UserUploadDTO userUploadDTO = new UserUploadDTO();
            userUploadDTO.setCategory(pictureDAO.getCategory());
            userUploadDTO.setDescription(pictureDAO.getDescription());
            userUploadDTO.setAttachment(pictureDAO.getAttachment());
            return ResponseEntity.ok(userUploadDTO);
        } else {
            log.debug("the picture" + " does not exist");
            return ResponseEntity.notFound().build();
        }
    }
}
