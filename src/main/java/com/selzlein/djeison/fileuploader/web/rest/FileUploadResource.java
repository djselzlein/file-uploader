package com.selzlein.djeison.fileuploader.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.selzlein.djeison.fileuploader.domain.FileUpload;
import com.selzlein.djeison.fileuploader.service.FileUploadService;
import com.selzlein.djeison.fileuploader.web.rest.errors.BadRequestAlertException;
import com.selzlein.djeison.fileuploader.web.rest.util.HeaderUtil;
import com.selzlein.djeison.fileuploader.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing FileUpload.
 */
@RestController
@RequestMapping("/api")
public class FileUploadResource {

    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

    private static final String ENTITY_NAME = "fileUpload";

    private final FileUploadService fileUploadService;

    public FileUploadResource(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * POST  /file-uploads : Create a new fileUpload.
     *
     * @param fileUpload the fileUpload to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fileUpload, or with status 400 (Bad Request) if the fileUpload has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/file-uploads")
    @Timed
    public ResponseEntity<FileUpload> createFileUpload(@Valid @RequestBody FileUpload fileUpload) throws URISyntaxException {
        log.debug("REST request to save FileUpload : {}", fileUpload);
        if (fileUpload.getId() != null) {
            throw new BadRequestAlertException("A new fileUpload cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FileUpload result = fileUploadService.save(fileUpload);
        return ResponseEntity.created(new URI("/api/file-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /file-uploads : Updates an existing fileUpload.
     *
     * @param fileUpload the fileUpload to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fileUpload,
     * or with status 400 (Bad Request) if the fileUpload is not valid,
     * or with status 500 (Internal Server Error) if the fileUpload couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/file-uploads")
    @Timed
    public ResponseEntity<FileUpload> updateFileUpload(@Valid @RequestBody FileUpload fileUpload) throws URISyntaxException {
        log.debug("REST request to update FileUpload : {}", fileUpload);
        if (fileUpload.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FileUpload result = fileUploadService.save(fileUpload);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, fileUpload.getId().toString()))
            .body(result);
    }

    /**
     * GET  /file-uploads : get all the fileUploads.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of fileUploads in body
     */
    @GetMapping("/file-uploads")
    @Timed
    public ResponseEntity<List<FileUpload>> getAllFileUploads(Pageable pageable) {
        log.debug("REST request to get a page of FileUploads");
        Page<FileUpload> page = fileUploadService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/file-uploads");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /file-uploads/:id : get the "id" fileUpload.
     *
     * @param id the id of the fileUpload to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fileUpload, or with status 404 (Not Found)
     */
    @GetMapping("/file-uploads/{id}")
    @Timed
    public ResponseEntity<FileUpload> getFileUpload(@PathVariable Long id) {
        log.debug("REST request to get FileUpload : {}", id);
        Optional<FileUpload> fileUpload = fileUploadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileUpload);
    }

    /**
     * DELETE  /file-uploads/:id : delete the "id" fileUpload.
     *
     * @param id the id of the fileUpload to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/file-uploads/{id}")
    @Timed
    public ResponseEntity<Void> deleteFileUpload(@PathVariable Long id) {
        log.debug("REST request to delete FileUpload : {}", id);
        fileUploadService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
