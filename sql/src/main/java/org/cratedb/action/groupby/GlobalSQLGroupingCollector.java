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

import org.cratedb.action.collect.CollectorContext;
import org.cratedb.action.groupby.aggregate.AggFunction;
import org.cratedb.action.groupby.key.GlobalRows;
import org.cratedb.action.groupby.key.Rows;
import org.cratedb.action.sql.ParsedStatement;

import java.util.Map;

/**
 * GroupingCollector that is used for global aggregation without group by
 */
public class GlobalSQLGroupingCollector extends SQLGroupingCollector {

    public GlobalSQLGroupingCollector(ParsedStatement parsedStatement,
                                      CollectorContext context,
                                      Map<String, AggFunction> aggFunctionMap,
                                      int numReducers) {
        super(parsedStatement, context, aggFunctionMap, numReducers);
        assert parsedStatement.isGlobalAggregate();
    }

    @Override
    protected Rows newRows() {
        return new GlobalRows(numReducers, parsedStatement);
    }
}
