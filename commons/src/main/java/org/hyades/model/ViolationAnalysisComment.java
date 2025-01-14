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
package org.hyades.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.Date;

/**
 * The ViolationAnalysisComment model provides zero or more comments for a human
 * auditing decision ({@link ViolationAnalysis}).
 *
 * @author Steve Springett
 * @since 4.0.0
 */

@RegisterForReflection
public class ViolationAnalysisComment implements Serializable {

    private long id;

    private ViolationAnalysis violationAnalysis;

    private Date timestamp;

    private String comment;

    private String commenter;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ViolationAnalysis getViolationAnalysis() {
        return violationAnalysis;
    }

    public void setViolationAnalysis(ViolationAnalysis violationAnalysis) {
        this.violationAnalysis = violationAnalysis;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }
}
