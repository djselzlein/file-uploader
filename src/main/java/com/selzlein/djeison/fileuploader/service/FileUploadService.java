package com.selzlein.djeison.fileuploader.service;

import com.selzlein.djeison.fileuploader.domain.FileUpload;
import com.selzlein.djeison.fileuploader.repository.FileUploadRepository;
import com.selzlein.djeison.fileuploader.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
/**
 * Service Implementation for managing FileUpload.
 */
@Service
@Transactional
public class FileUploadService {

    private final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private FileUploadRepository fileUploadRepository;
    @Autowired
    private HttpServletRequest request;

    /**
     * Save a fileUpload.
     *
     * @param fileUpload the entity to save
     * @return the persisted entity
     */
    public FileUpload save(FileUpload fileUpload) {
        log.debug("Request to save FileUpload : {}", fileUpload);        return fileUploadRepository.save(fileUpload);
    }

    /**
     * Get all the fileUploads.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<FileUpload> findAll(Pageable pageable) {
        log.debug("Request to get all FileUploads");
        return fileUploadRepository.findAll(pageable);
    }


    /**
     * Get one fileUpload by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<FileUpload> findOne(Long id) {
        log.debug("Request to get FileUpload : {}", id);
        return fileUploadRepository.findById(id);
    }

    /**
     * Delete the fileUpload by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete FileUpload : {}", id);
        fileUploadRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FileUpload upload(Long id, MultipartFile file) throws IOException {
        File uploaded = new File(file.getOriginalFilename());
        if (uploaded.exists()) {
            uploaded.delete();
        }
        uploaded.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(uploaded);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();

        return updateFilePath(id, uploaded.getAbsolutePath());
    }

    private FileUpload updateFilePath(Long id, String path) {
        Optional<FileUpload> fileUploadOptional = findOne(id);
        if (fileUploadOptional.isPresent()) {
            FileUpload fileUpload = fileUploadOptional.get();
            fileUpload.setPath(path);
            return save(fileUpload);
        } else {
            throw new BadRequestAlertException("Invalid id", "fileUpload", "idnull");
        }
    }
}
