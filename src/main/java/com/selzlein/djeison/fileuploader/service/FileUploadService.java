package com.selzlein.djeison.fileuploader.service;

import com.selzlein.djeison.fileuploader.domain.FileUpload;
import com.selzlein.djeison.fileuploader.repository.FileUploadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
/**
 * Service Implementation for managing FileUpload.
 */
@Service
@Transactional
public class FileUploadService {

    private final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    private final FileUploadRepository fileUploadRepository;

    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

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
}
