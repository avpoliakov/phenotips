/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.boqa.script;

import org.phenotips.boqa.DiagnosisService;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by meatcar on 9/5/14.
 *
 * @version $Id$
 */
@Component
@Named("diagnosis")
@Singleton
public class DiagnosisScriptService implements ScriptService
{
    @Inject
    private DiagnosisService manager;

    /**
     * Get a list of suggest diagnosies given a list of present phenotypes. Each phenotype is represented as a String
     * in the format {@code <ontology prefix>:<term id>}, for example
     * {@code HP:0002066}
     *
     * @param phenotypes A List of String phenotypes observed in the patient
     * @return A list of suggested diagnosies
     */
    public List<String> get(List<String> phenotypes) {
        return this.manager.getDiagnosis(phenotypes);
    }

    /**
     *
     * @param phenotypes A list of phenotypes observed in the patient
     * @param limit a number of phenotypes to return
     * @return a list of diseases found
     */
    public List<String> get(List<String> phenotypes, int limit) {
        return this.get(phenotypes);
    }
}
