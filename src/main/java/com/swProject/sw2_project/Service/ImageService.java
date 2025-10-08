package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Entity.Image;
import com.swProject.sw2_project.Repository.ImageRepository;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.user}")
    private String ftpUser;
    @Value("${ftp.pass}")
    private String ftpPass;
    @Value("${ftp.base-dir}")
    private String baseDir;

    private final ImageRepository imageRepository;
    private final CmmnUserRepository userRepository;

    public ImageService(ImageRepository imageRepository, CmmnUserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public Image uploadProfileImage(MultipartFile file, String userId) throws IOException {
        FTPClient ftp = new FTPClient();
        try (InputStream is = file.getInputStream()) {
            ftp.connect(host, port);
            ftp.login(ftpUser, ftpPass);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }

            String fileName;
            do {
                fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            } while (imageRepository.existsBySavedFilename(fileName));

            String remotePath = baseDir + "/" + fileName;

            boolean done = ftp.storeFile(remotePath, is);
            ftp.logout();
            if (!done) throw new IOException("FTP 업로드 실패");

            // 사용자 조회
            CmmnUser persistentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

            Image image = new Image();
            image.setUser(persistentUser);
            image.setOriginalFilename(originalName);
            image.setSavedFilename(fileName);
            image.setFileSize(file.getSize());
            image.setFileExtension(ext);

            imageRepository.save(image);

            // 프로필 이미지 id 저장
            persistentUser.setProfileImage(image);
            userRepository.save(persistentUser);

            return image;
        } finally {
            if (ftp.isConnected()) ftp.disconnect();
        }
    }
}
