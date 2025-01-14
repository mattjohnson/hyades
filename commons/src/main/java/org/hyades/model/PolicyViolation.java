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
import java.util.UUID;

/**
 * Defines a Model class for defining a policy violation.
 *
 * @author Steve Springett
 * @since 4.0.0
 */

@RegisterForReflection
public class PolicyViolation implements Serializable {

    public enum Type {
        LICENSE,
        SECURITY,
        OPERATIONAL
    }

    private long id;

    private Type type;

    private Project project;

    private Component component;

    private PolicyCondition policyCondition;

    private Date timestamp;

    private String text;

    private ViolationAnalysis analysis;

    /**
     * The unique identifier of the object.
     */
    private UUID uuid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        this.project = component.getProject();
    }

    public Project getProject() {
        return project;
    }

    public PolicyCondition getPolicyCondition() {
        return policyCondition;
    }

    public void setPolicyCondition(PolicyCondition policyCondition) {
        this.policyCondition = policyCondition;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ViolationAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ViolationAnalysis analysis) {
        this.analysis = analysis;
    }
}


