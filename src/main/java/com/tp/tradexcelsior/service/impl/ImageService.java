package com.tp.tradexcelsior.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.tp.tradexcelsior.dto.response.ImageDto;
import com.tp.tradexcelsior.exception.custom.ImageNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImageService {

  @Autowired
  private GridFSBucket gridFSBucket;

  private static final Map<String, String> EXTENSION_TO_MIME_TYPE = new HashMap<>();

  static {
    EXTENSION_TO_MIME_TYPE.put("jpg", "image/jpeg");
    EXTENSION_TO_MIME_TYPE.put("jpeg", "image/jpeg");
    EXTENSION_TO_MIME_TYPE.put("png", "image/png");
    EXTENSION_TO_MIME_TYPE.put("gif", "image/gif");
    EXTENSION_TO_MIME_TYPE.put("bmp", "image/bmp");
    EXTENSION_TO_MIME_TYPE.put("tiff", "image/tiff");
    EXTENSION_TO_MIME_TYPE.put("webp", "image/webp");
  }


  // Helper method to get file extension
  private String getFileExtension(String fileName) {
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
      return fileName.substring(dotIndex + 1).toLowerCase();
    }
    return ""; // No extension found
  }

  public byte[] getImageByName(String imageName) throws RuntimeException {
    // Check if imageName has an extension
    String fileExtension = getFileExtension(imageName);
    Optional<GridFSFile> imageFileOpt;

    if (fileExtension.isEmpty()) {
      imageFileOpt = Optional.ofNullable(gridFSBucket.find(Filters.regex("filename", "^" + imageName + ".*$", "i")).first());
    } else {
      imageFileOpt = Optional.ofNullable(gridFSBucket.find(Filters.eq("filename", imageName)).first());
    }

    // If the image file is found, read and return it
    return imageFileOpt
        .map(gridFSFile -> {
          try (InputStream inputStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId())) {
            return inputStream.readAllBytes();
          } catch (IOException e) {
            throw new RuntimeException("Error reading image from InputStream", e);
          }
        })
        .orElseThrow(() -> new ImageNotFoundException("Image not found with name: " + imageName));
  }

  // Fetch image by its MongoDB ObjectId
  public byte[] getImageById(String imageId) throws RuntimeException {
    // Parse the imageId string to an ObjectId
    ObjectId objectId = new ObjectId(imageId);

    // Find the image file by its ObjectId
    Optional<GridFSFile> imageFileOpt = Optional.ofNullable(gridFSBucket.find(Filters.eq("_id", objectId)).first());

    return imageFileOpt
        .map(gridFSFile -> {
          try (InputStream inputStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId())) {
            return inputStream.readAllBytes();
          } catch (IOException e) {
            throw new RuntimeException("Error reading image from InputStream", e);
          }
        })
        .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
  }


  // Method to retrieve content type dynamically from the image metadata
  public String getImageContentType(String imageName) {
    // Fetch the image metadata to get the content type
    Optional<GridFSFile> imageFile = Optional.ofNullable(gridFSBucket.find(
        Filters.eq("filename", imageName)).first());

    if (imageFile.isEmpty()) {
      throw new ImageNotFoundException("Image not found with name: " + imageName);
    }

    // Retrieve the content type from the metadata (if available)
    GridFSFile gridFSFile = imageFile.get();
    Document metadata = gridFSFile.getMetadata();
    String contentType = (metadata != null && metadata.containsKey("contentType"))
        ? metadata.getString("contentType")
        : "application/octet-stream";  // Default content type if not found

    return contentType;
  }

  // Method to retrieve content type dynamically from the image metadata by its ObjectId
  public String getImageContentTypeById(String imageId) {
    // Convert the imageId (String) to ObjectId
    ObjectId objectId = new ObjectId(imageId);

    // Fetch the image metadata using its ObjectId to get the content type
    Optional<GridFSFile> imageFile = Optional.ofNullable(gridFSBucket.find(
        Filters.eq("_id", objectId)).first());

    if (imageFile.isEmpty()) {
      throw new ImageNotFoundException("Image not found with ID: " + imageId);
    }

    // Retrieve the content type from the metadata (if available)
    GridFSFile gridFSFile = imageFile.get();
    Document metadata = gridFSFile.getMetadata();
    String contentType = (metadata != null && metadata.containsKey("contentType"))
        ? metadata.getString("contentType")
        : "application/octet-stream";  // Default content type if not found

    return contentType;
  }

  // Delete an image by its filename
  public void deleteImageByName(String imageName) {
    // Check if the image exists
    Optional<GridFSFile> imageFileOpt = Optional.ofNullable(gridFSBucket.find(Filters.eq("filename", imageName)).first());

    if (imageFileOpt.isEmpty()) {
      throw new ImageNotFoundException("Image with the name '" + imageName + "' does not exist.");
    }

    // Image exists, delete it
    GridFSFile gridFSFile = imageFileOpt.get();
    gridFSBucket.delete(gridFSFile.getObjectId());
  }

  // Delete an image by its ObjectId (imageId)
  public void deleteImageById(String imageId) {
    // Convert the imageId (String) to ObjectId
    ObjectId objectId = new ObjectId(imageId);

    // Check if the image exists by its ObjectId
    Optional<GridFSFile> imageFileOpt = Optional.ofNullable(gridFSBucket.find(Filters.eq("_id", objectId)).first());

    if (imageFileOpt.isEmpty()) {
      throw new ImageNotFoundException("Image with the ID '" + imageId + "' does not exist.");
    }

    // Image exists, delete it
    GridFSFile gridFSFile = imageFileOpt.get();
    gridFSBucket.delete(gridFSFile.getObjectId());
  }


  public List<ImageDto> getAllImages() {
    return gridFSBucket.find(Filters.empty())
        .map(gridFSFile -> {
          // Get metadata safely, ensuring it's not null
          Document metadata = gridFSFile.getMetadata();

          // Check if metadata is null or doesn't contain "contentType" key
          String contentType = (metadata != null && metadata.containsKey("contentType"))
              ? metadata.getString("contentType")
              : "application/octet-stream";  // Default content type

          // Get the image's ObjectId (imageId)
          String imageId = gridFSFile.getObjectId().toString();  // Convert ObjectId to String

          // Return new ImageDto with imageId, filename, and content type
          return new ImageDto(imageId, gridFSFile.getFilename(), contentType);
        })
        .into(new ArrayList<>());
  }


  //  Image with same name allowed and returns name and id of image
  public Map<String, String> uploadImageWithCustomName(MultipartFile file, String customFileName) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("File cannot be null or empty.");
    }

    if (customFileName == null || customFileName.isEmpty()) {
      String fileName = file.getOriginalFilename();
      customFileName = fileName.substring(0, fileName.lastIndexOf('.'));
    }

    String originalFileName = file.getOriginalFilename();
    String fileExtension = getFileExtension(originalFileName);
    if (fileExtension.isEmpty()) {
      throw new IllegalArgumentException("File must have an extension.");
    }

    // Combine custom name with the original file extension
    String finalFileName = customFileName + "." + fileExtension;

    // Get content type based on file extension
    String contentType = EXTENSION_TO_MIME_TYPE.getOrDefault(fileExtension, "application/octet-stream");

    // Prepare upload options with content type
    GridFSUploadOptions options = new GridFSUploadOptions()
        .metadata(new Document("contentType", contentType));

    // Save the image and get the ObjectId (which is inherently unique)
    ObjectId imageId;
    try (InputStream inputStream = file.getInputStream()) {
      imageId = gridFSBucket.uploadFromStream(finalFileName, inputStream, options);
      log.info("Image uploaded successfully: " + finalFileName);
    }

    // Prepare and return the response with image name and id
    Map<String, String> response = new HashMap<>();
    response.put("imageName", finalFileName);  // Custom name with extension
    response.put("imageId", imageId.toString());  // Unique image ID

    return response;
  }

}