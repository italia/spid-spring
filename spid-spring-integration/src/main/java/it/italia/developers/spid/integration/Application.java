/************************************************************************************
 * Copyright (c) 2011, 2017 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.italia.developers.spid.integration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author Gianluca Pindinelli
 *
 */
@Configuration
@ComponentScan(basePackages = { "it.italia.developers.spid.integration" })
@PropertySources({ @PropertySource("classpath:integration.properties"), @PropertySource(value = "file:${spid-spring-integration.properties.path}", ignoreResourceNotFound = true) })
public class Application {

}
