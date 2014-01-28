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
package org.phenotips.ontology.internal.solr;

import org.phenotips.ontology.OntologyService;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.cache.Cache;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import org.apache.solr.client.solrj.SolrServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.when;

/**
 * Tests for the HPO implementation of the {@link org.phenotips.ontology.OntologyService}, {@link
 * org.phenotips.ontology.internal.solr.HumanPhenotypeOntology}.
 */
public class SolrOntologyServiceInitializerTest
{
    public int ontologyServiceResult;

    public Cache<OntologyTerm> cache;

    public SolrServer server;

    public OntologyService ontologyService;

    SolrOntologyServiceInitializer externalServicesAccess;

    @Rule
    public final MockitoComponentMockingRule<SolrOntologyServiceInitializer> mocker =
        new MockitoComponentMockingRule<SolrOntologyServiceInitializer>
            (SolrOntologyServiceInitializer.class);

    @Test
    public void testGetSolrLocation() throws ComponentLookupException
    {
        ConfigurationSource configuration = mocker
            .getInstance(ConfigurationSource.class, "xwikiproperties");
        when(configuration.getProperty("solr.remote.url", String.class))
            .thenReturn("http://localhost:8080/solr/wiki/");
        SolrOntologyServiceInitializer initializer = mocker.getComponentUnderTest();
        String location = initializer.getSolrLocation();
        Assert.assertTrue(location.equalsIgnoreCase("http://localhost:8080/solr/"));
    }
}
