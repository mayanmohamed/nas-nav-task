package nas.nav.task.controllers;

import nas.nav.task.DTO.AdminUploadDTO;
import nas.nav.task.models.Picture;
import nas.nav.task.models.Status;
import nas.nav.task.security.services.UploadsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/admin/uploads")
@Slf4j
public class AdminUploadsController {

    @Autowired
    public UploadsService uploadsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unprocessed")
    public List<byte[]> getAcceptedPictures() {
        List<Picture> unproccessed = uploadsService.getPictureByStatus(Status.UNPROCESSED);
        List<byte[]> attachments = new ArrayList<>();
        for (Picture picture : unproccessed) {
            attachments.add(picture.getAttachment());
        }
        return attachments;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{pictureId}")
    public ResponseEntity<AdminUploadDTO> getUploadById(@PathVariable int pictureId) {

        log.debug("get picture by ID {}", pictureId);
        var pictureOptional = uploadsService.findByUnproccessedPictures(pictureId);
        if (pictureOptional.isPresent()) {
            Picture pictureDAO = pictureOptional.get();
            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(pictureOptional.get().getAttachment()));
            } catch (IOException e) {
                return  ResponseEntity.badRequest().build();
            }
            AdminUploadDTO adminUploadDTO = new AdminUploadDTO();
            adminUploadDTO.setDescription(pictureDAO.getDescription());
            adminUploadDTO.setCategory(pictureDAO.getCategory());
            adminUploadDTO.setHeight(image.getHeight());
            adminUploadDTO.setWidth(image.getWidth());
            adminUploadDTO.setAttachment(pictureDAO.getAttachment());

            return ResponseEntity.ok(adminUploadDTO);
        } else {
            log.debug("the picture" + " does not exist");
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{pictureId}")
    public void updateStatus(@PathVariable int pictureId , @RequestBody String status) {
        uploadsService.findByUnproccessedPictures(pictureId).ifPresent(pictureObj -> uploadsService.updateStatus(pictureObj.getId() , status));
    }

}
