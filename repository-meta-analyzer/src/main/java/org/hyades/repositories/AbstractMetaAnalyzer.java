/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.hyades.repositories;

import org.hyades.commonnotification.NotificationConstants;
import org.hyades.commonnotification.NotificationGroup;
import org.hyades.commonnotification.NotificationScope;
import org.hyades.commonutil.HttpUtil;
import org.hyades.model.Component;
import org.hyades.model.Notification;
import org.hyades.model.NotificationLevel;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

/**
 * Base abstract class that all IMetaAnalyzer implementations should likely extend.
 *
 * @author Steve Springett
 * @since 3.1.0
 */

@ApplicationScoped
public abstract class AbstractMetaAnalyzer implements IMetaAnalyzer {

    @Inject
    @Named("httpClient")
    CloseableHttpClient httpClient;

    protected String baseUrl;

    protected String username;

    protected String password;

    /**
     * {@inheritDoc}
     */
    public void setRepositoryBaseUrl(String baseUrl) {
        baseUrl = StringUtils.trimToNull(baseUrl);
        if (baseUrl == null) {
            return;
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl;
    }

    public void setRepositoryUsernameAndPassword(String username, String password) {
        this.username = StringUtils.trimToNull(username);
        this.password = StringUtils.trimToNull(password);
    }

    protected void handleUnexpectedHttpResponse(final Logger logger, String url, final int statusCode, final String statusText, final Component component) {
        logger.debug("HTTP Status : " + statusCode + " " + statusText);
        logger.debug(" - RepositoryType URL : " + url);
        logger.debug(" - Package URL : " + component.getPurl().canonicalize());
        Notification.dispatch(new Notification()
                .scope(NotificationScope.SYSTEM)
                .group(NotificationGroup.REPOSITORY)
                .title(NotificationConstants.Title.REPO_ERROR)
                .content("An error occurred while communicating with an " + supportedRepositoryType().name() + " repository. URL: " + url + " HTTP Status: " + statusCode + ". Check log for details." )
                .level(NotificationLevel.ERROR)
        );
    }

    protected void handleRequestException(final Logger logger, final Exception e) {
        logger.error("Request failure", e);
        Notification.dispatch(new Notification()
                .scope(NotificationScope.SYSTEM)
                .group(NotificationGroup.REPOSITORY)
                .title(NotificationConstants.Title.REPO_ERROR)
                .content("An error occurred while communicating with an " + supportedRepositoryType().name() + " repository. Check log for details. " + e.getMessage())
                .level(NotificationLevel.ERROR)
        );
    }

    protected CloseableHttpResponse processHttpRequest(String url) throws IOException {
        final HttpUriRequest request = new HttpGet(url);
        request.addHeader("accept", "application/json");
        if (username != null || password != null) {
            request.addHeader("Authorization", HttpUtil.basicAuthHeaderValue(username, password));
        }
        return httpClient.execute(request);
    }

}
