package com.java.sahil.minio.service.impl;

import com.java.sahil.minio.dto.PageableResponseDto;
import com.java.sahil.minio.dto.UserRequestDto;
import com.java.sahil.minio.dto.UserResponseDto;
import com.java.sahil.minio.entity.User;
import com.java.sahil.minio.error.EntityNotFoundException;
import com.java.sahil.minio.error.FileCantUploadException;
import com.java.sahil.minio.mapper.UserMapper;
import com.java.sahil.minio.repo.UserRepo;
import com.java.sahil.minio.service.UserService;
import com.java.sahil.minio.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.java.sahil.minio.client.DummyClient.getBase64Contract;
import static com.java.sahil.minio.client.DummyClient.getBase64Signature;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final FileServiceImpl fileServiceImpl;
    private final UserMapper userMapper;
    private final FileUtil fileUtil;
    @Value("${minio.image-folder}")
    private String imageFolder;
    @Value("${minio.video-folder}")
    private String videoFolder;
    @Value("${minio.resume-folder}")
    private String resumeFolder;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userRequestDto) {
        log.info("create User started with: {}", kv("userRequestDto", userRequestDto));
        User user = userRepo.save(userMapper.toUserEntity(userRequestDto));
        UserResponseDto userResponseDto = userMapper.toUserDto(user);
        log.info("create User completed successfully with: {}", kv("userRequestDto", userRequestDto));
        return userResponseDto;
    }

    @Override
    @Transactional
    public UserResponseDto update(UserRequestDto userRequestDto, Long id) {
        log.info("update User started with: {}, {}", kv("id", id),
                kv("userRequestDto", userRequestDto));
        User user = userRepo.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException(User.class, id);
        });
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        User saved = userRepo.save(user);
        UserResponseDto userResponseDto = userMapper.toUserDto(saved);
        log.info("update User completed successfully with: {}, {}", kv("id", id),
                kv("userRequestDto", userRequestDto));
        return userResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        log.info("findById User started with: {}", kv("id", id));
        User user = userRepo.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException(User.class, id);
        });
        UserResponseDto userResponseDto = userMapper.toUserDto(user);
        log.info("findById User completed successfully with: {}", kv("id", id));
        return userResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponseDto<List<UserResponseDto>> findAll(int page, int size) {
        log.info("findAll User started");
        Pageable pageable = PageRequest.of(page, size);
        List<User> userList = userRepo.findAll(pageable).getContent();
        log.info("findAll User completed successfully");
        return new PageableResponseDto<>(userMapper.toUserDtoList(userList),
                userRepo.count(), userList.size());
    }

    @Override
    @Transactional
    public UserResponseDto delete(Long id) {
        log.info("delete User started with: {}", kv("id", id));
        User user = userRepo.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException(User.class, id);
        });
        if (user.getPhoto() != null) {
            deleteFile(user.getPhoto(), imageFolder);
        }
        if (user.getVideo() != null) {
            deleteFile(user.getVideo(), videoFolder);
        }
        userRepo.delete(user);
        UserResponseDto userResponseDto = userMapper.toUserDto(user);
        log.info("delete User completed successfully with: {}", kv("id", id));
        return userResponseDto;
    }

    /**
     * FILE METHODS
     */

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public String uploadImage(MultipartFile file, Long id) {
        log.info("uploadImage to User started with, {}",
                kv("partnerId", id));
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
//        definition.setIsolationLevel(TransactionDefinition.PROPAGATION_REQUIRED);
        definition.setTimeout(3);
        TransactionStatus status = transactionManager.getTransaction(definition);
        String fileName = "";
        try {
            User user = getUser(id);
            if (user.getPhoto() == null) {
                fileName = fileServiceImpl.uploadImage(file, imageFolder, true);
                user.setPhoto(fileName);
                userRepo.save(user);
//                if (!fileName.equals("")) throw new RuntimeException("AHAAAAAAAAAAAAAAAAAAA");
                transactionManager.commit(status);
                log.info("uploadImage to User completed successfully with {}",
                        kv("partnerId", id));
                return fileName;
            }
        } catch (Exception e) {
            log.info("Filename:  ", fileName);
            fileServiceImpl.deleteFile(fileName, imageFolder);
            transactionManager.rollback(status);
        }
        throw new FileCantUploadException(file.getOriginalFilename());
    }

    @Override
    @Transactional
    public String updateImage(MultipartFile file, Long id) {
        log.info("updateImage to User started with, {}",
                kv("partnerId", id));
        User user = getUser(id);
        deleteFile(user.getPhoto(), imageFolder);
        String fileName = fileServiceImpl.uploadImage(file, imageFolder, true);
        user.setPhoto(fileName);
        userRepo.save(user);
        log.info("updateImage to User completed successfully with {}",
                kv("partnerId", user));
        return fileName;
    }

    @Override
    @Transactional
    public void deleteUserImage(Long id) {
        log.info("deleteUserImage started from User with {}", kv("id", id));
        User user = getUser(id);
        if (user.getPhoto() != null) {
            fileServiceImpl.deleteFile(user.getPhoto(), imageFolder);
            user.setPhoto(null);
            userRepo.save(user);
        }
        log.info("deleteUserImage completed successfully from User with {} ", kv("id", id));
    }

    @Override
    public void deleteFile(String fileName, String folder) {
        log.info("deleteFile started from User with {}", kv("fileName", fileName));
        fileServiceImpl.deleteFile(fileName, folder);
        log.info("deleteFile completed successfully from User with {} ", kv("fileName", fileName));
    }

    @Override
    public byte[] getFile(String fileName, String folder) {
        log.info("getFile started with {}", kv("fileName", fileName));
        return fileServiceImpl.getFile(fileName, folder);
    }

    @Override
    @Transactional
    public String uploadVideo(MultipartFile file, Long id) {
        log.info("uploadVideo to User started with, {}",
                kv("partnerId", id));
        User user = getUser(id);
        if (user.getVideo() == null) {
            String fileName = fileServiceImpl.uploadVideo(file, videoFolder);
            user.setVideo(fileName);
            userRepo.save(user);
            log.info("uploadFile to User completed successfully with {}",
                    kv("partnerId", id));
            return fileName;
        }
        throw new FileCantUploadException(file.getOriginalFilename());
    }

    @Override
    @Transactional
    public String updateVideo(MultipartFile file, Long id) {
        log.info("updateVideo to User started with, {}",
                kv("partnerId", id));
        User user = getUser(id);
        deleteFile(user.getVideo(), videoFolder);
        String fileName = fileServiceImpl.uploadVideo(file, videoFolder);
        user.setVideo(fileName);
        userRepo.save(user);
        log.info("updateVideo to User completed successfully with {}",
                kv("partnerId", user));
        return fileName;
    }

    @Override
    @Transactional
    public void deleteUserVideo(Long id) {
        log.info("deleteUserVideo started from User with {}", kv("id", id));
        User user = getUser(id);
        if (user.getPhoto() != null) {
            fileServiceImpl.deleteFile(user.getVideo(), videoFolder);
            user.setVideo(null);
            userRepo.save(user);
        }
        log.info("deleteUserVideo completed successfully from User with {} ", kv("id", id));
    }

    @SneakyThrows
    @Override
    @Transactional
    public String uploadContractByPin(String dummyPin) {
        User user = getUser(1L);
        BufferedImage contract = base64ToBufferedImage(getBase64Contract());
        BufferedImage signature = base64ToBufferedImage(getBase64Signature());
        signature = fileUtil.resizeImage(signature, 70, 25);

        BufferedImage combinedImage = new BufferedImage(contract.getWidth(), contract.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        g.drawImage(contract, 0, 0, null);
        g.drawImage(signature, 90,
                (contract.getHeight() - 40 - signature.getHeight()), null);
        g.setPaint(Color.BLACK);
        g.setFont(new Font("Serif", Font.ITALIC, 14));
        g.drawString("Sahil Appayev", 60, 82);
        g.dispose();

        log.info("Contract size: {} {}", combinedImage.getWidth(), combinedImage.getHeight());
        log.info("Signature size: {} {}", signature.getWidth(), signature.getHeight());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(combinedImage, "PNG", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        InputStream file = new ByteArrayInputStream(bytes);
        String fileName = fileServiceImpl.uploadInputStreamImage(file, imageFolder);
        user.setPhoto(fileName);
        userRepo.save(user);
        return fileName;
    }

    @SneakyThrows
    private BufferedImage base64ToBufferedImage(String base64) {
        base64 = base64.split("[,]")[1];
        byte[] bytes = Base64.decodeBase64(base64);
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    @Transactional
    public String uploadResume(MultipartFile file, Long id) {
        log.info("uploadResume to User started with, {}",
                kv("id", id));
        User user = getUser(id);
        if (user.getVideo() == null) {
            String fileName = fileServiceImpl.uploadPdf(file, resumeFolder);
            user.setResume(fileName);
            userRepo.save(user);
            log.info("uploadResume to User completed successfully with {}",
                    kv("id", id));
            return fileName;
        }
        throw new FileCantUploadException(file.getOriginalFilename());
    }

    @Override
    @Transactional
    public String updateResume(MultipartFile file, Long id) {
        log.info("updateResume to User started with, {}",
                kv("id", id));
        User user = getUser(id);
        deleteFile(user.getResume(), resumeFolder);
        String fileName = fileServiceImpl.uploadPdf(file, resumeFolder);
        user.setResume(fileName);
        userRepo.save(user);
        log.info("updateResume to User completed successfully with {}",
                kv("id", user));
        return fileName;
    }

    private User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(User.class, id));
    }

    @Override
    @Transactional
    public Map<String, String> uploadPhotos(Long id, String[] names, MultipartFile[] files) {
        User user = getUser(id);
        int i = 0;
        for (MultipartFile file : files) {
            String uploadImageName = fileServiceImpl.uploadImage(file, imageFolder, false);
            user.getPhotos().put(names[i], uploadImageName);
            i++;
        }
        return user.getPhotos();
    }

}
