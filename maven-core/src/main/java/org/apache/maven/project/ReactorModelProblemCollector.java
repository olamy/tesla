package org.apache.maven.project;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelProblem;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblem.Severity;

/**
 * 
 */
class ReactorModelProblemCollector
    implements ModelProblemCollector
{

    private List<ModelProblem> problems;

    private Model model;

    public ReactorModelProblemCollector( List<ModelProblem> problems, Model model )
    {
        this.problems = problems;
        this.model = model;
    }

    public void add( Severity severity, String message, InputLocation location, Exception cause )
    {
        int line = -1;
        int column = -1;
        String source = null;
        String modelId = null;

        if ( location != null )
        {
            line = location.getLineNumber();
            column = location.getColumnNumber();
            if ( location.getSource() != null )
            {
                modelId = location.getSource().getModelId();
                source = location.getSource().getLocation();
            }
        }

        ModelProblem problem;
        if ( modelId != null )
        {
            problem = new DefaultModelProblem( message, severity, source, line, column, modelId, cause );
        }
        else
        {
            problem = new DefaultModelProblem( message, severity, model, line, column, cause );
        }
        problems.add( problem );
    }

}
