package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.response.ImageDto;
import com.tp.tradexcelsior.exception.custom.ImageNotFoundException;
import com.tp.tradexcelsior.service.impl.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Image Management", description = "APIs for managing images")
@RestController
@RequestMapping("/images")
public class ImageController {

  @Autowired
  private ImageService imageService;

  // Endpoint to upload the image and return its name
  @Operation(summary = "Upload an image", description = "Upload an image file to the server and return the image filename.")
  @PostMapping
  public ResponseEntity<Map<String, String>> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "name", required = false) String customFileName) {
    try {
      // Call the service method to upload the image
      Map<String, String> result = imageService.uploadImageWithCustomName(file, customFileName);
      return ResponseEntity.ok(result); // Return image name and ID as response
    } catch (IOException | IllegalArgumentException e) {
      // Handle exceptions and return an appropriate error response
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  // Endpoint to fetch an image by its MongoDB ObjectId
  @Operation(summary = "Get an image by imageId", description = "Get the image by its imageId from the server.")
  @GetMapping("/id/{imageId}")
  public ResponseEntity<byte[]> getImageById(@PathVariable String imageId) {
    try {
      // Fetch the image bytes using the service method
      byte[] imageBytes = imageService.getImageById(imageId);

      String contentType;
      contentType = imageService.getImageContentTypeById(imageId);

      if(contentType.isEmpty()){
        contentType = "image/jpeg";
      }

      // Return the image in the response with correct headers for display in browser
      return ResponseEntity
          .status(HttpStatus.OK)
          .header(HttpHeaders.CONTENT_TYPE, contentType)
          .body(imageBytes);
    } catch (RuntimeException e) {
      // Handle image not found or other runtime exceptions
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  // Endpoint to retrieve the image by filename
  @Operation(summary = "Get an image by filename", description = "Retrieve the image by its filename.")
  @GetMapping("/{imageName}")
  public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
    byte[] imageBytes = imageService.getImageByName(imageName);
    String contentType = imageService.getImageContentType(imageName);
    return ResponseEntity.ok()
        .header("Content-Type", contentType) // Assuming JPEG; you can set dynamically
        .header("Content-Length", String.valueOf(imageBytes.length))
        .body(imageBytes); // Return the image as byte array in the response body
  }


  // Endpoint to retrieve all images
  @Operation(summary = "Get all images", description = "Fetch a list of all images stored in the system with their filenames and content types.")
  @GetMapping
  public ResponseEntity<List<ImageDto>> getAllImages() {
    List<ImageDto> imageDtos = imageService.getAllImages();
    return ResponseEntity.ok(imageDtos);
  }

  @Operation(summary = "Delete an image by filename", description = "Delete the image by its filename from the server.")
  @DeleteMapping("/{imageName}")
  public ResponseEntity<String> deleteImage(@PathVariable String imageName) {
    try {
      imageService.deleteImageByName(imageName); // No content response will be triggered here
      return ResponseEntity.noContent().build(); // Return 204 No Content response
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body("Image not found with name: " + imageName);
    }
  }


  // Endpoint to delete an image by its MongoDB ObjectId
  @Operation(summary = "Delete an image by imageId", description = "Delete the image by its imageId from the server.")
  @DeleteMapping("/id/{imageId}")
  public ResponseEntity<String> deleteImageById(@PathVariable String imageId) {
    try {
      // Call the service method to delete the image by its ID
      imageService.deleteImageById(imageId);

      // Return a success message
      return ResponseEntity
          .status(HttpStatus.NO_CONTENT) // 204 No Content
          .body("Image with ID " + imageId + " has been deleted successfully.");
    } catch (ImageNotFoundException e) {
      // If the image is not found, return 404 Not Found
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image with ID " + imageId + " not found.");
    } catch (RuntimeException e) {
      // Handle other runtime exceptions
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the image.");
    }
  }

}
