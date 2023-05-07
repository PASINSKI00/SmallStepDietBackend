package com.pasinski.sl.backend.image;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Base64EncodedMultipartFile implements MultipartFile {
    private final byte[] fileContent;
    private final String originalFileName;

    public Base64EncodedMultipartFile(byte[] fileContent, String originalFileName) {
        this.fileContent = fileContent;
        this.originalFileName = originalFileName;
    }

    @Override
    public String getName() {
        return this.originalFileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFileName;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return this.fileContent == null || this.fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return this.fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(this.fileContent), dest);
    }
}
