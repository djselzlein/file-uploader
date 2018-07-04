package com.selzlein.djeison.fileuploader.web.rest;

import com.selzlein.djeison.fileuploader.FileUploaderApp;

import com.selzlein.djeison.fileuploader.domain.FileUpload;
import com.selzlein.djeison.fileuploader.repository.FileUploadRepository;
import com.selzlein.djeison.fileuploader.service.FileUploadService;
import com.selzlein.djeison.fileuploader.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static com.selzlein.djeison.fileuploader.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FileUploadResource REST controller.
 *
 * @see FileUploadResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileUploaderApp.class)
public class FileUploadResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    @Autowired
    private FileUploadRepository fileUploadRepository;

    

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFileUploadMockMvc;

    private FileUpload fileUpload;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FileUploadResource fileUploadResource = new FileUploadResource(fileUploadService);
        this.restFileUploadMockMvc = MockMvcBuilders.standaloneSetup(fileUploadResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileUpload createEntity(EntityManager em) {
        FileUpload fileUpload = new FileUpload()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .creationDate(DEFAULT_CREATION_DATE)
            .path(DEFAULT_PATH);
        return fileUpload;
    }

    @Before
    public void initTest() {
        fileUpload = createEntity(em);
    }

    @Test
    @Transactional
    public void createFileUpload() throws Exception {
        int databaseSizeBeforeCreate = fileUploadRepository.findAll().size();

        // Create the FileUpload
        restFileUploadMockMvc.perform(post("/api/file-uploads")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
            .andExpect(status().isCreated());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeCreate + 1);
        FileUpload testFileUpload = fileUploadList.get(fileUploadList.size() - 1);
        assertThat(testFileUpload.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFileUpload.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFileUpload.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testFileUpload.getPath()).isEqualTo(DEFAULT_PATH);
    }

    @Test
    @Transactional
    public void createFileUploadWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileUploadRepository.findAll().size();

        // Create the FileUpload with an existing ID
        fileUpload.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileUploadMockMvc.perform(post("/api/file-uploads")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
            .andExpect(status().isBadRequest());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileUploadRepository.findAll().size();
        // set the field null
        fileUpload.setTitle(null);

        // Create the FileUpload, which fails.

        restFileUploadMockMvc.perform(post("/api/file-uploads")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
            .andExpect(status().isBadRequest());

        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFileUploads() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);

        // Get all the fileUploadList
        restFileUploadMockMvc.perform(get("/api/file-uploads?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())));
    }
    

    @Test
    @Transactional
    public void getFileUpload() throws Exception {
        // Initialize the database
        fileUploadRepository.saveAndFlush(fileUpload);

        // Get the fileUpload
        restFileUploadMockMvc.perform(get("/api/file-uploads/{id}", fileUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fileUpload.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingFileUpload() throws Exception {
        // Get the fileUpload
        restFileUploadMockMvc.perform(get("/api/file-uploads/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileUpload() throws Exception {
        // Initialize the database
        fileUploadService.save(fileUpload);

        int databaseSizeBeforeUpdate = fileUploadRepository.findAll().size();

        // Update the fileUpload
        FileUpload updatedFileUpload = fileUploadRepository.findById(fileUpload.getId()).get();
        // Disconnect from session so that the updates on updatedFileUpload are not directly saved in db
        em.detach(updatedFileUpload);
        updatedFileUpload
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .creationDate(UPDATED_CREATION_DATE)
            .path(UPDATED_PATH);

        restFileUploadMockMvc.perform(put("/api/file-uploads")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFileUpload)))
            .andExpect(status().isOk());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeUpdate);
        FileUpload testFileUpload = fileUploadList.get(fileUploadList.size() - 1);
        assertThat(testFileUpload.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFileUpload.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFileUpload.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testFileUpload.getPath()).isEqualTo(UPDATED_PATH);
    }

    @Test
    @Transactional
    public void updateNonExistingFileUpload() throws Exception {
        int databaseSizeBeforeUpdate = fileUploadRepository.findAll().size();

        // Create the FileUpload

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFileUploadMockMvc.perform(put("/api/file-uploads")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileUpload)))
            .andExpect(status().isBadRequest());

        // Validate the FileUpload in the database
        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFileUpload() throws Exception {
        // Initialize the database
        fileUploadService.save(fileUpload);

        int databaseSizeBeforeDelete = fileUploadRepository.findAll().size();

        // Get the fileUpload
        restFileUploadMockMvc.perform(delete("/api/file-uploads/{id}", fileUpload.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<FileUpload> fileUploadList = fileUploadRepository.findAll();
        assertThat(fileUploadList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileUpload.class);
        FileUpload fileUpload1 = new FileUpload();
        fileUpload1.setId(1L);
        FileUpload fileUpload2 = new FileUpload();
        fileUpload2.setId(fileUpload1.getId());
        assertThat(fileUpload1).isEqualTo(fileUpload2);
        fileUpload2.setId(2L);
        assertThat(fileUpload1).isNotEqualTo(fileUpload2);
        fileUpload1.setId(null);
        assertThat(fileUpload1).isNotEqualTo(fileUpload2);
    }
}
