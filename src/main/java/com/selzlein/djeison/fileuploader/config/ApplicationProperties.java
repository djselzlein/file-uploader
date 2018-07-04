package com.selzlein.djeison.fileuploader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to File Uploader.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

}
