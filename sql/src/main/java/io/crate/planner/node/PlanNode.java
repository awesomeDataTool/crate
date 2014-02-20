/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.planner.node;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import io.crate.planner.projection.Projection;
import org.cratedb.DataType;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PlanNode implements Streamable {

    private String id;
    protected List<Projection> projections = ImmutableList.of();
    protected List<DataType> outputTypes = ImmutableList.of();

    public PlanNode() {

    }

    public abstract Set<String> executionNodes();

    protected PlanNode(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean hasProjections() {
        return projections != null && projections.size() > 0;
    }

    public List<Projection> projections() {
        return projections;
    }

    public Optional<Projection> finalProjection() {
        if (projections.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(projections.get(projections.size()-1));
        }
    }

    public void projections(List<Projection> projections) {
        this.projections = projections;
    }

    public void outputTypes(List<DataType> outputTypes) {
        this.outputTypes = outputTypes;
    }

    public List<DataType> outputTypes() {
        return outputTypes;
    }

    public abstract <C, R> R accept(PlanVisitor<C, R> visitor, C context);

    @Override
    public void readFrom(StreamInput in) throws IOException {
        id = in.readString();

        int numCols = in.readVInt();
        if (numCols > 0) {
            outputTypes = new ArrayList<>(numCols);
            for (int i = 0; i < numCols; i++) {
                outputTypes.add(DataType.values()[in.readVInt()]);
            }
        }

        int numProjections = in.readVInt();
        if (numProjections > 0) {
            projections = new ArrayList<>(numProjections);
            for (int i = 0; i < numProjections; i++) {
                projections.add(Projection.fromStream(in));
            }
        }

    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(id);

        int numCols = outputTypes.size();
        out.writeVInt(numCols);
        for (int i = 0; i < numCols; i++) {
            out.writeVInt(outputTypes.get(i).ordinal());
        }

        if (hasProjections()) {
            out.writeVInt(projections.size());
            for (Projection p : projections) {
                Projection.toStream(p, out);
            }
        } else {
            out.writeVInt(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanNode node = (PlanNode) o;

        if (id != null ? !id.equals(node.id) : node.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
