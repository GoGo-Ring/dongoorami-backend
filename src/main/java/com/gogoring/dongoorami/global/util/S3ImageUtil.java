package com.gogoring.dongoorami.global.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gogoring.dongoorami.global.exception.FailFileUploadException;
import com.gogoring.dongoorami.global.exception.GlobalErrorCode;
import com.gogoring.dongoorami.global.exception.InvalidFileExtensionException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageUtil {

    private final AmazonS3 amazonS3;

    private final List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg");

    @Value("${cloud.aws.s3.bucket}/")
    private String bucket;

    public String putObject(MultipartFile multipartFile, ImageType imageType) {
        String originalFilename = multipartFile.getOriginalFilename();
        validateFileExtension(originalFilename);
        String s3Filename = UUID.randomUUID() + "-" + originalFilename;

        try {
            amazonS3.putObject(bucket + imageType.getName(), s3Filename,
                    multipartFile.getInputStream(), createObjectMetadataWith(multipartFile));
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new FailFileUploadException(GlobalErrorCode.FAIL_FILE_UPLOAD);
        }

        return s3Filename;
    }

    public void deleteObject(String filename, ImageType imageType) {
        amazonS3.deleteObject(bucket + imageType.getName(), filename);
    }

    public String getObjectUrl(String filename, ImageType imageType) {
        return amazonS3.getUrl(bucket + imageType.getName(), filename).toString();
    }

    private void validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            throw new InvalidFileExtensionException(GlobalErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private ObjectMetadata createObjectMetadataWith(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        return objectMetadata;
    }
}