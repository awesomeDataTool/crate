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

package org.cratedb.action.groupby;

import org.cratedb.action.groupby.aggregate.AggExpr;
import org.cratedb.action.groupby.aggregate.AggFunction;

import java.io.IOException;
import java.util.List;

/**
 * this simple handler does not check whether we need to to a field lookup or not
 *
 * Use this class if you have to iterate through all the docs nonetheless
 */
public class SimpleSQLGroupingAggregateHandler implements SQLGroupingAggregateHandler {

    /**
     * handle Aggregates and never mind if you could stop or have to continue
     * collecting for a special AggState
     *
     * @param row the GroupByRow to apply the looked up value to
     * @param aggregateExpressions the aggregate expressions found in the SQL-statement
     * @param aggFunctions the aggFunctions used in the current SQL-statement in correct order
     * @throws IOException
     */
    @Override
    public void handleAggregates(GroupByRow row,
            List<AggExpr> aggregateExpressions, AggFunction[] aggFunctions) throws IOException {
        for (int i = 0; i < aggFunctions.length; i++) {
            AggExpr aggExpr = aggregateExpressions.get(i);
            AggFunction function = aggFunctions[i];
            Object value = null;
            if (aggExpr.expression != null){
                value = aggExpr.expression.evaluate();
            }
            function.iterate(row.aggStates.get(i), value);
        }
    }
}
