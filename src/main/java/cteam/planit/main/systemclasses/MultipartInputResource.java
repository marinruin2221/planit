package com.example.demo.systemclasses;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public class MultipartInputResource extends ByteArrayResource {
  private final String filename;

  public MultipartInputResource(MultipartFile file) throws IOException {
    super(file.getBytes());
    this.filename = file.getOriginalFilename();
  }

  @Override
  public String getFilename() {
    return filename;
  }
}
