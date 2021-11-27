package nas.nav.task.security.services;

import com.google.gson.Gson;
import nas.nav.task.models.Picture;
import nas.nav.task.models.Status;
import nas.nav.task.models.User;
import nas.nav.task.repository.PictureRepository;
import nas.nav.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UploadsService {

    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    ServletContext context;

    @Autowired
    public UserRepository userRepository;

    public List<Picture> findAll() {
        return pictureRepository.findAll();
    }

    public Optional<Picture> findAcceptedPicture(int id) {
        return pictureRepository.findByIdAndStatus(id , Status.ACCEPTED);
    }

    public Optional<Picture> findByUnproccessedPictures(int id) {
        return pictureRepository.findByIdAndStatus(id , Status.UNPROCESSED);
    }

    public void deleteById(int id) {
    	pictureRepository.deleteById(id);
    }

    public List<Picture> getPictureByStatus(Status status) {
    	return pictureRepository.findAllByStatus(status);
    }

    public Picture save(Picture picture) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        Optional<User> user=userRepository.findByUsername(username);
        picture.setUser(user.get());
        picture.setStatus(Status.UNPROCESSED);
        return pictureRepository.save(picture);
    }

    public void updateStatus(Integer id, String status) {
        Optional<Picture> obj = pictureRepository.findById(id);
        Gson gson = new Gson();
        LinkedHashMap<String, String> statusObj = gson.fromJson(status , LinkedHashMap.class);
        if(obj.isPresent()){
            Picture upload = obj.get();
            if(statusObj.get("status").equals("ACCEPTED")){
                try {
                    byte[] file = obj.get().getAttachment();
                    // Get the file and save it uploads dir
                    //  byte[] bytes = file.getBytes();
                    Path path =  java.nio.file.Paths.get(context.getRealPath("uploads") +obj.get().getOriginalFilename());
                    Files.write(path, file);
                    upload.setUrls(path.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                upload.setStatus(Status.ACCEPTED);
                pictureRepository.save(upload);
            }
            else if(statusObj.get("status").equals("REJECTED")){
                upload.setStatus(Status.REJECTED);
                pictureRepository.save(upload);
            }
        }
    }
}
